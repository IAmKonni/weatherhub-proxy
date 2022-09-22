package rocks.konrath.weatherhub;

import java.time.LocalDateTime;

public class ThermalSensor extends Sensor {
	
	public float temperature;
	public int humidity;
	    
	public ThermalSensor() {
		super();
	}
	
	public ThermalSensor(String id, String label, LocalDateTime time, float temperature, int humidity) {
		super();
		this.id = id;
		this.label = label;
		this.time = time;
		this.temperature = temperature;
		this.humidity = humidity;
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
