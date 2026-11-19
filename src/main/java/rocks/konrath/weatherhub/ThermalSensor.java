package rocks.konrath.weatherhub;

import io.quarkus.runtime.annotations.RegisterForReflection;

/** Temperature / humidity sensor. */
@RegisterForReflection
public class ThermalSensor extends Sensor {

	private float temperature;
	private int humidity;

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
		return getClass().getSimpleName() + " [id=" + getId() + ", label=" + getLabel() + ", time=" + getTime()
				+ ", temperature=" + temperature + ", humidity=" + humidity + "]";
	}
}
