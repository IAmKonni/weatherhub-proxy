package rocks.konrath.weatherhub;

import java.time.LocalDateTime;

public class Sensor {

	public String id;
	public String label;
	public LocalDateTime time;

	public Sensor() {
		super();
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
	
	public String getSensorClass() {
		return this.getClass().getName();
	}

}