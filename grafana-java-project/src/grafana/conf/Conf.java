package grafana.conf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import grafana.Configurable;

public class Conf implements Configurable{
	private File file;
	private int port;
	
	public Conf(int port) {
		this.port = port;
	}

	@Override
	public void createConfig(String grafanaPath) {
		String conf = "[server]\n"
				+ "http_port = " + this.port + "\n"
				+ "[auth.anonymous]\n" 
				+ "enabled = true";
		file = new File(grafanaPath+"/conf/custom.ini");
		Writer writer = null;
		
		try{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			writer.write(conf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void deleteConfig() {
		// TODO Auto-generated method stub
		
	}

}
