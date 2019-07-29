package config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

public class Config {
	public static String GrafanaPath;
	public static int GrafanaPort;
	public static int APIServerPort;
	public static String JSONDataSourceName;
	public static String DashboardProviderName;
	public static int CollectorDelay;
	
	static {
			JSONObject object;
			try {
				object = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
				GrafanaPath = object.getString("grafana-path");
				GrafanaPort = object.getInt("grafana-port");
				APIServerPort = object.getInt("api-server-port");
				JSONDataSourceName = object.getString("json-datasource-name");
				DashboardProviderName = object.getString("dashboard-provider-name");
				CollectorDelay = object.getInt("collector-delay");
			} catch (JSONException e) {
				System.out.println("Il file di configurazione non è formattato correttamente");
				System.exit(1);
			} catch (IOException e) {
				System.out.println("Il file di configurazione non è stato trovato");
				System.exit(1);
			}
	}

}
