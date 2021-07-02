package rocks.konrath.weatherhub;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThermalSensor {
	
	public String id;
	public String label;
	public LocalDateTime time;
	public float temperature;
	public int humidity;
	
    final String simpleSensorRegex = "([^ ]+) ID ([^ ]+) Zeitpunkt (.*) Temperatur ([^ ]+) C Luftfeuchte ([^%]+)%";
    
	public ThermalSensor() {
		super();
	}
	
	public ThermalSensor(String text) {
		final Pattern pattern = Pattern.compile(simpleSensorRegex, Pattern.MULTILINE);
	    final Matcher matcher = pattern.matcher(text);
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
	    
	    while (matcher.find()) {	        
	        label = matcher.group(1);
	        id = matcher.group(2);
	        time = LocalDateTime.parse(matcher.group(3), formatter);
	        temperature = Float.parseFloat(matcher.group(4).replace(",", "."));
	        humidity = Integer.parseInt(matcher.group(5));
	    }
	}
	
	public ThermalSensor(String id, String label, LocalDateTime time, float temperature, int humidity) {
		super();
		this.id = id;
		this.label = label;
		this.time = time;
		this.temperature = temperature;
		this.humidity = humidity;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public float getTemperature() {
		return temperature;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	public int getHumidity() {
		return humidity;
	}
	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	@Override
	public String toString() {
		return "ThermalSensor [id=" + id + ", label=" + label + ", time=" + time + ", temperature=" + temperature
				+ ", humidity=" + humidity + "]";
	}
	
}
