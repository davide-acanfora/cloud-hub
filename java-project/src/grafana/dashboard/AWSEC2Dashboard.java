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

public class AWSEC2Dashboard extends Dashboard{
	private File file;
	private ArrayList<String> vms;
	private ArrayList<String> volumes;
	
	public AWSEC2Dashboard(String name, CloudWatchDataSource cloudWatchDataSource) {
		setName(name);
		setDataSource(cloudWatchDataSource);
		this.vms = new ArrayList<String>();
		this.volumes = new ArrayList<String>();
	}
	
	@Override
	public void createConfig() {
		try {
			String dashboard = new String(Files.readAllBytes(Paths.get(Grafana.folderPath+"/conf/provisioning/dashboards/awsec2.template")));
			
			dashboard = dashboard.replaceAll("\\$dashboard-name", getName());
			dashboard = dashboard.replaceAll("\\$dashboard-uid", getName().replaceAll(" ", ""));
			dashboard = dashboard.replaceAll("\\$cloudwatch-datasource-name", dataSource.getName());
			dashboard = dashboard.replaceAll("\\$cloudwatch-default-region", ((CloudWatchDataSource) dataSource).getDefaultRegion());
			if (vms.isEmpty()) 
				dashboard = dashboard.replaceAll("\\$vm-regex|\\$vm-current", "");
			else {
				String regex = "";
				int i;
				for (i=0; i<vms.size()-1; i++)
					regex += vms.get(i) + "|";
				regex += vms.get(i);
				dashboard = dashboard.replaceAll("\\$vm-regex", regex);
				dashboard = dashboard.replaceAll("\\$vm-current", vms.get(i));
			}
			
			if (volumes.isEmpty()) 
				dashboard = dashboard.replaceAll("\\$volume-ids|\\$volume-current", "");
			else {
				String options = "";
				int i;
				for (i=0; i<volumes.size()-1; i++)
					options += "{\"selected\":false, \"text\":\"" + volumes.get(i) + "\", \"value\":\"" + volumes.get(i) + "\"},";
				options += "{\"selected\":true, \"text\":\"" + volumes.get(i) + "\", \"value\":\"" + volumes.get(i) + "\"}";
				dashboard = dashboard.replaceAll("\\$volume-ids", options);
				dashboard = dashboard.replaceAll("\\$volume-current", volumes.get(i));
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
	
	public void addVM(String intanceId) {
		this.vms.add(intanceId);
		if (this.file != null) 
			createConfig();
	}
	
	public void addEBSVolume(String volumeId) {
		this.volumes.add(volumeId);
		if (this.file != null) 
			createConfig();
	}
	
}