package main;

import java.io.IOException;
import grafana.Grafana;

public class Run {
	
	public static void main(String[] args) throws IOException {
		Grafana grafana = new Grafana(8080, 8081, "JSONDataSource", "Test", 2000);
		grafana.start();
	}

}
