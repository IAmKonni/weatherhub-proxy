# weatherhub-proxy

[![Maven and Docker build](https://github.com/IAmKonni/weatherhub-proxy/actions/workflows/maven_and_docker.yml/badge.svg)](https://github.com/IAmKonni/weatherhub-proxy/actions/workflows/maven_and_docker.yml)

weatherhub-proxy is a Quarkus-based implementation for reading Weatherhub / Mobile Alerts sensors from the web portal https://measurements.mobile-alerts.eu and exposing them via a REST interface in JSON format.

Supported sensors:

* simple temperature/humidity sensors
* temperature/humidity "sensors" with multiple sensors
* rainfall sensors

## Installation / Setup

The REST endpoint is exposed on port 8080. There is nothing else to configure.

Optional environment variables:

| Variable | Default | Description |
|---|---|---|
| `WEATHERHUB_BASE_URL` | `https://measurements.mobile-alerts.eu/` | URL of the Mobile Alerts web portal |
| `WEATHERHUB_TIMEOUT` | `PT15S` | Timeout for requests to the portal (ISO-8601 duration) |
| `QUARKUS_LOG_CATEGORY__ROCKS_KONRATH__LEVEL` | `INFO` | Set to `DEBUG` to log requests and parser details |

Health and metrics endpoints: `/q/health`, `/q/health/live`, `/q/health/ready`, `/q/metrics`.

This is an example docker-compose file that can be used.

```docker-compose.yml
services:
  weatherhub-proxy:
    image: iamkonni/weatherhub-proxy:latest
    container_name: weatherhub-proxy
    environment:
      - TZ=Europe/Berlin
    restart: unless-stopped
    ports:
      - "8080:8080"
```

You can use this shell/cli command as well

```shell
docker run -d --name weatherhub-proxy --restart unless-stopped -p 8080:8080 iamkonni/weatherhub-proxy:latest
```

## Querying the sensor data

Just use HTTP GET requests to the following endpoint:

```endpoint
http(s)://host:8080/sensor/all/<phoneid>
```

The `<phoneid>` can be read out in the WeatherHub / Mobile-Alerts app on your smartphone.
  
The response should look like:
  
```application/json
[
  {
    "id": "0305E29CC32E",
    "label": "Außen",
    "time": "2022-09-23T09:24:26",
    "temperature": 6.5,
    "humidity": 87,
    "sensorClass": "rocks.konrath.weatherhub.ThermalSensor"
  },
  {
    "id": "0320B7FB6650",
    "label": "Garage",
    "time": "2022-09-23T09:25:53",
    "temperature": 13.1,
    "humidity": 64,
    "sensorClass": "rocks.konrath.weatherhub.ThermalSensor"
  },
  {
    "id": "085148DB8CA0",
    "label": "Regensensor",
    "time": "2022-09-22T14:13:16",
    "rainfall": 0.5,
    "sensorClass": "rocks.konrath.weatherhub.RainSensor"
  },
  {
    "id": "1236FB3D35A1",
    "label": "Schlafzimmer",
    "time": "2022-09-23T09:19:08",
    "temperature": 21.4,
    "humidity": 54,
    "sensorClass": "rocks.konrath.weatherhub.ThermalSensor"
  },
  {
    "id": "042167F6D4AB",
    "label": "Waschküche",
    "time": "2022-09-23T09:22:06",
    "temperature": 23.4,
    "humidity": 52,
    "sensorClass": "rocks.konrath.weatherhub.ThermalSensor"
  },
  {
    "id": "115CCE323FAD",
    "label": "Wohnen",
    "time": "2022-09-23T09:23:45",
    "temperature": 22.8,
    "humidity": 60,
    "temperature2": 23.1,
    "temperature3": 22.7,
    "temperature4": 21.7,
    "humidity2": 46,
    "humidity3": 59,
    "humidity4": 49,
    "sensorClass": "rocks.konrath.weatherhub.ThermalMultiSensor"
  }
]
```

Phone and sensor IDs must be alphanumeric; anything else is rejected with HTTP 400.
If the portal cannot be reached, an empty result is returned.

## Querying the history of a single sensor

```endpoint
http(s)://host:8080/sensorHistory/<phoneid>/<sensorid>
```

Returns the measurements of the last five days, grouped by measurement:

```application/json
{
  "id": "0305E29CC32E",
  "label": "Außen",
  "dataPointStreams": [
    {
      "name": "Temperatur",
      "unit": "C",
      "values": [
        { "time": "2022-09-23T09:24:26", "value": 6.5 }
      ]
    }
  ]
}
```

## Building

Requirements: Java 21 and Docker. Maven is provided via the wrapper.

```shell
# run tests and build the JVM application
./mvnw verify

# build the native executable inside a container (no local GraalVM needed)
./mvnw package -Dnative -Dquarkus.native.container-build=true
docker build -f src/main/docker/Dockerfile.native-distroless -t weatherhub-proxy .
```

The GitHub Action runs the tests on every push and pull request, builds the native image
and pushes it to Docker Hub on pushes to `master`. It needs the repository secrets
`DOCKER_USER` and `DOCKER_PASSWORD` (a Docker Hub access token).
