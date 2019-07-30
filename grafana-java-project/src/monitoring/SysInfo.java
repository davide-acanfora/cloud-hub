package monitoring;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

//Classe che si interfaccia con il sistema per ottenerne i valori attuali
public class SysInfo{
	
	//Interfaccia fornita da Java per la gestione del sistema operativo sul quale la JVM è in esecuzione
	private static OperatingSystemMXBean systemInterface = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
	
	//Metodo per ottenere il valore attuale della metrica passata come parametro
	public static double getSysInfo(Metric metric) {
		
		switch (metric) {
		case CPU:
			return getCpuUsage();
		case MEMORY:
			return getMemoryUsage();
		default:
			throw new UnsupportedMetricException("La metrica '"+metric.name()+"' richiesta non è implementata");
		}		
	}

	//Ritorna in percentuale la quantità di RAM usata
	private static double getMemoryUsage() {
		return 100 - (systemInterface.getFreePhysicalMemorySize() * 100 / systemInterface.getTotalPhysicalMemorySize());
	}

	//Ritorna in percentuale l'utilizzo complessivo della CPU
	private static double getCpuUsage(){
		double usage = systemInterface.getSystemCpuLoad()*100;
		return usage <= 100 ? usage > 0 ? usage : 0 : 100;
	}

	//debug
	public static void main(String[] args) throws InterruptedException{
		while (true) {
			System.out.println(getCpuUsage());
			Thread.sleep(1000);
		}
	}
}