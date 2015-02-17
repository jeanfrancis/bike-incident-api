package org.joshdurbin

import com.google.inject.Inject
import groovy.util.logging.Slf4j
import rx.Observable

@Slf4j
class IncidentService {

  @Inject
  IncidentRepository repository

  void init() {
    repository.initialize()
  }

  Observable<Incident> all() {
    repository.findAll().map {
      new Incident(id: it.id, createAt: it.createAt, description: it.description)
    }
  }

  Observable<Incident> get(Long id) {
    repository.getByID(id).map {
      new Incident(id: it.id, createAt: it.createAt, description: it.description)
    }
  }

  Observable<Long> create(String description) {
    Incident incident = new Incident(createAt: new Date(), description: description)
    repository.insert(incident).map {

      it?.first()?.first()
    }
  }

  Observable<Long> update(Incident incident) {
    repository.update(incident).map { it }
  }

  Observable<Void> delete(Long id) {
    repository.deleteByID(id)
  }
}
