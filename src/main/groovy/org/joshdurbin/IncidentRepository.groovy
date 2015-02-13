package org.joshdurbin

import com.google.inject.Inject
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.flywaydb.core.Flyway
import rx.Observable

import java.sql.Timestamp

@Slf4j
class IncidentRepository {

  @Inject
  private Sql sql

  void initialize() {
    log.info "Creating tables"

    def flyway = new Flyway()
    flyway.dataSource = sql.dataSource
    flyway.migrate()
  }

  Observable<List<Incident>> findAll() {
    Observable.from(sql.rows("SELECT id, createAt, description FROM incident ORDER BY createAt")).map {
      new Incident(id: it.id, createAt: it.createAt, description: it.description)
    }.toList()
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
    Observable.from(sql.executeInsert("INSERT INTO incident (createAt, description) VALUES (${new Timestamp(incident.createAt.getTime())}, $incident.description) RETURNING id")).map {
      it.id as Long
    }.single()
  }

  def delete(Long id) {
    sql.execute("DELETE FROM incident WHERE id=$id")
  }
}
