package it.slamm.benchmark.alchemy;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;

public class AlchemyAPIEntityFinder {

	private static EtmMonitor monitor;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static String alchemyTextGetRelations(String content) throws IOException {

		final String API_KEY = "5377d672d6cbdc7d6f13b7791968f2f342f976c5";
		final String TEXT = URLEncoder.encode(content, "UTF-8");
		final String OUTPUT = "json";
		final String USER_AGENT = "Mozilla/5.0";
	
		EtmPoint point1 = monitor.createPoint("AlchemyAPI NameExtraction - preparing and sending request");

		String url = "http://access.alchemyapi.com/calls/text/TextGetRankedNamedEntities";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Accept-encoding", "gzip");
 
		String urlParameters = "apikey=" + API_KEY + 
				"&text=" + TEXT + 
				"&maxRetrieve=100" +
				"&outputMode=" + OUTPUT;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		
		point1.collect();
		EtmPoint point2 = monitor.createPoint("AlchemyAPI NameExtraction - getting response");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(new GZIPInputStream(con.getInputStream())));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		point2.collect();
		//print result
		return new String(response.toString());


		
	}
	
	public static void main(String[] args) throws IOException {
		BasicEtmConfigurator.configure();
	    monitor = EtmManager.getEtmMonitor();
	    monitor.start();
		for(int i=1; i<=3; i++) {
			String documentStr = new String(Files.readAllBytes(Paths.get("news/" + i +".txt")));
			System.out.println(alchemyTextGetRelations(documentStr));
			System.out.println();
		}
		monitor.render(new SimpleTextRenderer());
		monitor.stop();
	}

}
