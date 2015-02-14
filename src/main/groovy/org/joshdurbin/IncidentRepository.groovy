package org.joshdurbin

import com.google.inject.Inject
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import com.netflix.hystrix.HystrixObservableCommand
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.flywaydb.core.Flyway
import ratpack.exec.ExecControl
import rx.Observable

import java.sql.Timestamp

import static ratpack.rx.RxRatpack.observeEach

@Slf4j
class IncidentRepository {

  private final Sql sql
  private final ExecControl execControl
  private static final HystrixCommandGroupKey hystrixCommandGroupKey = HystrixCommandGroupKey.Factory.asKey("sql-incidentrepo")

  void initialize() {
    log.info "Creating tables"

    def flyway = new Flyway()
    flyway.dataSource = sql.dataSource
    flyway.migrate()
  }

  @Inject
  public IncidentRepository(Sql sql, ExecControl execControl) {
    this.sql = sql
    this.execControl = execControl
  }

  Observable<GroovyRowResult> findAll() {

    return new HystrixObservableCommand<GroovyRowResult>(
      HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("getAll"))) {

      @Override
      protected rx.Observable<GroovyRowResult> run() {
        observeEach(execControl.blocking {
          sql.rows("SELECT id, createAt, description FROM incident ORDER BY createAt")
        })
      }

      @Override
      protected String getCacheKey() {
        return "db-incidentrepo-all"
      }
    }.toObservable()
  }

  Observable<Incident> get(Long id) {
    Observable.from(sql.rows("SELECT id, createAt, description FROM incident WHERE id=$id")).map {
      new Incident(id: it.id, createAt: it.createAt, description: it.description)
    }.single()
  }

  Observable<Long> update(Incident incident) {
    sql.executeUpdate("UPDATE incident set description=$incident.description WHERE id=$incident.id")

    Observable.just(incident.id)
  }

  Observable<Long> insert(Incident incident) {

    Observable.from(sql.executeInsert("INSERT INTO incident (createAt, description) VALUES (${new Timestamp(incident.createAt.getTime())}, $incident.description)")).map {

      it.first() as Long
    }.single()

  }

  def delete(Long id) {
    sql.execute("DELETE FROM incident WHERE id=$id")
  }
}
