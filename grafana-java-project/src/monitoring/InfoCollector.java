package monitoring;

import java.util.ArrayList;
import java.util.HashMap;

import config.Config;
import grafana.misc.GrafanaTimeseriePoint;

public class InfoCollector implements Runnable{
	public static HashMap<String, ArrayList<GrafanaTimeseriePoint>> targetMap = new HashMap<String, ArrayList<GrafanaTimeseriePoint>>();
	private final int delay = Config.CollectorDelay;

	static {
		targetMap.put(SysInfo.CPU, new ArrayList<GrafanaTimeseriePoint>());
		targetMap.put(SysInfo.MEMORY, new ArrayList<GrafanaTimeseriePoint>());
	}
	
	public void run() {
		while(true)
		{
			targetMap.get(SysInfo.CPU).add(new GrafanaTimeseriePoint(SysInfo.getSysInfo(SysInfo.CPU), System.currentTimeMillis()));
			targetMap.get(SysInfo.MEMORY).add(new GrafanaTimeseriePoint(SysInfo.getSysInfo(SysInfo.MEMORY), System.currentTimeMillis()));
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
