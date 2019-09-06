package grafana;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import grafana.dashboard.DashboardConfig;
import grafana.dashboard.DashboardProvider;
import grafana.datasource.JSONDataSource;
import monitoring.LocalMonitoringWebServer;
import net.lingala.zip4j.ZipFile;
import test.Test;

public class Grafana {
	private int httpPort;
	private int apiPort;
	private String jsonDataSourceName;
	private String dashboardProviderName;
	private int collectorDelay;
	private String folderPath;
	
	public Grafana(int httpPort, int apiPort, String jsonDataSourceName, String dashboardProviderName,
			int collectorDelay) {
		super();
		this.httpPort = httpPort;
		this.apiPort = apiPort;
		this.jsonDataSourceName = jsonDataSourceName;
		this.dashboardProviderName = dashboardProviderName;
		this.collectorDelay = collectorDelay;
	}
	
	public void start() throws IOException {
		System.out.println("Inizializzazione server Grafana...");
		if (!deployServer()) {
			System.out.println("Errore di inizializzazione - impossibile reperire il server Grafana");
			System.exit(-1);
		}
		
		ArrayList<Configurable> configurables = new ArrayList<Configurable>();
		
		JSONDataSource dataSource = new JSONDataSource(this.jsonDataSourceName, "http://localhost:"+this.apiPort, JSONDataSource.SERVER);
		dataSource.createConfig(this.folderPath);
		configurables.add(dataSource);
		
		DashboardProvider dashboardProvider = new DashboardProvider(this.dashboardProviderName);
		dashboardProvider.createConfig(this.folderPath);
		configurables.add(dashboardProvider);
		
		DashboardConfig dashboardConfig = new DashboardConfig(this.jsonDataSourceName);
		dashboardConfig.createConfig(this.folderPath);
		configurables.add(dashboardConfig);
		
		String command = "./grafana-server";
		//String[] array = {command, "--homepath", Config.GrafanaPath};
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);	
	    pb.directory(new File(this.folderPath + "/bin"));
	    Process grafana = pb.start();
	    
	    Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Stopping Grafana...");
				grafana.destroy();
				System.out.println("Cleaning up configuration files...");
				for (Configurable configurable : configurables)
					configurable.deleteConfig();
			}
		});
	    
	    Thread grafanaOutputPrinter = new Thread(new GrafanaOutputPrinter(grafana));
	    grafanaOutputPrinter.start();
	    Thread localWebServer = new Thread(new LocalMonitoringWebServer(this.apiPort, this.collectorDelay));
	    localWebServer.start();
	    
	    System.out.println("Grafana in ascolto all'indirizzo http://localhost:" + this.httpPort + "/d/isislab?refresh=2s");
	}
	
	private boolean deployServer() {
		InputStream input = Test.class.getResourceAsStream("/server/server.zip");
		String destination = System.getProperty("java.io.tmpdir");
		
		try {
            Files.copy(input, Paths.get(destination+"/server.zip"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
        	return false;
        }
		
		try {
			ZipFile zip = new ZipFile(destination+"/server.zip");
			zip.extractAll(destination);
			Files.deleteIfExists(Paths.get(destination+"/server.zip"));
			this.folderPath = destination+"/grafana";
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}