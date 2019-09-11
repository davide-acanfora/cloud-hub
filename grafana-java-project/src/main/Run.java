package main;

import java.io.IOException;
import grafana.Grafana;

public class Run {
	
	public static void main(String[] args) throws IOException {
		Grafana grafana = new Grafana(3000, 8081, "JSONDataSource", "Test", 2000);
		grafana.start();
		
		//grafana.stop();
	}

}
