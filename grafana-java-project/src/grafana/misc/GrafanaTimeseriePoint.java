package grafana.misc;

public class GrafanaTimeseriePoint {
	private double value;
	private long timestamp;
	
	public GrafanaTimeseriePoint(double value, long timestamp) {
		this.value = value;
		this.timestamp = timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

}
