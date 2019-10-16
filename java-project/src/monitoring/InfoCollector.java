package monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import grafana.misc.GrafanaTimeseriePoint;

public class InfoCollector extends Thread{
	//HashMap che mantiene e i suoi rispettivi Point, accessibile tramite nome della metrica
	public static HashMap<Metric, ArrayList<GrafanaTimeseriePoint>> metricsMap = new HashMap<Metric, ArrayList<GrafanaTimeseriePoint>>();
	//Delay del thread che colleziona i punti delle metriche
	private int delay;
	private boolean running;

	static {
		//Inizializzo l'HashMap con le metriche supportate attualmente dal programma.
		//La chiave dell'HashMap � la metrica stessa
		for (Metric metric : Metric.values())
			metricsMap.put(metric, new ArrayList<GrafanaTimeseriePoint>());
	}
	
	public InfoCollector(int delay) {
		this.delay = delay;
		running = true;
	}
	
	//Thread che colleziona i punti
	public void run() {
		//All'infinito
		while(running)
		{
			//Ottengo l'ArrayList di ogni metrica e vi aggiungo un nuovo GrafanaTimeseriePoint contenente valore e tempo corrente
			for(Metric metrica : Metric.values())
				metricsMap.get(metrica).add(new GrafanaTimeseriePoint(SysInfo.getSysInfo(metrica), System.currentTimeMillis()));
			
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void terminate() {
		this.running = false;
	}
}
