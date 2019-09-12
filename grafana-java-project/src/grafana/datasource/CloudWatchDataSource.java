package grafana.datasource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class CloudWatchDataSource extends AbstractDataSource{
	private File file;
	private String accessKey;
	private String secretKey;
	private String defaultRegion;
	
	public CloudWatchDataSource(String name, String accessKey, String secretKey, String defaultRegion) {
		setName(name);
		setType("cloudwatch");
		setAccessKey(accessKey);
		setSecretKey(secretKey);
		setDefaultRegion(defaultRegion);
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getDefaultRegion() {
		return defaultRegion;
	}

	public void setDefaultRegion(String defaultRegion) {
		this.defaultRegion = defaultRegion;
	}

	@Override
	public void createConfig(String grafanaPath) {
		file = new File(grafanaPath+"/conf/provisioning/datasources/"+getName()+".yaml");
		String toFile = "apiVersion: 1\n"
				+ "datasources:\n"
				+ " - name: " + getName() + "\n"
				+ "   type: " + getType() + "\n" 
				+ "   jsonData:\n"
				+ "     authType: keys\n"
				+ "     defaultRegion: " + getDefaultRegion() + "\n"
				+ "   secureJsonData:\n"
				+ "     accessKey: \"" + getAccessKey() + "\"\n"
				+ "     secretKey: \"" + getSecretKey() + "\"";
				
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
