package org.joshdurbin

import com.google.inject.Inject
import groovy.util.logging.Slf4j

@Slf4j
class IncidentService {

  @Inject
  IncidentRepository repository

  void init() {
    repository.initialize()
  }

  def all() {
    repository.findAll()
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
