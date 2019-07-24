package monitoring;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import grafana.misc.GrafanaTimeseriePoint;

public class LocalMonitoringWebServer {
	
	public static final String MEMORY = "memory";
	public static final String CPU = "cpu";

	private static HashMap<String, ArrayList<GrafanaTimeseriePoint>> targetMap = new HashMap<String, ArrayList<GrafanaTimeseriePoint>>();
	
	private static String search = "[\"" + LocalMonitoringWebServer.MEMORY + "\",\"" + LocalMonitoringWebServer.CPU + "\"]";
	
	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
		server.createContext("/", new RequestOk());
		server.createContext("/query", new RequestQuery());
		server.createContext("/search", new RequestSearch());
		//server.createContext("/annotations", new RequestAnnotation());
		//server.createContext("/tag-keys", new RequestTagKeys());
		//server.createContext("/tag-values", new RequestTagValues());
		server.setExecutor(null);
		
		targetMap.put(LocalMonitoringWebServer.MEMORY, new ArrayList<GrafanaTimeseriePoint>());
		targetMap.put(LocalMonitoringWebServer.CPU, new ArrayList<GrafanaTimeseriePoint>());
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		server.start();
	}
	
	static class RequestOk implements HttpHandler{
		@Override
		public void handle(HttpExchange t) throws IOException {
			t.sendResponseHeaders(200, 0);
			OutputStream os = t.getResponseBody();
			os.write("".getBytes());
			os.close();
		}
	}
	
	static class RequestSearch implements HttpHandler{
		@Override
		public void handle(HttpExchange t) throws IOException {
			t.sendResponseHeaders(200, search.length());
			OutputStream os = t.getResponseBody();
			os.write(search.getBytes());
			os.close();	
		}
	}
	
	static class RequestQuery implements HttpHandler{
		@Override
		public void handle(HttpExchange t) throws IOException {
			StringBuilder body = new StringBuilder();
		    try (InputStreamReader reader = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8)) {
		        char[] buffer = new char[256];
		        int read;
		        while ((read = reader.read(buffer)) != -1) {
		            body.append(buffer, 0, read);
		        }
		    }
		    
			//System.out.println(body.toString());
			
			JSONObject jsonObject;
			JSONArray response = new JSONArray();
			
			try {
				jsonObject = new JSONObject(body.toString());
				JSONArray targets = jsonObject.getJSONArray("targets");
				for (int i=0; i<targets.length(); i++) {
					JSONObject target = targets.getJSONObject(i);
					String key = "";
					try {
						key = target.getString("target");
					} catch (JSONException e) {
						break;
					}
					
					if (!target.isNull("data")) {
						JSONObject data = target.getJSONObject("data");
						if (data.has("create")) 
							if (data.getBoolean("create")) 
								targetMap.get(key).add(new GrafanaTimeseriePoint(SysInfo.getSysInfo(key), System.currentTimeMillis()));						
					}
					
					if (!targetMap.get(key).isEmpty()) {
						JSONObject object = getTargetPoints(key);
						response.put(object);
					}
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			
			
			//System.out.println(response.toString());
			
			t.sendResponseHeaders(200, response.toString().length());
			OutputStream os = t.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
		}
		
		private JSONObject getTargetPoints(String target) throws JSONException {
			JSONObject object = new JSONObject();
			object.put("target", target);
			JSONArray points = new JSONArray();
			for (GrafanaTimeseriePoint point : targetMap.get(target)) {
				JSONArray JSONpoint = new JSONArray();
				JSONpoint.put(point.getValue());
				JSONpoint.put(point.getTimestamp());
				points.put(JSONpoint);
			}
			object.put("datapoints", points);
			return object;
		}
	}

}
