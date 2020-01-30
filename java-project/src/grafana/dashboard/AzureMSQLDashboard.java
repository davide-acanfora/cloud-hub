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

public class AzureMSQLDashboard extends Dashboard{
	private File file;
	private ArrayList<String> dbs;
	
	public AzureMSQLDashboard(String name, AzureMonitorDataSource azureMonitorDataSource) {
		setName(name);
		setDataSource(azureMonitorDataSource);
		this.dbs = new ArrayList<String>();
	}

	@Override
	public void createConfig() {
		try {
			String dashboard = new String(Files.readAllBytes(Paths.get(Grafana.folderPath+"/conf/provisioning/dashboards/azuremsql.template")));
			
			dashboard = dashboard.replaceAll("\\$dashboard-name", getName());
			dashboard = dashboard.replaceAll("\\$dashboard-uid", getName().replaceAll(" ", ""));
			dashboard = dashboard.replaceAll("\\$azure-datasource-name", this.dataSource.getName());
			if (dbs.isEmpty()) 
				dashboard = dashboard.replaceAll("\\$variable-functions|\\$variable-current", "");
			else {
				String options = "";
				int i;
				for (i=0; i<dbs.size()-1; i++)
					options += "{\"selected\":false, \"text\":\"" + dbs.get(i) + "\", \"value\":\"" + dbs.get(i) + "\"},";
				options += "{\"selected\":true, \"text\":\"" + dbs.get(i) + "\", \"value\":\"" + dbs.get(i) + "\"}";
				dashboard = dashboard.replaceAll("\\$variable-dbs", options);
				dashboard = dashboard.replaceAll("\\$variable-current", dbs.get(i));
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
	
	public void addDatabase(String databaseName) {
		this.dbs.add(databaseName);
		if (this.file != null) 
			createConfig();
	}

	
}