package rocks.konrath.weatherhub;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/sensorHistory")
public class SensorHistoryResource {

	@Inject
	Logger log;
	
	@Inject
	WeatherhubClient client;
	
	@GET
	@Path("/{phoneId}/{sensorId}")
	@Produces(MediaType.APPLICATION_JSON)
	public SensorHistory getSensorHistoryDataWithGivenPhoneIdAndSensorId(@PathParam String phoneId, @PathParam String sensorId) {
		return client.getSensorHistoryDataWithGivenPhoneIdAndSensorId(phoneId, sensorId);
	}

}