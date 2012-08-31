package org.test.streaming.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndexRequester {

	protected static final Log log = LogFactory.getLog(IndexRequester.class);

	private final String urlString;

	public IndexRequester(String url){
		this.urlString = url;
	}

	public String get(){
		
		String response = null;
		URI uri = null;
		URL url = null;
		try {
			uri = new URI(urlString);
			url = uri.toURL();
			response = getResponse(url.openConnection());
			
		} catch (URISyntaxException e) {
			log.error("Invalid request", e);
		} catch ( IOException e) {
			log.error("Unable to perform request", e);
		} 
		return response;
	}

	public String post(Map<String, String> params) {

		StringBuilder response = new StringBuilder();

		if(params == null) {
			params = new HashMap<String, String>();
		}

		try {
		    // Construct data
			String data = "";
			for(Map.Entry<String,String> param: params.entrySet()){
//				data += "&" + URLEncoder.encode(param.getKey(), "UTF-8") + "=" + URLEncoder.encode(param.getValue(), "UTF-8");
				data += "&" + param.getKey() + "=" + param.getValue();
				
			}
			data = data.substring(1);
		    System.out.println(data);

		    // Send data
		    URL url = new URL(urlString);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		        response.append(line);
		    }
		    wr.close();
		    rd.close();
		} catch (Exception e) {
			log.error("Unable to perform request", e);
		}
		String resp = response.toString();
		log.info(resp);
		return resp;
	}
	
	private String getResponse(URLConnection conn) throws IOException {
	
		String line= null;
		StringBuilder sb = new StringBuilder();
		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		return sb.toString();
	}

}
