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

import static ratpack.rx.RxRatpack.observe
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
      HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("findAll"))) {

      @Override
      protected Observable<GroovyRowResult> run() {
        observeEach(execControl.blocking {
          sql.rows("SELECT id, createAt, description FROM incident ORDER BY createAt")
        })
      }

      @Override
      protected String getCacheKey() {
        return "incidentrepo-findall"
      }
    }.toObservable()
  }

  Observable<GroovyRowResult> getByID(Long id) {

    return new HystrixObservableCommand<GroovyRowResult>(
      HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("getByID"))) {

      @Override
      protected Observable<GroovyRowResult> run() {
        observe(execControl.blocking {
          sql.firstRow("SELECT id, createAt, description FROM incident WHERE id=$id")
        })
      }

      @Override
      protected String getCacheKey() {
        return "incidentrepo-getbyid"
      }
    }.toObservable().single()
  }

  Observable<GroovyRowResult> update(Incident incident) {

    return new HystrixObservableCommand<GroovyRowResult>(
      HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("update"))) {

      @Override
      protected Observable<GroovyRowResult> run() {
        observe(execControl.blocking {
          sql.executeUpdate("UPDATE incident set description=$incident.description WHERE id=$incident.id")
        })
      }

    }.toObservable()
  }

  Observable<GroovyRowResult> insert(Incident incident) {

    return new HystrixObservableCommand<GroovyRowResult>(
      HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("insert"))) {

      @Override
      protected Observable<GroovyRowResult> run() {
        observe(execControl.blocking {
          sql.executeInsert("INSERT INTO incident (createAt, description) VALUES (${new Timestamp(incident.createAt.getTime())}, $incident.description)")
        })
      }

    }.toObservable()
  }

  Observable<Void> deleteByID(Long id) {

    return new HystrixObservableCommand<Void>(
      HystrixObservableCommand.Setter.withGroupKey(hystrixCommandGroupKey).andCommandKey(HystrixCommandKey.Factory.asKey("deleteByID"))) {

      @Override
      protected Observable<Void> run() {
        observe(execControl.blocking {
          sql.executeUpdate("DELETE FROM incident WHERE id=$id")
        })
      }

    }.toObservable()
  }
}
