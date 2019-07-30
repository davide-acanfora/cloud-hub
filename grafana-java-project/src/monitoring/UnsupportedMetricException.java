package monitoring;

public class UnsupportedMetricException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public UnsupportedMetricException(String message) {
		super(message);
	}
	
	public UnsupportedMetricException() {
		super();
	}
}
