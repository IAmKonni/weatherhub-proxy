package rocks.konrath.weatherhub;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/sensor")
public class SensorResource {

	@Inject
	Logger log;
	
	@Inject
	WeatherhubClient client;
	
	@GET
	@Path("/all/{phoneId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<Sensor> getSensorDataWithGivenPhoneId(@PathParam String phoneId) {
		return client.getSensorDataWithGivenPhoneId(phoneId);
	}
}