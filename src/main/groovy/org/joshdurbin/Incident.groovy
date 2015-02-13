package org.joshdurbin

import groovy.transform.Canonical

@Canonical
class Incident {

  Long id
  Date createAt
  String description
}
