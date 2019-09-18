package grafana;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
//import java.util.ArrayList;
import java.util.ArrayList;

import grafana.conf.Conf;
import grafana.dashboard.AWSDashboard;
import grafana.dashboard.LocalDashboard;
import grafana.datasource.AzureMonitorDataSource;
import grafana.datasource.CloudWatchDataSource;
import grafana.datasource.JSONDataSource;
import monitoring.LocalMonitoringWebServer;
import net.lingala.zip4j.ZipFile;
import test.Test;

//Classe che rappresenta il server di Grafana
public class Grafana {
	private Process grafana;
	private int httpPort = 3000; //Porta su cui ascolta la console di Grafana
	private String folderPath;
	private boolean consoleLog;	
	private final static String serverName = "server.zip";
	
	private LocalDashboard localDashboard;
	private LocalMonitoringWebServer localWebServer;
	
	private AWSDashboard awsDashboard;

	//Costruttore
	public Grafana(int httpPort, boolean consoleLog) {
		this.httpPort = httpPort;
		this.consoleLog = consoleLog;
		this.folderPath = System.getProperty("java.io.tmpdir")+"/grafana";
		
		System.out.println("Deploy server Grafana...");
		if (!this.deployServerFolder()) {
			System.out.println("Errore di inizializzazione - impossibile eseguire il deploy di Grafana");
			System.exit(-1);
		}
		
		Conf conf = new Conf(this.httpPort);
		conf.createConfig(this.folderPath);
	}
	
	//Metodo che avvia il server facendone prima il deploy
	public void start() throws IOException {
		System.out.println("Avvio server Grafana...");
		
		//Avvio dell'eseguibile di Grafana
		String command = "./grafana-server";
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);	
	    pb.directory(new File(folderPath + "/bin"));
	    grafana = pb.start();
	    
	    Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Stopping Grafana...");
				grafana.destroy();
				System.out.println("Cleaning up server files...");
				try {
					Grafana.deleteFile(new File(folderPath));
				}catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	    
	    System.out.println("Grafana in ascolto all'indirizzo http://localhost:" + this.httpPort);
	    
	    if(consoleLog) {
		    Thread grafanaOutputPrinter = new Thread(new GrafanaOutputPrinter(grafana));
		    grafanaOutputPrinter.start();
	    }
	    
	}
	
	private boolean deployServerFolder() {
		//Ottengo l'archivio dei file server di Grafana posto all'interno dell'eseguibile Java
		InputStream input = Test.class.getResourceAsStream("/server/"+serverName);
		//Imposto come destinazione dello scompattamento la cartella dei file temporanei di sistema
		String destination = System.getProperty("java.io.tmpdir");
		
		try {
			//Copio prima l'archivio sul disco
            Files.copy(input, Paths.get(destination+"/"+serverName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
        	return false;
        }
		
		try {
			//Lo estraggo
			ZipFile zip = new ZipFile(destination+"/"+serverName);
			zip.extractAll(destination);
			//Cancello l'archivio ormai inutile
			Files.deleteIfExists(Paths.get(destination+"/"+serverName));			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		//Tutto OK
		return true;
	}
	
	private static void deleteFile(File element) {
	    if (element.isDirectory()) {
	        for (File sub : element.listFiles()) {
	            deleteFile(sub);
	        }
	    }
	    element.delete();
	}
	
	public void keepRunning() throws InterruptedException {
		this.grafana.waitFor();
	}
	
	public void enableLocalMonitoring(int apiPort, int collectorDelay) {
		JSONDataSource jsonDataSource = new JSONDataSource("JSONDataSource", "http://localhost:"+apiPort, JSONDataSource.SERVER);
		jsonDataSource.createConfig(this.folderPath);
		
		this.localDashboard = new LocalDashboard(jsonDataSource);
		this.localDashboard.createConfig(this.folderPath);
		
		this.localWebServer = new LocalMonitoringWebServer(apiPort, collectorDelay);
	    this.localWebServer.start();
	}
	
	public void disableLocalMonitoring() {
		this.localDashboard.deleteConfig();
		this.localWebServer.stop();
	}
	
	public void enableCloudWatchMonitoring(String accessKey, String secretKey, String defaultRegion) {
		CloudWatchDataSource cloudWatchDataSource = new CloudWatchDataSource("CloudWatch", accessKey, secretKey, defaultRegion);
		cloudWatchDataSource.createConfig(this.folderPath);
		
		this.awsDashboard = new AWSDashboard(cloudWatchDataSource);
		this.awsDashboard.createConfig(this.folderPath);
	}
	
	public void enableAzureMonitoring(String tenantId, String clientId, String clientSecret, String defaultSubscription, String applicationId, String apiKey) {
		AzureMonitorDataSource azureMonitorDataSource = new AzureMonitorDataSource("Azure Monitor", tenantId, clientId, clientSecret, defaultSubscription, applicationId, apiKey);
		azureMonitorDataSource.createConfig(this.folderPath);
		
		//dashboard
	}
	
	public void addAWSFunction(String functionName) {
		this.awsDashboard.addFunction(functionName);
		this.awsDashboard.createConfig(this.folderPath);
	}
	
	public void addAWSFunction(ArrayList<String> functions) {
		for (String function : functions)
			this.awsDashboard.addFunction(function);
		this.awsDashboard.createConfig(this.folderPath);
	}

}