package grafana;

import java.io.File;

public interface Configurable {
	public File createConfig(String grafanaPath);
}
