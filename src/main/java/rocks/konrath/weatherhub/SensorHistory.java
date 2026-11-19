package rocks.konrath.weatherhub;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

/** Measurement history of a single sensor. */
@RegisterForReflection
public class SensorHistory {

	private String id = "";
	private String label = "";
	private List<SensorDataPointStream> dataPointStreams = new ArrayList<>();

	public SensorHistory() {
	}

	public SensorHistory(String id) {
		this.id = id;
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

	public List<SensorDataPointStream> getDataPointStreams() {
		return dataPointStreams;
	}

	public void setDataPointStreams(List<SensorDataPointStream> dataPointStreams) {
		this.dataPointStreams = dataPointStreams;
	}
}
