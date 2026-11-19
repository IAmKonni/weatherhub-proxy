package rocks.konrath.weatherhub;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Fetches the raw HTML pages from the Mobile Alerts web portal.
 * <p>
 * Network or HTTP errors are logged and result in an empty page, so the REST API keeps
 * answering with an empty result instead of an error (unchanged API behaviour).
 */
@ApplicationScoped
public class WeatherhubClient {

	private static final Logger LOG = Logger.getLogger(WeatherhubClient.class);

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36";
	private static final String VENDOR_ID = "1af98e8c-2265-4887-b211-3c04623d27c7";
	private static final String APP_BUNDLE = "eu.mobile_alerts.weatherhub";
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final int HISTORY_DAYS = 5;

	@ConfigProperty(name = "weatherhub.base-url", defaultValue = "https://measurements.mobile-alerts.eu/")
	URI baseUrl;

	@ConfigProperty(name = "weatherhub.timeout", defaultValue = "PT15S")
	Duration timeout;

	Clock clock = Clock.systemDefaultZone();

	private HttpClient httpClient;

	@PostConstruct
	void init() {
		httpClient = HttpClient.newBuilder()
				.connectTimeout(timeout)
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build();
	}

	/** HTML of the overview page listing all sensors of the given phone id. */
	public String fetchOverviewPage(String phoneId) {
		return post(baseUrl, phoneId, Map.of("phoneid", phoneId));
	}

	/** HTML of the measurement detail page of one sensor, covering the last days. */
	public String fetchHistoryPage(String phoneId, String sensorId) {
		ZonedDateTime to = LocalDate.now(clock).plusDays(1).atStartOfDay(clock.getZone());
		ZonedDateTime from = to.minusDays(HISTORY_DAYS);

		Map<String, String> form = new LinkedHashMap<>();
		form.put("deviceid", sensorId);
		form.put("vendorid", VENDOR_ID);
		form.put("appbundle", APP_BUNDLE);
		form.put("fromepoch", String.valueOf(from.toEpochSecond()));
		form.put("toepoch", String.valueOf(to.toEpochSecond()));
		form.put("from", DATE_FORMAT.format(from) + " 00:00");
		form.put("to", DATE_FORMAT.format(to) + " 00:00");
		form.put("area", "week");

		return post(baseUrl.resolve("Home/MeasurementDetails"), phoneId, form);
	}

	private String post(URI uri, String phoneId, Map<String, String> form) {
		HttpRequest request = HttpRequest.newBuilder(uri)
				.timeout(timeout)
				.header("Accept", "text/html")
				.header("Cache-Control", "max-age=0")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("User-Agent", USER_AGENT)
				.header("Cookie", "PhoneID=" + encode(phoneId))
				.POST(HttpRequest.BodyPublishers.ofString(formEncode(form)))
				.build();
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
			if (response.statusCode() / 100 != 2) {
				LOG.errorf("Weatherhub portal answered HTTP %d for %s", response.statusCode(), uri);
				return "";
			}
			return response.body();
		} catch (IOException e) {
			LOG.errorf(e, "Request to %s failed", uri);
			return "";
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOG.errorf("Request to %s was interrupted", uri);
			return "";
		}
	}

	private static String formEncode(Map<String, String> form) {
		return form.entrySet().stream()
				.map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
				.collect(Collectors.joining("&"));
	}

	private static String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}
}
