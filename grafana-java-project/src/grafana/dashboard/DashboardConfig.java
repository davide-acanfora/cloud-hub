package grafana.dashboard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import grafana.Configurable;

public class DashboardConfig implements Configurable{
	private File file;
	private String dataSourceName;	
	
	public DashboardConfig(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	@Override
	public void createConfig(String grafanaPath) {
		try {
			String dashboard = new String(Files.readAllBytes(Paths.get(grafanaPath+"/conf/provisioning/dashboards/dashboard.template")));
			dashboard = dashboard.replaceAll("\\$json-datasource-name", this.dataSourceName);
			file = new File(grafanaPath+"/conf/provisioning/dashboards/dashboard.json");
			Writer writer = null;
			
			try{
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
				writer.write(dashboard);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteConfig() {
		file.delete();
	}

}
