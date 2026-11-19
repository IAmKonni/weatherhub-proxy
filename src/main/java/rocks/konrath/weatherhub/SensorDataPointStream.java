package rocks.konrath.weatherhub;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

/** All values of one measurement (e.g. temperature) of a sensor. */
@RegisterForReflection
public class SensorDataPointStream {

	private String name = "";
	private String unit = "";
	private List<SensorDataPoint> values = new ArrayList<>();

	public SensorDataPointStream() {
	}

	public SensorDataPointStream(String name) {
		this.name = name;
	}

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
