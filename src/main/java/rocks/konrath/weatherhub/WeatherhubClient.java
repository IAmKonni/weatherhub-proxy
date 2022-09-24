package rocks.konrath.weatherhub;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
	
	static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public Set<Sensor> getSensorDataWithGivenPhoneId(String phoneId) {
		String htmlBody = requestSensorDataFromOverviewPage(phoneId);
		
		return parser.parseAllSensorData(htmlBody);
	}

	private String requestSensorDataFromOverviewPage(String phoneId) {
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
	
	private String requestSensorDataFromHistoryPage(String phoneId, String sensorId) {
		HttpClient client = HttpClient.newHttpClient();
		
		Timestamp timestampNow = new Timestamp(System.currentTimeMillis());
		timestampNow.setNanos(0);
		long timestampDifferenceTwoDaysAgo = 60 * 60 * 24 * 2;
		Timestamp timestampTwoDaysAgo = new Timestamp(timestampNow.getTime() - timestampDifferenceTwoDaysAgo);
		
		long timestampNowInSeconds = timestampNow.getTime() / 1000;
		long timestampToDaysAgoInSeconds = timestampNow.getTime() / 1000;
		
		String formattedDateFrom = sdf.format(timestampTwoDaysAgo);
		String formattedDateTo = sdf.format(timestampNow);
		
		BodyPublisher body = BodyPublishers.ofString("deviceid=" + sensorId +  "&vendorid=1af98e8c-2265-4887-b211-3c04623d27c7&appbundle=eu.mobile_alerts.weatherhub&fromepoch=" + timestampNowInSeconds + "&toepoch=" + timestampToDaysAgoInSeconds + "&from=" + formattedDateFrom + "+00%3A00&to=" + formattedDateTo + "+00%3A00&area=week");
		HttpRequest request = HttpRequest.newBuilder(URI.create("https://measurements.mobile-alerts.eu/Home/MeasurementDetails"))
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

	public SensorHistory getSensorHistoryDataWithGivenPhoneIdAndSensorId(String phoneId, String sensorId) {
        String htmlBody = requestSensorDataFromHistoryPage(phoneId, sensorId);
		
		return parser.parseSensorHistoryData(htmlBody);
	}
}
