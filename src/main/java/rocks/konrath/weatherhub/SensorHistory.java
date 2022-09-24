package rocks.konrath.weatherhub;

import java.util.ArrayList;
import java.util.List;

public class SensorHistory {
	
	String id = "";
	String label = "";
	List<SensorDataPointStream> dataPointStreams = new ArrayList<>();
	
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
