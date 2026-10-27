package rocks.konrath.weatherhub;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

@Path("/sensorHistory")
public class SensorHistoryResource {

	@Inject
	Logger log;
	
	@Inject
	WeatherhubClient client;
	
	@GET
	@Path("/{phoneId}/{sensorId}")
	@Produces(MediaType.APPLICATION_JSON)
	public SensorHistory getSensorHistoryDataWithGivenPhoneIdAndSensorId(@PathParam("phoneId") String phoneId, @PathParam("sensorId") String sensorId) {
		return client.getSensorHistoryDataWithGivenPhoneIdAndSensorId(phoneId, sensorId);
	}

}
