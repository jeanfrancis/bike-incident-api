import com.zaxxer.hikari.HikariConfig
import org.joshdurbin.Incident
import org.joshdurbin.IncidentModule
import org.joshdurbin.IncidentService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ratpack.form.Form
import ratpack.groovy.sql.SqlModule
import ratpack.groovy.template.TextTemplateModule
import ratpack.hikari.HikariModule
import ratpack.hystrix.HystrixMetricsEventStreamHandler
import ratpack.hystrix.HystrixModule
import ratpack.jackson.Jackson
import ratpack.jackson.JacksonModule
import ratpack.rx.RxRatpack
import ratpack.server.Service
import ratpack.server.StartEvent

import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack

final Logger log = LoggerFactory.getLogger(Ratpack.class)

ratpack {
  bindings {
    add TextTemplateModule

    add(HikariModule) { HikariConfig config ->

      config.driverClassName = 'org.postgresql.Driver'

      def uri = new URI(System.env.DATABASE_URL ?: "postgres://test:test@localhost/bike-incident-api")

      def url = "jdbc:postgresql://${uri.host}${uri.path}"
      def username = uri.userInfo.split(":")[0]
      def password = ''
      if (!uri.userInfo.endsWith(':')) {
        password = uri.userInfo.split(":")[1]
      }

      config.jdbcUrl = url
      config.username = username
      config.password = password ?: ''

    }

    add new SqlModule()
    add new JacksonModule()
    add new IncidentModule()
    add new HystrixModule().sse()

    bindInstance Service, new Service() {

      @Override
      void onStart(StartEvent event) throws Exception {

        log.info "Initializing RX"
        RxRatpack.initialize()
        event.registry.get(IncidentService).init()
      }
    }
  }

  handlers { IncidentService incidentService ->
    get {
      render groovyTemplate("index.html", title: "Bike Incident Registry API")
    }

    handler("incidents") {

      byMethod {

        get {
          incidentService.all().toList().subscribe { List<Incident> incidents ->
            render Jackson.json(incidents)
          }
        }

        post {

          Form form = parse(Form)

          incidentService.create(form.description ?: 'Bahhhhh. No description entered.'

          ).subscribe() { Long id ->
            redirect "/incidents/${id}"
          }
        }
      }
    }

    handler("incidents/:id") {

      byMethod {

        get {

          Long id = pathTokens.id as Long

          incidentService.get(id).subscribe { Incident incident ->
            render Jackson.json(incident)
          }
        }

        put {

          Long id = pathTokens.id as Long

          incidentService.get(id).subscribe { Incident incident ->

            if (!incident) {
              clientError(404)

            } else {

              Form form = parse(Form)
              incident.description = form.description

              incidentService.update(incident).subscribe() { Long updatedID ->
                redirect "/incidents/${updatedID}"
              }
            }

          }
        }

        delete {

          Long id = pathTokens.id as Long

          incidentService.delete(id).subscribe {

            render Jackson.json('success')
          }
        }
      }
    }

    get("hystrix.stream", new HystrixMetricsEventStreamHandler())

    assets "public"
  }
}
