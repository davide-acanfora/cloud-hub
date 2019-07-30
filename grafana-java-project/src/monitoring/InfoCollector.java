package monitoring;

import java.util.ArrayList;
import java.util.HashMap;

import config.Config;
import grafana.misc.GrafanaTimeseriePoint;

public class InfoCollector implements Runnable{
	//HashMap che mantiene e i suoi rispettivi Point, accessibile tramite nome della metrica
	public static HashMap<Metric, ArrayList<GrafanaTimeseriePoint>> metricsMap = new HashMap<Metric, ArrayList<GrafanaTimeseriePoint>>();
	//Delay del thread che colleziona i punti delle metriche
	private final int delay = Config.CollectorDelay;

	static {
		//Inizializzo l'HashMap con le metriche supportate attualmente dal programma.
		//La chiave dell'HashMap è la metrica stessas
		metricsMap.put(Metric.CPU, new ArrayList<GrafanaTimeseriePoint>());
		metricsMap.put(Metric.MEMORY, new ArrayList<GrafanaTimeseriePoint>());
	}
	
	//Thread che colleziona i punti
	public void run() {
		//All'infinito
		while(true)
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
}
