# weatherhub-proxy

weatherhub-proxy is a quarkus-based implementation for reading Weatherhub / Mobile Alerts sensors from the web portal https://measurements.mobile-alerts.eu and exposing them via a REST interface in JSON format.

Supported are up to day:

* simple temperature/humidity sensors
* temperature/humidity "sensors" with multiple sensors
* rainfall sensors

## Installation / Setup

The REST endpoint is exposed on port 8080. There is nothing else to configure.

This is an example docker-compose file that can be used.

```docker-compose.yml
version: "3.7"

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
docker run -d --name weatherhub-proxy --restart unless-stopped -p 8088:8088 iamkonni/weatherhub-proxy:latest
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
