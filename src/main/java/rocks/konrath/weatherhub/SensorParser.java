package rocks.konrath.weatherhub;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Parses the HTML pages of measurements.mobile-alerts.eu into sensor objects.
 * Stateless and thread-safe.
 */
@ApplicationScoped
public class SensorParser {

	private static final Logger LOG = Logger.getLogger(SensorParser.class);

	static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

	/** Common prefix of every sensor tile: label, id and timestamp. */
	private static final String HEADER = "(\\S+) ID (\\S+) Zeitpunkt (.+?) ";
	private static final String TEMPERATURE = "(\\S+) ?°?C?";
	private static final String HUMIDITY = "([^\\s%]+)%?";

	private static final Pattern MULTI_SENSOR = Pattern.compile(HEADER
			+ "Temp In " + TEMPERATURE + " Hum In " + HUMIDITY
			+ " Temp 1 " + TEMPERATURE + " Hum 1 " + HUMIDITY
			+ " Temp 2 " + TEMPERATURE + " Hum 2 " + HUMIDITY
			+ " Temp 3 " + TEMPERATURE + " Hum 3 " + HUMIDITY);
	private static final Pattern THERMAL_SENSOR = Pattern.compile(HEADER
			+ "Temperatur " + TEMPERATURE + " Luftfeuchte " + HUMIDITY);
	private static final Pattern RAIN_SENSOR = Pattern.compile(HEADER
			+ "Regen (\\S+) ?(?:mm)?");

	private static final Pattern HISTORY_TITLE = Pattern.compile("(\\S+) Messungen");
	private static final Pattern VALUE_WITH_UNIT = Pattern.compile("(-?\\d+(?:[.,]\\d+)?) ?(.*)");

	/**
	 * Parses all sensor tiles ({@code div.sensor}) of the overview page.
	 */
	public Set<Sensor> parseAllSensorData(String html) {
		Set<Sensor> result = new LinkedHashSet<>();
		Document document = Jsoup.parse(html);

		for (Element tile : document.select("div.sensor")) {
			String text = tile.text();
			Optional<Sensor> sensor = parseSingleSensorData(text);
			if (sensor.isPresent() && !sensor.get().getId().isBlank()) {
				result.add(sensor.get());
				LOG.debug(sensor.get());
			} else {
				LOG.debugf("Unsupported sensor tile: %s", text);
			}
		}
		return result;
	}

	/**
	 * Parses the plain text of a single sensor tile.
	 */
	public Optional<Sensor> parseSingleSensorData(String text) {
		return parseMultiSensor(text)
				.or(() -> parseThermalSensor(text))
				.or(() -> parseRainSensor(text));
	}

	private Optional<Sensor> parseMultiSensor(String text) {
		Matcher m = MULTI_SENSOR.matcher(text);
		if (!m.find()) {
			return Optional.empty();
		}
		ThermalMultiSensor sensor = new ThermalMultiSensor();
		if (!fillHeader(sensor, m)) {
			return Optional.empty();
		}
		for (int channel = 1; channel <= 4; channel++) {
			int group = 2 + channel * 2;
			sensor.setChannel(channel,
					parseFloat(m.group(group), sensor, "temperature " + channel),
					parseInt(m.group(group + 1), sensor, "humidity " + channel));
		}
		return Optional.of(sensor);
	}

	private Optional<Sensor> parseThermalSensor(String text) {
		Matcher m = THERMAL_SENSOR.matcher(text);
		if (!m.find()) {
			return Optional.empty();
		}
		ThermalSensor sensor = new ThermalSensor();
		if (!fillHeader(sensor, m)) {
			return Optional.empty();
		}
		sensor.setTemperature(parseFloat(m.group(4), sensor, "temperature"));
		sensor.setHumidity(parseInt(m.group(5), sensor, "humidity"));
		return Optional.of(sensor);
	}

	private Optional<Sensor> parseRainSensor(String text) {
		Matcher m = RAIN_SENSOR.matcher(text);
		if (!m.find()) {
			return Optional.empty();
		}
		RainSensor sensor = new RainSensor();
		if (!fillHeader(sensor, m)) {
			return Optional.empty();
		}
		sensor.setRainfall(parseFloat(m.group(4), sensor, "rainfall"));
		return Optional.of(sensor);
	}

	private static boolean fillHeader(Sensor sensor, Matcher m) {
		sensor.setLabel(m.group(1));
		sensor.setId(m.group(2));
		try {
			sensor.setTime(LocalDateTime.parse(m.group(3), TIMESTAMP_FORMAT));
			return true;
		} catch (DateTimeParseException e) {
			LOG.warnf("Sensor %s (%s) has an unparsable timestamp: %s", sensor.getId(), sensor.getLabel(), m.group(3));
			return false;
		}
	}

	/** Parses a German decimal number; sensors without data ("---") yield 0. */
	private static float parseFloat(String raw, Sensor sensor, String what) {
		try {
			return Float.parseFloat(raw.replace(',', '.'));
		} catch (NumberFormatException e) {
			LOG.warnf("Sensor %s (%s) delivers no %s: %s", sensor.getId(), sensor.getLabel(), what, raw);
			return 0;
		}
	}

	private static int parseInt(String raw, Sensor sensor, String what) {
		try {
			return Integer.parseInt(raw);
		} catch (NumberFormatException e) {
			LOG.warnf("Sensor %s (%s) delivers no %s: %s", sensor.getId(), sensor.getLabel(), what, raw);
			return 0;
		}
	}

	/**
	 * Parses the measurement detail page ({@code /Home/MeasurementDetails}) into the given history.
	 */
	public SensorHistory parseSensorHistoryData(String html, SensorHistory history) {
		Document document = Jsoup.parse(html);

		history.setLabel(firstGroup(HISTORY_TITLE, document.select("h3").text()).orElse(""));

		List<SensorDataPointStream> streams = history.getDataPointStreams();
		for (Element caption : document.select("table.table-striped thead tr th.measurement")) {
			streams.add(new SensorDataPointStream(caption.text()));
		}

		for (Element row : document.select("table.table-striped tbody tr")) {
			LocalDateTime time;
			try {
				time = LocalDateTime.parse(row.select("td.timestamp").text(), TIMESTAMP_FORMAT);
			} catch (DateTimeParseException e) {
				LOG.debugf("Skipping history row without valid timestamp: %s", row.text());
				continue;
			}

			Elements cells = row.select("td.measurement");
			for (int i = 0; i < cells.size() && i < streams.size(); i++) {
				addDataPoint(streams.get(i), time, cells.get(i).text());
			}
		}
		return history;
	}

	private static void addDataPoint(SensorDataPointStream stream, LocalDateTime time, String cellText) {
		Matcher m = VALUE_WITH_UNIT.matcher(cellText.trim());
		if (!m.matches()) {
			LOG.debugf("Skipping unparsable value '%s' of %s", cellText, stream.getName());
			return;
		}
		stream.getValues().add(new SensorDataPoint(time, Float.parseFloat(m.group(1).replace(',', '.'))));
		if (stream.getUnit().isBlank()) {
			stream.setUnit(m.group(2).trim());
		}
	}

	private static Optional<String> firstGroup(Pattern pattern, String input) {
		Matcher m = pattern.matcher(input);
		return m.find() ? Optional.of(m.group(1)) : Optional.empty();
	}
}
