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

public class AzureContainerDashboard extends Dashboard{
	private File file;
	private String resourceGroup;
	private ArrayList<String> containers;
	
	public AzureContainerDashboard(String name, String resourceGroup, AzureMonitorDataSource azureMonitorDataSource) {
		setName(name);
		setDataSource(azureMonitorDataSource);
		this.resourceGroup = resourceGroup;
		this.containers = new ArrayList<String>();
	}

	@Override
	public void createConfig() {
		try {
			String dashboard = new String(Files.readAllBytes(Paths.get(Grafana.folderPath+"/conf/provisioning/dashboards/azurecontainer.template")));
			
			dashboard = dashboard.replaceAll("\\$dashboard-name", getName());
			dashboard = dashboard.replaceAll("\\$dashboard-uid", getName().replaceAll(" ", ""));
			dashboard = dashboard.replaceAll("\\$azure-datasource-name", this.dataSource.getName());
	
			dashboard = dashboard.replaceAll("\\$resource-group", this.resourceGroup);
			
			if (containers.isEmpty()) 
				dashboard = dashboard.replaceAll("\\$variable-regex|\\$variable-current", "");
			else {
				String regex = "";
				int i;
				for (i=0; i<containers.size()-1; i++)
					regex += containers.get(i) + ".*|";
				regex += containers.get(i)+".*";
				dashboard = dashboard.replaceAll("\\$variable-regex", regex);
				dashboard = dashboard.replaceAll("\\$variable-current", containers.get(i));
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
	
	public void addResource(String StorageAccount) {
		this.containers.add(StorageAccount);
		if (this.file != null) 
			createConfig();
	}
	
}