package rocks.konrath.weatherhub;

import java.time.LocalDateTime;

public class SensorDataPoint {
	
	LocalDateTime time;
	float value;
	
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}

}
