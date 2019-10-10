package grafana;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GrafanaOutputPrinter implements Runnable{
	private Process grafana;
	
	public GrafanaOutputPrinter(Process grafana) {
		this.grafana = grafana;
	}
	
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(grafana.getInputStream()));
	    String line;
	    try {
			while ((line = reader.readLine()) != null) {
			    System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
