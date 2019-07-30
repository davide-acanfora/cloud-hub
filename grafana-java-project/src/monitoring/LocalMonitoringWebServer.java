package monitoring;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import grafana.misc.GrafanaTimeseriePoint;


//Classe per fornire a Grafana l'API per ottenere i punti in formato JSON
public class LocalMonitoringWebServer implements Runnable{
	private int port;
	
	private static String searchString;
	static {
		//Come da protocollo del JSONDatasource, la risposta della richiesta "/search" deve ritornare l'array dei nomi delle metriche messe a disposizione dal programma
		//es. ["CPU", "MEMORY"]
		//searchString = "[\"" + SysInfo.MEMORY + "\",\"" + SysInfo.CPU + "\"]"
		
		//Inizio array
		searchString = "[";
		//Per ogni metrica
		for (Metric metrica : Metric.values()) {
			//Inserisco il suo nome tra virgolette seguito da una virgola
			searchString += "\"" + metrica.name() + "\",";
		}
		
		//Rimuovo l'ultima virgola, non necessaria
		searchString = searchString.substring(0, searchString.length()-1);
		
		//Fine array
		searchString += "]";
	}
	
	//Costruttore della classe che permette di parametrizzarla con la porta su cui ricevere le richieste di Grafana
	public LocalMonitoringWebServer(int port) {
		this.port = port;
	}
	
	public void run() {
		try {
			runServer(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//
	public void runServer(int port) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new RequestOk());
		server.createContext("/query", new RequestQuery());
		server.createContext("/search", new RequestSearch());
		//server.createContext("/annotations", new RequestAnnotation());
		//server.createContext("/tag-keys", new RequestTagKeys());
		//server.createContext("/tag-values", new RequestTagValues());
		server.setExecutor(null);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Stopping Web Server...");
				server.stop(0);
			}
		});
		
		//Avvio il thread che colleziona i punti del grafo
		Thread collector = new Thread(new InfoCollector());
		collector.start();

		server.start();
	}
	
	public class RequestOk implements HttpHandler{
		@Override
		public void handle(HttpExchange t) throws IOException {
			t.sendResponseHeaders(200, 0);
			OutputStream os = t.getResponseBody();
			os.write("".getBytes());
			os.close();
		}
	}
	
	public class RequestSearch implements HttpHandler{
		@Override
		public void handle(HttpExchange t) throws IOException {
			t.sendResponseHeaders(200, searchString.length());
			OutputStream os = t.getResponseBody();
			os.write(searchString.getBytes());
			os.close();	
		}
	}
	
	
	//Classe per rispondere alle richieste all'indirizzo /query
	public class RequestQuery implements HttpHandler{
		@Override
		public void handle(HttpExchange t) throws IOException {
			
			//Ottengo il corpo della richiesta
			StringBuilder body = new StringBuilder();
		    try (InputStreamReader reader = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8)) {
		        char[] buffer = new char[256];
		        int read;
		        while ((read = reader.read(buffer)) != -1) {
		            body.append(buffer, 0, read);
		        }
		    }
		    
			//System.out.println(body.toString());
			
		    
		    //JSONArray che conterrà la risposta da dover mandare a Grafana
		    //L'array deve essere composto da: 
		    //1) un JSONObject contenente la key "target" corrispondente al suo nome
		    //2) un JSONArray di coppie valore-tempo nella chiave "datapoints"
			JSONArray response = new JSONArray();
			
			
			//Dal corpo della richiesta in formato String lo converto nel JSON corrispondente
			try {
				JSONObject request = new JSONObject(body.toString());
				//Array delle metriche (targets) richieste da Grafana
				JSONArray targets = request.getJSONArray("targets");
				
				//Itero per ogni metrica (target) richiesta
				for (int i=0; i<targets.length(); i++) {
					JSONObject target = targets.getJSONObject(i);
					
					//Ottengo la metrica dal suo nome contenuto nel JSON
					Metric metrica;
					try {
						//Il nome è contenuto nella key "target" dell'oggetto JSON corrente
						metrica = Metric.valueOf(target.getString("target"));
					} catch (JSONException e) {
						break;
					}
					
					//Se ho già ho collezionato almeno un punto della metrica richiesta, allora la inseriso nella risposta
					if (!InfoCollector.metricsMap.get(metrica).isEmpty()) {
						//Converto i punti raccolti fino a questo momento in un oggetto JSON
						JSONObject targetResponse = getMetricPointsToTargetJSON(metrica);
						response.put(targetResponse);
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
		
		//Metodo per convertire i punti raccolti di una specifica metrica in un oggetto JSON da restituire a Grafana
		private JSONObject getMetricPointsToTargetJSON(Metric metric) throws JSONException {
			
			//Costruisco il target di risposta
			JSONObject targetResponse = new JSONObject();
			
			//Inserisco il suo nome all'interno della key "target"
			targetResponse.put("target", metric.name());
			
			//Array per contenere le coppie valore-tempo
			JSONArray pointsArray = new JSONArray();
			
			//Per ogni punto raccolto fino a quel momento per quella rispettiva metrica
			for (GrafanaTimeseriePoint point : InfoCollector.metricsMap.get(metric)) {
				
				//Creo la coppia corrispondente valore-tempo
				JSONArray coppia = new JSONArray();
				coppia.put(point.getValue());
				coppia.put(point.getTimestamp());
				
				//E l'aggiungo all'array dei punti
				pointsArray.put(pointsArray);
			}
			
			//Inserisco la lista dei punti raccolti nell'oggetto da dover essere inserito nella risposta
			targetResponse.put("datapoints", pointsArray);
			return targetResponse;
		}
	}

}
