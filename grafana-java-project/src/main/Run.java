package main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import config.Config;
import grafana.Configurable;
import grafana.GrafanaOutputPrinter;
import grafana.dashboard.DashboardConfig;
import grafana.dashboard.DashboardProvider;
import grafana.datasource.JSONDataSource;
import monitoring.LocalMonitoringWebServer;

public class Run {
	
	public static void main(String[] args) throws IOException {
		ArrayList<Configurable> configurables = new ArrayList<Configurable>();
		
		JSONDataSource dataSource = new JSONDataSource(Config.JSONDataSourceName, "http://localhost:"+Config.APIServerPort, JSONDataSource.SERVER);
		dataSource.createConfig(Config.GrafanaPath);
		configurables.add(dataSource);
		
		DashboardProvider dashboardProvider = new DashboardProvider(Config.DashboardProviderName);
		dashboardProvider.createConfig(Config.GrafanaPath);
		configurables.add(dashboardProvider);
		
		DashboardConfig dashboardConfig = new DashboardConfig();
		dashboardConfig.createConfig(Config.GrafanaPath);
		configurables.add(dashboardConfig);
		
		String command = Config.GrafanaPath + "\\bin\\grafana-server.exe";
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);	
	    pb.directory(new File(Config.GrafanaPath + "\\bin"));
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
	    
	    Thread localWebServer = new Thread(new LocalMonitoringWebServer(Config.APIServerPort));
	    localWebServer.start();
	    
	    System.out.println("Grafana in ascolto all'indirizzo http://localhost:" + Config.GrafanaPort + "/d/isislab?refresh=2s");
	}

}
