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

		String response = null;
		
		if(params == null) {
			params = new HashMap<String, String>();
		}

		try{
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			String param;
			for(Map.Entry<String, String> entry : params.entrySet()){
				param = URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + entry.getValue();
				wr.write(param);
			}
			wr.flush();
			response = getResponse(conn);
			wr.close();

		} catch (Exception e) {
			log.error("Unable to perform request", e);
		}
		return response;
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
