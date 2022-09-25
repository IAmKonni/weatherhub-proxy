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
public class SensorParser {
	
	@Inject
	Logger log;

	final String simpleSensorRegex = "([^ ]+) ID ([^ ]+) Zeitpunkt (.*) Temperatur ([^ ]+) ?C? Luftfeuchte ([^% ]+)%?";
    final String multiSensorRegex = "([^ ]+) ID ([^ ]+) Zeitpunkt (.*) Temp In ([^ ]+) ?C? Hum In ([^%]+)%? Temp 1 ([^ ]+) ?C? Hum 1 ([^%]+)%? Temp 2 ([^ ]+) ?C? Hum 2 ([^%]+)%? Temp 3 ([^ ]+) ?C? Hum 3 ([^%]+)%?";
    final String rainSensorRegex = "([^ ]+) ID ([^ ]+) Zeitpunkt (.*) Regen ([^ ]+) ?mm?";
    
    final String valueUnitRegex = "([0-9,]+) ?(.*)";
    
	public Set<Sensor> parseAllSensorData(String html) {
		Set<Sensor> resultSet = new LinkedHashSet<>();

		Document document = Jsoup.parse(html);

		Elements sensorElements = document.select("div.sensor");

		for (Element sensor : sensorElements) {
			Sensor tsensor = parseSingleSensorData(sensor.text());
			if (tsensor != null && tsensor.getId() != null && !tsensor.getId().isEmpty()) {
				resultSet.add(tsensor);
			}
			log.debug(tsensor == null ? sensor.text() : tsensor.toString());
		}
		return resultSet;
	}

	public Sensor parseSingleSensorData(String text) {
		Sensor result = null;
		result = parseMultiSensorData(text);
		
		if (result == null) {
			result = parseSimpleSensorData(text);
		}
		
		if (result == null) {
			result = parseRainSensorData(text);
		}
		
		return result;
	}

	private Sensor parseRainSensorData(String text) {
		final Pattern pattern = Pattern.compile(rainSensorRegex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(text);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		RainSensor sensor = null;

		while (matcher.find()) {
			sensor = new RainSensor();
			sensor.setLabel(matcher.group(1));
			sensor.setId(matcher.group(2));
			sensor.setTime(LocalDateTime.parse(matcher.group(3), formatter));
			try {
				sensor.setRainfall(Float.parseFloat(matcher.group(4).replace(",", ".")));
			} catch (NumberFormatException e) {
				log.error("Sensor liefert keine Daten: " + sensor.getId() + ", " + sensor.getLabel(), e);
				sensor.setRainfall(0);
			}
		}
		
		return sensor;
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

	public SensorHistory parseSensorHistoryData(String htmlBody) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		
		Document document = Jsoup.parse(htmlBody);

		Elements headCaptions = document.select("table.table-striped thead tr");
		Elements valueRows = document.select("table.table-striped tbody tr");
		
		SensorHistory history = new SensorHistory();
		history.setId("testId");
		history.setLabel("label");
		
		for (Element head : headCaptions) {
			//Elements tsElement = head.select("th.timestamp");
			Elements measurementElements = head.select("th.measurement");
			for (Element measurement : measurementElements) {
				SensorDataPointStream dpStream = new SensorDataPointStream();
				dpStream.setName(measurement.text());
				history.getDataPointStreams().add(dpStream);
			}
		}

		for (Element row : valueRows) {
			
			Elements tsElement = row.select("td.timestamp");
			Elements measurementElements = row.select("td.measurement");

			for (int i = 0; i < measurementElements.size(); i++) {
				SensorDataPoint dp = new SensorDataPoint();
				dp.setTime(LocalDateTime.parse(tsElement.text(), formatter));
				dp.setValue(Float.parseFloat(getValueString(measurementElements.get(i).text()).replace(",", ".")));
				history.getDataPointStreams().get(i).getValues().add(dp);
				
				//set unit
				if (history.getDataPointStreams().get(i).getUnit().isBlank()) {
					history.getDataPointStreams().get(i).setUnit(getUnitString(measurementElements.get(i).text()));
				}
			}
		}
		return history;
	}	
	
	private String getValueString(String input) {
		final Pattern pattern = Pattern.compile(valueUnitRegex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(input);
		
		while (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}
	
	private String getUnitString(String input) {
		final Pattern pattern = Pattern.compile(valueUnitRegex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(input);
		
		while (matcher.find()) {
			return matcher.group(2);
		}
		return "";
	}
	
}
