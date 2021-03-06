package rocks.konrath.weatherhub;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Singleton
public class ThermalSensorParser {
	
	@Inject
	Logger log;

	final String simpleSensorRegex = "([^ ]+) ID ([^ ]+) Zeitpunkt (.*) Temperatur ([^ ]+) ?C? Luftfeuchte ([^% ]+)%?";
    final String multiSensorRegex = "([^ ]+) ID ([^ ]+) Zeitpunkt (.*) Temp In ([^ ]+) ?C? Hum In ([^%]+)%? Temp 1 ([^ ]+) ?C? Hum 1 ([^%]+)%? Temp 2 ([^ ]+) ?C? Hum 2 ([^%]+)%? Temp 3 ([^ ]+) ?C? Hum 3 ([^%]+)%?";
    
	public Set<ThermalSensor> parseAllSensorData(String html) {
		Set<ThermalSensor> resultSet = new LinkedHashSet<>();

		Document document = Jsoup.parse(html);

		Elements sensorElements = document.select("div.sensor");

		for (Element sensor : sensorElements) {
			ThermalSensor tsensor = parseSingleSensorData(sensor.text());
			if (tsensor != null && tsensor.getId() != null && !tsensor.getId().isEmpty()) {
				resultSet.add(tsensor);
			}
			log.debug(tsensor.toString());
		}
		return resultSet;
	}

	public ThermalSensor parseSingleSensorData(String text) {
		ThermalSensor result = null;
		result = parseMultiSensorData(text);
		
		if (result == null) {
			result = parseSimpleSensorData(text);
		}
		
		return result;
	}

	private ThermalMultiSensor parseMultiSensorData(String text) {
		final Pattern pattern = Pattern.compile(multiSensorRegex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(text);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		ThermalMultiSensor sensor = null;

		while (matcher.find()) {
			sensor = new ThermalMultiSensor();
			sensor.setLabel(matcher.group(1));
			sensor.setId(matcher.group(2));
			sensor.setTime(LocalDateTime.parse(matcher.group(3), formatter));
			try {
				sensor.setTemperature(Float.parseFloat(matcher.group(4).replace(",", ".")));
				sensor.setHumidity(Integer.parseInt(matcher.group(5)));
			} catch (NumberFormatException e) {
				log.warn("Sensor (1) liefert keine Daten: " + sensor.getId() + ", " + sensor.getLabel(), e);
				sensor.setTemperature(0);
				sensor.setHumidity(0);
			}
			
			try {
				sensor.setTemperature2(Float.parseFloat(matcher.group(6).replace(",", ".")));
				sensor.setHumidity2(Integer.parseInt(matcher.group(7)));
			} catch (NumberFormatException e) {
				log.warn("Sensor (2) liefert keine Daten: " + sensor.getId() + ", " + sensor.getLabel(), e);
				sensor.setTemperature2(0);
				sensor.setHumidity2(0);
			}
			
			try {
				sensor.setTemperature3(Float.parseFloat(matcher.group(8).replace(",", ".")));
				sensor.setHumidity3(Integer.parseInt(matcher.group(9)));
			} catch (NumberFormatException e) {
				log.warn("Sensor (3) liefert keine Daten: " + sensor.getId() + ", " + sensor.getLabel(), e);
				sensor.setTemperature3(0);
				sensor.setHumidity3(0);
			}
			
			try {
				sensor.setTemperature4(Float.parseFloat(matcher.group(10).replace(",", ".")));
				sensor.setHumidity4(Integer.parseInt(matcher.group(11)));
			} catch (NumberFormatException e) {
				log.warn("Sensor (4) liefert keine Daten: " + sensor.getId() + ", " + sensor.getLabel(), e);
				sensor.setTemperature4(0);
				sensor.setHumidity4(0);
			}
		}
		
		return sensor;
	}

	private ThermalSensor parseSimpleSensorData(String text) {
		final Pattern pattern = Pattern.compile(simpleSensorRegex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(text);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		ThermalSensor sensor = null;

		while (matcher.find()) {
			sensor = new ThermalSensor();
			sensor.setLabel(matcher.group(1));
			sensor.setId(matcher.group(2));
			sensor.setTime(LocalDateTime.parse(matcher.group(3), formatter));
			try {
				sensor.setTemperature(Float.parseFloat(matcher.group(4).replace(",", ".")));
				sensor.setHumidity(Integer.parseInt(matcher.group(5)));
			} catch (NumberFormatException e) {
				log.error("Sensor liefert keine Daten: " + sensor.getId() + ", " + sensor.getLabel(), e);
				sensor.setTemperature(0);
				sensor.setHumidity(0);
			}
		}
		
		return sensor;
	}
	
	
}
