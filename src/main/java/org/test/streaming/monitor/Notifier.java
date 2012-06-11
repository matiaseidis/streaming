package org.test.streaming.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.test.streaming.Conf;

public class Notifier {
	
	private static final String URL_BASE = Conf.NOTIFIER_URL_BASE; 
	public Logger logger = Logger.getLogger(getClass());

	public Notifier(){
		System.out.println("Notifier.Notifier()");
	}
	
	public String addUser(String userId, String ip, int port){
		
		Assert.state(StringUtils.isNotEmpty(userId));
		Assert.state(StringUtils.isNotEmpty(ip));
		Assert.state(port > 0);
		
		return sendRequest("user/add/"+userId+"/"+ip+"/"+port);	
	}
	
	public String removeCacho(String userId, String videoId, long from, long lenght){
		Assert.state(StringUtils.isNotEmpty(userId));
		Assert.state(StringUtils.isNotEmpty(videoId));
		Assert.state(lenght > 0);

		return sendRequest("cacho/remove/"+userId+"/"+videoId+"/"+from+"/"+lenght);
	}
	
	public String addCacho(String userId, String ip, int port, String videoId, String fileName, long from, long lenght){
		Assert.state(StringUtils.isNotEmpty(userId));
		Assert.state(StringUtils.isNotEmpty(videoId));
		Assert.state(StringUtils.isNotEmpty(fileName));
		
		return sendRequest("cacho/add/"+userId+"/"+ip+"/"+port+"/"+videoId+"/"+fileName+"/"+from+"/"+lenght);
	}
	
	
	
	public String listCachos(String videoId){
		
		Assert.state(StringUtils.isNotEmpty(videoId));

		return sendRequest("cacho/list/"+videoId);	
	}

	public String listVideos(String videoId){
		
		Assert.state(StringUtils.isNotEmpty(videoId));
		
		return sendRequest("video/list");	
	}

	public String getGrafo(String videoId){
		
		Assert.state(StringUtils.isNotEmpty(videoId));
		
		return sendRequest("grafo/"+videoId);
	}
	
	private String sendRequest(String urlSuffix) {
		
		StringBuilder sb = new StringBuilder();
		URI uri = null;
		URL url = null;
		try {
			uri = new URI(URL_BASE+urlSuffix);
			url = uri.toURL();
			URLConnection urlConnection = url.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							urlConnection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null){ 
				sb.append(inputLine);
			}
			in.close();
			
		} catch ( IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return sb.toString();
	}
	
}
