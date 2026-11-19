package rocks.konrath.weatherhub;

import static rocks.konrath.weatherhub.SensorResource.requireValidId;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/sensorHistory")
@Produces(MediaType.APPLICATION_JSON)
public class SensorHistoryResource {

	@Inject
	WeatherhubClient client;

	@Inject
	SensorParser parser;

	@GET
	@Path("/{phoneId}/{sensorId}")
	public SensorHistory getSensorHistoryDataWithGivenPhoneIdAndSensorId(@PathParam("phoneId") String phoneId,
			@PathParam("sensorId") String sensorId) {
		requireValidId(phoneId, "phoneId");
		requireValidId(sensorId, "sensorId");
		String html = client.fetchHistoryPage(phoneId, sensorId);
		return parser.parseSensorHistoryData(html, new SensorHistory(sensorId));
	}
}
