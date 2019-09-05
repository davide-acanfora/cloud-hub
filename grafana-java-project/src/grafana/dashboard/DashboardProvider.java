package grafana.dashboard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import grafana.Configurable;

public class DashboardProvider implements Configurable{
	private String providerName;
	private boolean editable;
	private File file;

	public DashboardProvider(String providerName, boolean editable) {
		this.providerName = providerName;
		this.editable = editable;
	}
	
	public DashboardProvider(String providerName) {
		this.providerName = providerName;
		this.editable = false;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public void createConfig(String grafanaPath) {
		String fullpath = grafanaPath + "/conf/provisioning/dashboards";
		file = new File(fullpath+"/"+getProviderName()+".yaml");
		String toFile = "apiVersion: 1\n"
				+ "providers:\n"
				+ " - name: " + getProviderName() + "\n"
				+ "   folder: ''\n"
				+ "   type: file\n"
				+ "   editable: " + isEditable() + "\n"
				+ "   options:\n"
				+ "      path: ../conf/provisioning/dashboards";
		
		Writer writer = null;
		try{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			writer.write(toFile);
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
	}

	@Override
	public void deleteConfig() {
		file.delete();
	}

}
