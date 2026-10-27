package rocks.konrath.weatherhub;

import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

@Path("/sensor")
public class SensorResource {

	@Inject
	Logger log;
	
	@Inject
	WeatherhubClient client;
	
	@GET
	@Path("/all/{phoneId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Sensor> getSensorDataWithGivenPhoneId(@PathParam("phoneId") String phoneId) {
		return client.getSensorDataWithGivenPhoneId(phoneId);
	}
}
