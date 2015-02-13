package org.joshdurbin

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class IncidentModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(IncidentRepository).in(Scopes.SINGLETON)
    bind(IncidentService).in(Scopes.SINGLETON)
  }

}
