package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static void main(String[] args) throws IOException{
		String command = "C:\\Users\\mardon\\Desktop\\grafana-6.2.5\\bin\\grafana-server.exe";
		ProcessBuilder pb = new ProcessBuilder(command);
	    System.out.println(System.getProperty("user.dir"));
		runProcess(pb);
	}
	
	private static void runProcess(ProcessBuilder pb) throws IOException {
	    pb.redirectErrorStream(true);
	    pb.directory(new File("C:\\Users\\mardon\\\\Desktop\\grafana-6.2.5\\bin"));
	    Process p = pb.start();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    String line;
	    while ((line = reader.readLine()) != null) {
	        System.out.println(line);
	    }
	}

}
