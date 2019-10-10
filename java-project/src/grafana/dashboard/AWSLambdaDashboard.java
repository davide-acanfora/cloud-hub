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
import grafana.datasource.CloudWatchDataSource;

public class AWSLambdaDashboard extends Dashboard{
	private File file;
	private ArrayList<String> functions;
	
	public AWSLambdaDashboard(String name, CloudWatchDataSource cloudWatchDataSource) {
		setName(name);
		setDataSource(cloudWatchDataSource);
		this.functions = new ArrayList<String>();
	}
	
	@Override
	public void createConfig() {
		try {
			String dashboard = new String(Files.readAllBytes(Paths.get(Grafana.folderPath+"/conf/provisioning/dashboards/awslambda.template")));
			
			dashboard = dashboard.replaceAll("\\$dashboard-name", getName());
			dashboard = dashboard.replaceAll("\\$dashboard-uid", getName().replaceAll(" ", ""));
			dashboard = dashboard.replaceAll("\\$cloudwatch-datasource-name", dataSource.getName());
			dashboard = dashboard.replaceAll("\\$cloudwatch-default-region", ((CloudWatchDataSource) dataSource).getDefaultRegion());
			if (functions.isEmpty()) 
				dashboard = dashboard.replaceAll("\\$functionname-regex|\\$variable-current", "");
			else {
				String regex = "";
				int i;
				for (i=0; i<functions.size()-1; i++)
					regex += functions.get(i) + "|";
				regex += functions.get(i);
				dashboard = dashboard.replaceAll("\\$functionname-regex", regex);
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