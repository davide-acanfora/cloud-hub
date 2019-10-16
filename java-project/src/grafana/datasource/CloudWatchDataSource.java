package grafana.datasource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import grafana.Grafana;

public class CloudWatchDataSource extends DataSource{
	private static int counter = 0;
	private File file;
	private String accessKey;
	private String secretKey;
	private String defaultRegion;
	
	public CloudWatchDataSource(String accessKey, String secretKey, String defaultRegion) {
		setName("CloudWatch"+(++counter));
		setType("cloudwatch");
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.defaultRegion = defaultRegion;
	}
	
	public String getDefaultRegion() {
		return defaultRegion;
	}

	@Override
	public void createConfig() {
		file = new File(Grafana.folderPath+"/conf/provisioning/datasources/"+getName()+".yaml");
		String toFile = "apiVersion: 1\n"
				+ "datasources:\n"
				+ " - name: " + getName() + "\n"
				+ "   type: " + getType() + "\n" 
				+ "   jsonData:\n"
				+ "     authType: keys\n"
				+ "     defaultRegion: " + this.defaultRegion + "\n"
				+ "   secureJsonData:\n"
				+ "     accessKey: \"" + this.accessKey + "\"\n"
				+ "     secretKey: \"" + this.secretKey + "\"";
				
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