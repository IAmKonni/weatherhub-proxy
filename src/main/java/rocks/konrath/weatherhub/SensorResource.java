package rocks.konrath.weatherhub;

import java.util.Set;
import java.util.regex.Pattern;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/sensor")
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {

	/** Phone and sensor ids are plain alphanumeric strings. */
	static final Pattern ID_PATTERN = Pattern.compile("[A-Za-z0-9]{1,64}");

	@Inject
	WeatherhubClient client;

	@Inject
	SensorParser parser;

	@GET
	@Path("/all/{phoneId}")
	public Set<Sensor> getSensorDataWithGivenPhoneId(@PathParam("phoneId") String phoneId) {
		requireValidId(phoneId, "phoneId");
		return parser.parseAllSensorData(client.fetchOverviewPage(phoneId));
	}

	static void requireValidId(String value, String name) {
		if (value == null || !ID_PATTERN.matcher(value).matches()) {
			throw new BadRequestException("Invalid " + name);
		}
	}
}
