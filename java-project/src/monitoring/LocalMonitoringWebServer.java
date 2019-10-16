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

//Classe per fornire a Grafana l'API per monitorare il sistema locale
public class LocalMonitoringWebServer{
	private HttpServer apiServer;
	private int port;
	private InfoCollector collector;
	private int collectorDelay;
	private static String searchString = buildSearchStringFromMetrics();
	
	private static String buildSearchStringFromMetrics() {
		//Come da protocollo del JSONDatasource, la risposta della richiesta "/search" deve ritornare un JSONArray contenente i nomi delle metriche messe a disposizione dal programma
		//es. ["CPU", "MEMORY"]
		
		//Inizio array
		JSONArray array = new JSONArray();
		
		//Per ogni metrica supportata dal programma
		for (Metric metrica : Metric.values()) 
			//Inserisco il suo nome all'interno dell'array
			array.put(metrica.name());
		
		return array.toString();
	}
	
	//Costruttore della classe che permette di parametrizzarla con la porta su cui ricevere le richieste di Grafana
	public LocalMonitoringWebServer(int port, int collectorDelay) {
		this.port = port;
		this.collectorDelay = collectorDelay;
	}
	
	public void start() {
		try {
			runServer(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//
	public void runServer(int port) throws IOException {
		this.apiServer = HttpServer.create(new InetSocketAddress(port), 0);
		this.apiServer.createContext("/", new RequestOk());
		this.apiServer.createContext("/query", new RequestQuery());
		this.apiServer.createContext("/search", new RequestSearch());
		//server.createContext("/annotations", new RequestAnnotation());
		//server.createContext("/tag-keys", new RequestTagKeys());
		//server.createContext("/tag-values", new RequestTagValues());
		this.apiServer.setExecutor(null);
		
		//Avvio il thread che colleziona i punti del grafo
		collector = new InfoCollector(this.collectorDelay);
		collector.start();

		this.apiServer.start();
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
		    
			//System.out.println("Request: "+body.toString());
			 
		    //JSONArray che conterr� la risposta da dover mandare a Grafana
		    //L'array deve essere composto da: 
		    //1) un JSONObject contenente la key "target" corrispondente al suo nome
		    //2) un JSONArray di coppie valore-unixtimestamp nella chiave "datapoints"
			JSONArray response = new JSONArray();
						
			try {
				//Il corpo della richiesta in formato String lo converto nel JSON corrispondente
				JSONObject request = new JSONObject(body.toString());
				
				//Array delle metriche (targets) richieste da Grafana
				JSONArray targets = request.getJSONArray("targets");
				
				
				//Itero per ogni metrica (target) richiesta
				for (int i=0; i<targets.length(); i++) {
					JSONObject target = targets.getJSONObject(i);
					
					//Ottengo la metrica dal suo nome contenuto nel JSON
					Metric metrica;
					try {
						//Il nome � contenuto nella key "target" dell'oggetto JSON (target) corrente
						metrica = Metric.valueOf(target.getString("target").toUpperCase());
					} catch (UnsupportedMetricException e) {
						//Pu� succedere se da Grafana viene inserito un nome di una metrica inesistente
						break;
					}
					
					//Se ho gi� collezionato almeno un punto della metrica richiesta, allora la inserisco nella risposta
					if (!InfoCollector.metricsMap.get(metrica).isEmpty())
						//Converto i suoi punti in un oggetto JSON e lo accodo alla response
						response.put(getMetricPointsToTargetJSON(metrica));
				}
				
			} catch (JSONException e) {
				//Pu� succedere se Grafana costruisce una richiesta malformata (raro)
				e.printStackTrace();
				System.exit(-1);
				
				//In futuro: al posto di uscire dal programma, dare una risposta vuota
				//response = new JsonArray();
			}
			
			//System.out.println("Response: "+response.toString());
	
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
				pointsArray.put(coppia);
			}
			//Inserisco la lista dei punti raccolti nell'oggetto target da dover essere inserito nella risposta
			targetResponse.put("datapoints", pointsArray);
			return targetResponse;
		}
	}
	
	public void stop() {
		collector.terminate();
		this.apiServer.stop(0);
	}

}
