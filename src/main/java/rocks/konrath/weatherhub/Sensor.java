package rocks.konrath.weatherhub;

import java.time.LocalDateTime;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Base type for all sensors read from the Mobile Alerts overview page.
 * <p>
 * The fully qualified class name is part of the public JSON contract
 * ({@code sensorClass}), so classes in this hierarchy must not be renamed or moved.
 */
@RegisterForReflection
public class Sensor {

	private String id;
	private String label;
	private LocalDateTime time;

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

	public String getSensorClass() {
		return getClass().getName();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [id=" + id + ", label=" + label + ", time=" + time + "]";
	}
}
