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

  Observable<List<Incident>> all() {
    repository.findAll().map {
      new Incident(id: it.id, createAt: it.createAt, description: it.description)
    }.toList()
  }

  def get(Long id) {
    repository.get(id)
  }

  def create(String description) {
    Incident incident = new Incident(createAt: new Date(), description: description)
    repository.insert(incident)
  }

  def update(Incident incident) {
    repository.update(incident)
  }

  def delete(long id) {
    repository.delete(id)
  }
}
