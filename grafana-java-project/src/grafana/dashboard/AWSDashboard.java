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
import grafana.datasource.CloudWatchDataSource;

public class AWSDashboard implements Configurable{
	private File file;
	private CloudWatchDataSource cloudWatchDataSource;
	private ArrayList<String> functions;
	
	public AWSDashboard(CloudWatchDataSource cloudWatchDataSource) {
		this.cloudWatchDataSource = cloudWatchDataSource;
		this.functions = new ArrayList<String>();
	}

	@Override
	public void createConfig(String grafanaPath) {
		try {
			String dashboard = new String(Files.readAllBytes(Paths.get(grafanaPath+"/conf/provisioning/dashboards/awsdashboard.template")));
			
			dashboard = dashboard.replaceAll("\\$cloudwatch-datasource-name", cloudWatchDataSource.getName());
			dashboard = dashboard.replaceAll("\\$cloudwatch-default-region", cloudWatchDataSource.getDefaultRegion());
			if (functions.isEmpty()) 
				dashboard = dashboard.replaceAll("\\$functionname-regex", "");
			else {
				String regex = "";
				int i;
				for (i=0; i<functions.size()-1; i++)
					regex += functions.get(i) + "|";
				regex += functions.get(i);
				dashboard = dashboard.replaceAll("\\$functionname-regex", regex);
			}	
			
			file = new File(grafanaPath+"/conf/provisioning/dashboards/awsdashboard.json");
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
		cloudWatchDataSource.deleteConfig();
	}
	
	public void addFunction(String funcionName) {
		this.functions.add(funcionName);
	}

	
}