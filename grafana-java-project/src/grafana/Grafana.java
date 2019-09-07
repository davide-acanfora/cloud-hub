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

//Classe che rappresenta il server di Grafana
public class Grafana {
	private int httpPort = 3000; //Porta su cui ascolta la console di Grafana
	private int apiPort; //Porta su cui ascolta il server che fornisce l'API 
	private String jsonDataSourceName; //Nome del datasource di Grafana da utilizzare
	private String dashboardProviderName; //Nome della dashboard di Grafana da utilizzare
	private int collectorDelay; //Delay del thread che raccoglie le informazioni del sistema
	private String folderPath;
	
	public Grafana(int httpPort, int apiPort, String jsonDataSourceName, String dashboardProviderName, int collectorDelay) {
		this.httpPort = httpPort;
		this.apiPort = apiPort; 
		this.jsonDataSourceName = jsonDataSourceName; 
		this.dashboardProviderName = dashboardProviderName; 
		this.collectorDelay = collectorDelay; 
	}
	
	public void start() throws IOException {
		System.out.println("Inizializzazione server Grafana...");
		if (!deployServer()) {
			System.out.println("Errore di inizializzazione - impossibile eseguire il deploy di Grafana");
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
		
		//Avvio dell'eseguibile di Grafana
		String command = "./grafana-server";
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
		//Ottengo l'archivio dei file server di Grafana posto all'interno dell'eseguibile Java
		InputStream input = Test.class.getResourceAsStream("/server/server.zip");
		//Imposto come destinazione dello scompattamento la cartella dei file temporanei di sistema
		String destination = System.getProperty("java.io.tmpdir");
		
		try {
			//Copio prima l'archivio sul disco
            Files.copy(input, Paths.get(destination+"/server.zip"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
        	return false;
        }
		
		try {
			//Lo estraggo
			ZipFile zip = new ZipFile(destination+"/server.zip");
			zip.extractAll(destination);
			//Cancello l'archivio ormai inutile
			Files.deleteIfExists(Paths.get(destination+"/server.zip"));
			//I file di Grafana sono ora contenuti nel seguente percorso
			this.folderPath = destination+"/grafana";
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		//Tutto OK
		return true;
	}

}