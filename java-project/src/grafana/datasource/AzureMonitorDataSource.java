package grafana.datasource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import grafana.Grafana;

public class AzureMonitorDataSource extends DataSource{
	private static int counter = 0;
	private File file;
	private String tenantId;
	private String clientId;
	private String clientSecret;
	private String defaultSubscription;
	private String applicationId;
	private String apiKey;
	
	
	public AzureMonitorDataSource(String tenantId, String clientId, String clientSecret, String defaultSubscription, String applicationId, String apiKey) {
		setName("AzureMonitor"+(++counter));
		setType("grafana-azure-monitor-datasource");
		this.tenantId = tenantId;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.defaultSubscription = defaultSubscription;
		this.applicationId = applicationId;
		this.apiKey = apiKey;
	}	

	@Override
	public void createConfig() {
		file = new File(Grafana.folderPath+"/conf/provisioning/datasources/"+getName()+".yaml");
		String toFile = "apiVersion: 1\n"
				+ "datasources:\n"
				+ " - name: " + getName() + "\n"
				+ "   type: " + getType() + "\n"
				+ "   access: proxy\n" 
				+ "   jsonData:\n"
				+ "     cloudName: azuremonitor\n"
				+ "     subscriptionId: " + this.defaultSubscription + "\n"
				+ "     tenantId: " + this.tenantId + "\n"
				+ "     clientId: " + this.clientId + "\n"
				+ "     appInsightsAppId: " + this.applicationId + "\n"
				+ "   secureJsonData:\n"
				+ "     clientSecret: " + this.clientSecret + "\n"
				+ "     appInsightsApiKey: " + this.apiKey + "\n";
				
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
