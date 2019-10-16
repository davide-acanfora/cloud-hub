package grafana.datasource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import grafana.Grafana;

public class JSONDataSource extends DataSource{
	private String url;
	private File file;
	private String access;
	private long version;
	private boolean editable;
	public static final String SERVER = "proxy";
	public static final String BROWSER = "direct";
	
	public JSONDataSource(String name, String url, String access, long version, boolean editable) {
		setName(name);
		setType("simpod-json-datasource");
		this.url = url;
		this.access = access;
		this.version = version;
		this.editable = editable;
	}
	
	public JSONDataSource(String name, String url, String access) {
		setName(name);
		setType("simpod-json-datasource");
		this.url = url;
		this.access = access;
		this.version = System.currentTimeMillis();
		this.editable = false;
	}

	@Override
	public void createConfig() {
		file = new File(Grafana.folderPath+"/conf/provisioning/datasources/"+getName()+".yaml");
		String toFile = "apiVersion: 1\n"
				+ "datasources:\n"
				+ " - name: " + getName() + "\n"
				+ "   type: " + getType() + "\n"
				+ "   url: " + this.url + "\n"
				+ "   access: " + this.access + "\n"
				+ "   version: " + this.version + "\n"
				+ "   editable: " + this.editable;
		
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
