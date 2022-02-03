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

The <phoneid> can be seen in the WeatherHub / Mobile-Alerts app on your smartphone.
