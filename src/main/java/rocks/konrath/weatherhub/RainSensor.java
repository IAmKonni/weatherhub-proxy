package rocks.konrath.weatherhub;

import io.quarkus.runtime.annotations.RegisterForReflection;

/** Rainfall sensor (value in mm). */
@RegisterForReflection
public class RainSensor extends Sensor {

	private float rainfall;

	public float getRainfall() {
		return rainfall;
	}

	public void setRainfall(float rainfall) {
		this.rainfall = rainfall;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + getId() + ", label=" + getLabel() + ", time=" + getTime()
				+ ", rainfall=" + rainfall + "]";
	}
}
