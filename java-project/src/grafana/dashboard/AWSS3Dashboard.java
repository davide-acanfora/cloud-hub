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

public class AWSS3Dashboard extends Dashboard{
	private File file;
	private ArrayList<String> buckets;
	
	public AWSS3Dashboard(String name, CloudWatchDataSource cloudWatchDataSource) {
		setName(name);
		setDataSource(cloudWatchDataSource);
		this.buckets = new ArrayList<String>();
	}
	
	@Override
	public void createConfig() {
		try {
			String dashboard = new String(Files.readAllBytes(Paths.get(Grafana.folderPath+"/conf/provisioning/dashboards/awss3.template")));
			
			dashboard = dashboard.replaceAll("\\$dashboard-name", getName());
			dashboard = dashboard.replaceAll("\\$dashboard-uid", getName().replaceAll(" ", ""));
			dashboard = dashboard.replaceAll("\\$cloudwatch-datasource-name", dataSource.getName());
			dashboard = dashboard.replaceAll("\\$cloudwatch-default-region", ((CloudWatchDataSource) dataSource).getDefaultRegion());
			if (buckets.isEmpty()) 
				dashboard = dashboard.replaceAll("\\$buckets-regex|\\$buckets-current", "");
			else {
				String regex = "";
				int i;
				for (i=0; i<buckets.size()-1; i++)
					regex += buckets.get(i) + "|";
				regex += buckets.get(i);
				dashboard = dashboard.replaceAll("\\$buckets-regex", regex);
				dashboard = dashboard.replaceAll("\\$buckets-current", buckets.get(i));
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
	
	public void addBucket(String bucketName) {
		this.buckets.add(bucketName);
		if (this.file != null) 
			createConfig();
	}
	
}