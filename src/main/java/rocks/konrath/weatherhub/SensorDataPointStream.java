package rocks.konrath.weatherhub;

import java.util.ArrayList;
import java.util.List;

public class SensorDataPointStream {
  
	String name = "";
	String unit = "";
	
	List<SensorDataPoint> values = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<SensorDataPoint> getValues() {
		return values;
	}

	public void setValues(List<SensorDataPoint> values) {
		this.values = values;
	}
}
