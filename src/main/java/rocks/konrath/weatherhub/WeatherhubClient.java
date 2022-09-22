package rocks.konrath.weatherhub;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

@Singleton
public class WeatherhubClient {
	
	@Inject
	Logger log;
	
	@Inject
	SensorParser parser;

	public Set<Sensor> getSensorDataWithGivenPhoneId(String phoneId) {
		String htmlBody = requestData(phoneId);
		
		return parser.parseAllSensorData(htmlBody);
	}

	private String requestData(String phoneId) {
		HttpClient client = HttpClient.newHttpClient();

		BodyPublisher body = BodyPublishers.ofString("phoneid=" + phoneId);
		HttpRequest request = HttpRequest.newBuilder(URI.create("https://measurements.mobile-alerts.eu/"))
				.header("accept", MediaType.TEXT_HTML)
				.header("accept-encoding", "deflate, br")
				.header("cache-control", "max-age=0")
				.header("content-type", MediaType.APPLICATION_FORM_URLENCODED)
				.header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36")
				.header("cookie", "PhoneID=" + phoneId)
				.POST(body)
				.build();
		try {
			HttpResponse<String> response =
			  client.send(request, BodyHandlers.ofString());
			return response.body();
		} catch (Exception e) {
			log.error(e);
			return "";			
		}
	}
}
