package rocks.konrath.weatherhub;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Base station with three additional temperature / humidity channels.
 * Channel 1 is the inherited {@code temperature} / {@code humidity}.
 */
@RegisterForReflection
public class ThermalMultiSensor extends ThermalSensor {

	private float temperature2;
	private float temperature3;
	private float temperature4;
	private int humidity2;
	private int humidity3;
	private int humidity4;

	/**
	 * Sets temperature and humidity for channel 1 to 4.
	 */
	public void setChannel(int channel, float temperature, int humidity) {
		switch (channel) {
			case 1 -> {
				setTemperature(temperature);
				setHumidity(humidity);
			}
			case 2 -> {
				temperature2 = temperature;
				humidity2 = humidity;
			}
			case 3 -> {
				temperature3 = temperature;
				humidity3 = humidity;
			}
			case 4 -> {
				temperature4 = temperature;
				humidity4 = humidity;
			}
			default -> throw new IllegalArgumentException("Unknown channel: " + channel);
		}
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

	public float getTemperature4() {
		return temperature4;
	}

	public void setTemperature4(float temperature4) {
		this.temperature4 = temperature4;
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

	public int getHumidity4() {
		return humidity4;
	}

	public void setHumidity4(int humidity4) {
		this.humidity4 = humidity4;
	}
}
