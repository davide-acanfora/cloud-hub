package grafana.datasource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class AzureMonitorDataSource extends AbstractDataSource{
	private File file;
	private String tenantId;
	private String clientId;
	private String clientSecret;
	private String defaultSubscription;
	private String applicationId;
	private String apiKey;
	
	
	public AzureMonitorDataSource(String name, String tenantId, String clientId, String clientSecret, String defaultSubscription, String applicationId, String apiKey) {
		setName(name);
		setType("grafana-azure-monitor-datasource");
		setTenantId(tenantId);
		setClientId(clientId);
		setClientSecret(clientSecret);
		setDefaultSubscription(defaultSubscription);
		setApplicationId(applicationId);
		setApiKey(apiKey);
	}	

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getDefaultSubscription() {
		return defaultSubscription;
	}

	public void setDefaultSubscription(String defaultSubscription) {
		this.defaultSubscription = defaultSubscription;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public void createConfig(String grafanaPath) {
		file = new File(grafanaPath+"/conf/provisioning/datasources/"+getName()+".yaml");
		String toFile = "apiVersion: 1\n"
				+ "datasources:\n"
				+ " - name: " + getName() + "\n"
				+ "   type: " + getType() + "\n"
				+ "   access: proxy\n" 
				+ "   jsonData:\n"
				+ "     cloudName: azuremonitor\n"
				+ "     subscriptionId: " + getDefaultSubscription() + "\n"
				+ "     tenantId: " + getTenantId() + "\n"
				+ "     clientId: " + getClientId() + "\n"
				+ "     appInsightsAppId: " + getApplicationId() + "\n"
				+ "   secureJsonData:\n"
				+ "     clientSecret: " + getClientSecret() + "\n"
				+ "     appInsightsApiKey: " + getApiKey() + "\n";
				
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
