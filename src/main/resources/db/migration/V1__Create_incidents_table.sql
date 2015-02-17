CREATE TABLE incident (

  id SERIAL PRIMARY KEY,
  insertionDate TIMESTAMP,
  latitude INTEGER,
  longitude INTEGER,
  eventDate TIMESTAMP,
  event TEXT,
  eventLocation TEXT,
  fatalities INTEGER,
  injuries INTEGER,
  weather TEXT,
  lighting TEXT,
  incidentType TEXT,
  surfaceCondition TEXT,
  trafficControl TEXT,
  vehicleType TEXT,
  vehicleSpecification TEXT,
  vehicleDirection TEXT,
  vehicleManeuver TEXT

)