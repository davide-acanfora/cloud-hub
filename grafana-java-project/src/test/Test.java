package test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException  {
		Random random = new Random();
		for (int i=0; i<15; i++) {
			for (int j=0; j<random.nextInt(5)+15; j++) {
				URL url = new URL("https://grafana-app.azurewebsites.net/api/grafanaTestBello?code=QrILWEBUOoRCBaH76bHwJSb8x0/jXKc3KQgSqXwRl6d5D6W/Fa38QA==&name=Ciccio");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				System.out.println(j+") "+con.getResponseCode());
			}
		Thread.sleep(60000);
		System.out.println(i+") Ho aspettato fratellò");
		}
	}
}
