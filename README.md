# weatherhub-proxy

weatherhub-proxy is a quarkus-based implementation for reading Weatherhub / Mobile Alerts sensors from the web portal https://measurements.mobile-alerts.eu and exposing them via a REST interface in JSON format.

## Installation / Setup

The REST endpoint is exposed on port 8080. There is nothing else to configure.

This is am example docker-compose file that can be used.

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
    "time": "2022-02-03T23:14:38",
    "temperature": 3.8,
    "humidity": 93
  },
  {
    "id": "0320B7FB6650",
    "label": "Garage",
    "time": "2022-02-03T21:28:22",
    "temperature": 5.5,
    "humidity": 80
  },
  {
    "id": "1236FB3D35A1",
    "label": "Schlafzimmer",
    "time": "2022-02-03T23:15:02",
    "temperature": 22.1,
    "humidity": 45
  },
  {
    "id": "042167F6D4AB",
    "label": "Waschküche",
    "time": "2022-02-03T23:15:39",
    "temperature": 23.2,
    "humidity": 41
  },
  {
    "id": "115CCE323FAD",
    "label": "Wohnen",
    "time": "2022-02-03T23:19:05",
    "temperature": 22.3,
    "humidity": 47,
    "temperature2": 22.4,
    "temperature3": 22.6,
    "temperature4": 20.9,
    "humidity2": 39,
    "humidity3": 58,
    "humidity4": 43
  }
]
```
