package org.joshdurbin.domain

import groovy.transform.Canonical
import org.joshdurbin.domain.enums.Direction
import org.joshdurbin.domain.enums.Event
import org.joshdurbin.domain.enums.EventLocation
import org.joshdurbin.domain.enums.IncidentType
import org.joshdurbin.domain.enums.Lighting
import org.joshdurbin.domain.enums.Maneuver
import org.joshdurbin.domain.enums.SurfaceCondition
import org.joshdurbin.domain.enums.TrafficControl
import org.joshdurbin.domain.enums.VehicleSpecification
import org.joshdurbin.domain.enums.VehicleType
import org.joshdurbin.domain.enums.Weather

@Canonical
class Incident {

  Long id
  Date insertionDate
  Long latitude
  Long longitude
  String formattedAddress
  Date eventDate
  Event event
  EventLocation eventLocation
  Integer fatalities
  Integer injuries
  Weather weather
  Lighting lighting
  IncidentType incidentType
  SurfaceCondition surfaceCondition
  TrafficControl trafficControl
  VehicleType vehicleType
  VehicleSpecification vehicleSpecification
  Direction vehicleDirection
  Maneuver vehicleManeuver
}
