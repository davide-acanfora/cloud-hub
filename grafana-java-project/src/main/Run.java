package main;

import java.io.IOException;
import grafana.Grafana;

public class Run {
	
	public static void main(String[] args) throws IOException {
		Grafana grafana = new Grafana(3000, false);
		grafana.enableLocalMonitoring(8080, 12000);
		grafana.enableCloudWatchMonitoring("test4", "test4", "us-east-1");
		grafana.start();
		//grafana.stop();
	}
	
}
