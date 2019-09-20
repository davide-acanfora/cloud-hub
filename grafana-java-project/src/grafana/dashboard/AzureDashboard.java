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
import java.util.ArrayList;

import grafana.Configurable;
import grafana.datasource.AzureMonitorDataSource;

public class AzureDashboard implements Configurable{
	private File file;
	private AzureMonitorDataSource azureMonitorDataSource;
	private ArrayList<String> functions;
	
	public AzureDashboard(AzureMonitorDataSource azureMonitorDataSource) {
		this.azureMonitorDataSource = azureMonitorDataSource;
		this.functions = new ArrayList<String>();
	}

	@Override
	public void createConfig(String grafanaPath) {
		try {
			String dashboard = new String(Files.readAllBytes(Paths.get(grafanaPath+"/conf/provisioning/dashboards/azuredashboard.template")));
			
			dashboard = dashboard.replaceAll("\\$azure-datasource-name", azureMonitorDataSource.getName());
			if (functions.isEmpty()) 
				dashboard = dashboard.replaceAll("\\$variable-functions", "");
			else {
				String options = "";
				int i;
				for (i=0; i<functions.size()-1; i++)
					options += "{\"selected\":false, \"text\":\"" + functions.get(i) + "\", \"value\":\"" + functions.get(i) + "\"},";
				options += "{\"selected\":true, \"text\":\"" + functions.get(i) + "\", \"value\":\"" + functions.get(i) + "\"}";
				dashboard = dashboard.replaceAll("\\$variable-functions", options);
				dashboard = dashboard.replaceAll("\\$variable-current", functions.get(i));
			}	
			
			file = new File(grafanaPath+"/conf/provisioning/dashboards/azuredashboard.json");
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
		azureMonitorDataSource.deleteConfig();
	}
	
	public void addFunction(String functionName) {
		this.functions.add(functionName);
	}

	
}