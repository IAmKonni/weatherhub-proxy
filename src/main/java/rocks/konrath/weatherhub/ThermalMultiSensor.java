package rocks.konrath.weatherhub;

import java.time.LocalDateTime;

public class ThermalMultiSensor extends ThermalSensor {
	
	public float temperature2;
	public float temperature3;
	public float temperature4;
	public int humidity2;
	public int humidity3;
	public int humidity4;
	
	public ThermalMultiSensor() {
		super();
	}
	public ThermalMultiSensor(String id, String label, LocalDateTime time, float temperature, int humidity, float temperature2, int humidity2, float temperature3, int humidity3, float temperature4, int humidity4) {
		super(id, label, time, temperature, humidity);
		this.temperature2 = temperature2;
		this.temperature3 = temperature3;
		this.temperature4 = temperature4;
		this.humidity2 = humidity2;
		this.humidity3 = humidity3;
		this.humidity4 = humidity4;
	}
	public float getTemperature2() {
		return temperature2;
	}
	public void setTemperature2(float temperature2) {
		this.temperature2 = temperature2;
	}
	public float getTemperature3() {
		return temperature3;
	}
	public void setTemperature3(float temperature3) {
		this.temperature3 = temperature3;
	}
	public int getHumidity2() {
		return humidity2;
	}
	public void setHumidity2(int humidity2) {
		this.humidity2 = humidity2;
	}
	public int getHumidity3() {
		return humidity3;
	}
	public void setHumidity3(int humidity3) {
		this.humidity3 = humidity3;
	}
	public float getTemperature4() {
		return temperature4;
	}
	public void setTemperature4(float temperature4) {
		this.temperature4 = temperature4;
	}
	public int getHumidity4() {
		return humidity4;
	}
	public void setHumidity4(int humidity4) {
		this.humidity4 = humidity4;
	}


}
