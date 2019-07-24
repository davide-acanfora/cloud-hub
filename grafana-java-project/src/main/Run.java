package main;

import grafana.dashboard.DashboardConfig;
import grafana.datasource.JSONDataSource;

public class Run {

	private static final String SERVER_FOLDER = "C:\\Users\\mardon\\Desktop\\grafana-6.2.5";
	
	public static void main(String[] args) {
		JSONDataSource dataSource = new JSONDataSource("Test", "http://localhost:8081", JSONDataSource.SERVER);
		dataSource.createConfig(SERVER_FOLDER);
		DashboardConfig dashboardConfig = new DashboardConfig("Provider");
		dashboardConfig.createConfig(SERVER_FOLDER);
	}

}
