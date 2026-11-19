package rocks.konrath.weatherhub;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SensorResourceTest {

	@BeforeEach
	void stubPortal() {
		QuarkusMock.installMockForType(new WeatherhubClient() {
			@Override
			public String fetchOverviewPage(String phoneId) {
				return TestResources.read("overview.html");
			}

			@Override
			public String fetchHistoryPage(String phoneId, String sensorId) {
				return TestResources.read("history.html");
			}
		}, WeatherhubClient.class);
	}

	@Test
	void returnsAllSensorsAsJson() {
		given()
				.when().get("/sensor/all/123456789012")
				.then()
				.statusCode(200)
				.body("$", hasSize(4))
				.body("id", containsInAnyOrder("0305E29CC32E", "0320B7FB6650", "085148DB8CA0", "115CCE323FAD"))
				.body("find { it.id == '0305E29CC32E' }.time", is("2022-09-23T09:24:26"))
				.body("find { it.id == '0305E29CC32E' }.temperature", is(-6.5f))
				.body("find { it.id == '0305E29CC32E' }.sensorClass", is("rocks.konrath.weatherhub.ThermalSensor"))
				.body("find { it.id == '085148DB8CA0' }.rainfall", is(0.5f))
				.body("find { it.id == '115CCE323FAD' }.humidity4", is(49))
				.body("find { it.id == '115CCE323FAD' }.sensorClass",
						is("rocks.konrath.weatherhub.ThermalMultiSensor"));
	}

	@Test
	void returnsSensorHistoryAsJson() {
		given()
				.when().get("/sensorHistory/123456789012/0305E29CC32E")
				.then()
				.statusCode(200)
				.body("id", is("0305E29CC32E"))
				.body("label", is("Außen"))
				.body("dataPointStreams.name", contains("Temperatur", "Luftfeuchte"))
				.body("dataPointStreams[0].values[0].time", is("2022-09-23T09:24:26"))
				.body("dataPointStreams[0].values[0].value", is(-6.5f));
	}

	@Test
	void rejectsInvalidIds() {
		given()
				.when().get("/sensor/all/abc%3Bdef")
				.then()
				.statusCode(400);
	}

	@Test
	void healthEndpointIsUp() {
		given()
				.when().get("/q/health/live")
				.then()
				.statusCode(200)
				.body("status", is("UP"));
	}
}
