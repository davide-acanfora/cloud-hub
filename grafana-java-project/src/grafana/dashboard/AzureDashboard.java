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
import grafana.Grafana;
import grafana.datasource.AzureMonitorDataSource;

public class AzureDashboard extends Dashboard{
	private File file;
	private ArrayList<String> functions;
	
	public AzureDashboard(String name, AzureMonitorDataSource azureMonitorDataSource) {
		setName(name);
		setDataSource(azureMonitorDataSource);
		this.functions = new ArrayList<String>();
	}

	@Override
	public void createConfig() {
		try {
			String dashboard = new String(Files.readAllBytes(Paths.get(Grafana.folderPath+"/conf/provisioning/dashboards/azuredashboard.template")));
			
			dashboard = dashboard.replaceAll("\\$dashboard-name", getName());
			dashboard = dashboard.replaceAll("\\$dashboard-uid", getName().replaceAll(" ", ""));
			dashboard = dashboard.replaceAll("\\$azure-datasource-name", this.dataSource.getName());
			if (functions.isEmpty()) 
				dashboard = dashboard.replaceAll("\\$variable-functions|\\$variable-current", "");
			else {
				String options = "";
				int i;
				for (i=0; i<functions.size()-1; i++)
					options += "{\"selected\":false, \"text\":\"" + functions.get(i) + "\", \"value\":\"" + functions.get(i) + "\"},";
				options += "{\"selected\":true, \"text\":\"" + functions.get(i) + "\", \"value\":\"" + functions.get(i) + "\"}";
				dashboard = dashboard.replaceAll("\\$variable-functions", options);
				dashboard = dashboard.replaceAll("\\$variable-current", functions.get(i));
			}	
			
			file = new File(Grafana.folderPath+"/conf/provisioning/dashboards/"+getName()+".json");
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
	
	public void addFunction(String functionName) {
		this.functions.add(functionName);
		if (this.file != null) 
			createConfig();
	}

	
}