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
import grafana.Grafana;
import grafana.datasource.AzureMonitorDataSource;

public class AzureQueueDashboard extends Dashboard{
	private File file;
	private String resourceGroup;
	private String resourceName;
	
	public AzureQueueDashboard(String name, String resourceGroup, String resourceName, AzureMonitorDataSource azureMonitorDataSource) {
		setName(name);
		setDataSource(azureMonitorDataSource);
		this.resourceGroup = resourceGroup;
		this.resourceName = resourceName;
	}

	@Override
	public void createConfig() {
		try {
			String dashboard = new String(Files.readAllBytes(Paths.get(Grafana.folderPath+"/conf/provisioning/dashboards/azurequeue.template")));
			
			dashboard = dashboard.replaceAll("\\$dashboard-name", getName());
			dashboard = dashboard.replaceAll("\\$dashboard-uid", getName().replaceAll(" ", ""));
			dashboard = dashboard.replaceAll("\\$azure-datasource-name", this.dataSource.getName());
			
			dashboard = dashboard.replaceAll("\\$resource-group", this.resourceGroup);
			dashboard = dashboard.replaceAll("\\$resource-name", this.resourceName);
			
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
	
}