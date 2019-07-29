package grafana;

public interface Configurable {
	public void createConfig(String grafanaPath);
	public void deleteConfig();
}
