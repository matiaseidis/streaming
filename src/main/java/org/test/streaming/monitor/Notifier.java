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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.test.streaming.Conf;

import com.google.gson.Gson;

public class Notifier {
	
	private final String urlBase;
	public Logger logger = Logger.getLogger(getClass());
	private Conf conf;
	
	public Notifier(Conf conf){
		this.conf = conf;
		urlBase = conf.getNotifierUrl();
		System.out.println("Notifier.Notifier()");
	}
	
	
	
	public List<String> listChunks(String videoId){
		
//		Assert.state(StringUtils.isNotEmpty(videoId));

		String response = sendRequest("video/getChunks/"+videoId+"/"+conf.get("test.user.id"));
		
		Gson gson = new Gson();
		List<String> chunkIds = gson.fromJson(response, List.class);
		
		return chunkIds;  	
	}

	private List<String> toCkunkList(String response) {
		
		List<String> result = new ArrayList<String>(); 
		
		return result;
	}



	public String listVideos(String videoId){
		
//		Assert.state(StringUtils.isNotEmpty(videoId));
		
		return sendRequest("video/list");	
	}

	public String getGrafo(String videoId, String userId){
		
//		Assert.state(StringUtils.isNotEmpty(videoId));
//		Assert.state(StringUtils.isNotEmpty(userId));
		
		return sendRequest("grafo/"+videoId+"/"+userId);
	}
	
	
	private String sendRequest(String urlSuffix) {
		
		StringBuilder sb = new StringBuilder();
		URI uri = null;
		URL url = null;
		try {
			uri = new URI(urlBase+urlSuffix);
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

	public String registerParts(String fileName, String chunks) {
//		Assert.state(StringUtils.isNotEmpty(fileName));
//		Assert.state(StringUtils.isNotEmpty(chunks));
		
		return sendRequest("video/registerChunks/"+fileNameFromPart(fileName)+"/"+conf.get("test.user.id")+"/"+chunks);	
		
	}

	private String fileNameFromPart(String fileName) {
		// Luther.S02E01.720p.HDTV.x264-3.mp4-67108864-67108864.part
		String[] splittedFileName = fileName.split("-");
		String result = StringUtils.EMPTY;
		int pieces = splittedFileName.length-2;
		for(int i=0; i<pieces; i++) {
			result +=splittedFileName[i]+"-";
		}
		result = result.substring(0, result.length()-1);
		System.out.println(result);
		return result;
	}



	public String registerVideo(String videoId, String fileName, long lenght, String chunks) {
//		Assert.state(StringUtils.isNotEmpty(fileName));
//		Assert.state(StringUtils.isNotEmpty(videoId));
//		Assert.state(StringUtils.isNotEmpty(chunks));
		//register/{videoId}/{fileName}/{lenght}/{chunks}/{userId}", method = RequestMethod.POST)
		String line= null;
		try {
		    // Construct data
		    String data = URLEncoder.encode("chunks", "UTF-8") + "=" + chunks;
		    
		    System.out.println(data);
		    
		    // Send data
		    URL url = new URL(conf.getNotifierUrl()+"video/register/"+videoId+"/"+fileName+"/"+lenght+"/"+conf.get("test.user.id"));
		    System.out.println(url.toString());
		    System.out.println(url.toURI());
		    
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    while ((line = rd.readLine()) != null) {
		        System.out.print(line);
		    }
		    wr.close();
		    rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

//		return sendRequest("video/register/"+videoId+"/"+fileName+"/"+lenght+"/"+chunks+"/"+Conf.USER_ID);	
		return line;

	}
	
}
