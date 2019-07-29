package monitoring;
import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class SysInfo{
	
	public static final String MEMORY = "memory";
	public static final String CPU = "cpu";
	
	private static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
	
	public static double getSysInfo(String metric) {
		if (metric.equals(LocalMonitoringWebServertTest.CPU))
			return getCpuUsage();
		else if (metric.equals(LocalMonitoringWebServertTest.MEMORY))
			return getMemoryUsage();
		else 
			return -1;
	}

	private static double getMemoryUsage() {
		double currentMemory = osBean.getFreePhysicalMemorySize();
		double totalMemory = osBean.getTotalPhysicalMemorySize();
		return 100-(currentMemory*100/totalMemory);
	}

	
	private static double getCpuUsage(){
		double usage = osBean.getSystemCpuLoad()*100;
		return usage <= 100 ? usage > 0 ? usage : 0 : 100;
	}

	public static void main(String[] args) throws InterruptedException{
		while (true) {
			System.out.println(getCpuUsage());
			Thread.sleep(1000);
		}
	}
}