package monitoring;
import java.lang.management.ManagementFactory;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.sun.management.OperatingSystemMXBean;

//Classe che si interfaccia con il sistema per ottenerne i valori attuali
public class SysInfo{
	
	//Interfaccia fornita da Java per la gestione del sistema operativo sul quale la JVM � in esecuzione
	private static OperatingSystemMXBean systemInterface = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
	
	//Metodo per ottenere il valore attuale della metrica passata come parametro
	public static double getSysInfo(Metric metric) {
		
		switch (metric) {
		case CPU:
			return getCpuUsage();
		case MEMORY:
			return getMemoryUsage();
		case CPU_TEMPERATURE:
			return getCpuTemperature();
		default:
			throw new UnsupportedMetricException("La metrica '"+metric.name()+"' richiesta non � implementata");
		}		
	}

	//Ritorna in percentuale la quantit� di RAM usata
	private static double getMemoryUsage() {
		return 100 - (systemInterface.getFreePhysicalMemorySize() * 100 / systemInterface.getTotalPhysicalMemorySize());
	}

	//Ritorna in percentuale l'utilizzo complessivo della CPU
	private static double getCpuUsage(){
		double usage = systemInterface.getSystemCpuLoad()*100;
		return usage <= 100 ? usage > 0 ? usage : 0 : 100;
	}
	
	private static double getCpuTemperature(){
		Cpu cpu = JSensors.get.components().cpus.get(0);
		return cpu.sensors.temperatures.get(0).value/10;
	}

	//debug
	public static void main(String[] args) throws InterruptedException{
		while (true) {
			System.out.println(getCpuUsage());
			Thread.sleep(1000);
		}
	}
}