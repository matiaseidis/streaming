package org.test.streaming.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.CachoRequest;
import org.test.streaming.CachoRetrieval;
import org.test.streaming.Conf;
import org.test.streaming.MovieCacho;
import org.test.streaming.MovieRetrievalPlan;
import org.test.streaming.User;
import org.test.streaming.WatchMovieRetrievalPlan;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class Notifier {
	
	protected static final Log log = LogFactory.getLog(Notifier.class);
	private Conf conf;
	
	public Notifier(Conf conf){
		this.conf = conf;
	}
	
	public List<String> listChunks(String videoId){
		
		String response = new IndexRequester(conf.getNotifierUrl()+"video/getChunks/"+videoId+"/"+conf.get("test.user.id")).get();
		@SuppressWarnings("unchecked")
		List<String> chunkIds = new Gson().fromJson(response, List.class);
		return chunkIds;  	
	}

	public String listVideos(String videoId){
		
		return new IndexRequester("video/list").get();	
	}

	public MovieRetrievalPlan getRetrievalPlan(String videoId, String userId){

		String rp = new IndexRequester(conf.getNotifierUrl()+"plan/"+videoId+"/"+userId).get();
		
		LinkedHashMap json = new Gson().fromJson(rp, LinkedHashMap.class);

		String fileName = ((StringMap)((StringMap)json.get("body")).get("video")).get("fileName").toString();
		
		/*
		 * viene otro videoId del indice
		 */
		String id = ((StringMap)((StringMap)json.get("body")).get("video")).get("id").toString();
		if (!videoId.equals(id)) {
			throw new RuntimeException("no  puede pasar esto");
		}

		WatchMovieRetrievalPlan retrievalPlan = new WatchMovieRetrievalPlan(videoId);
		ArrayList<StringMap> userCachos = ((ArrayList<StringMap>)((StringMap)json.get("body")).get("userCachos"));

		for (StringMap userCacho : userCachos) {
			StringMap userCachoUser = (StringMap)userCacho.get("user");
			StringMap userCachoMap = (StringMap)userCacho.get("cacho");
			String from = userCachoMap.get("from").toString();
			String lenght = userCachoMap.get("lenght").toString();
			
			CachoRequest cachoRequest = new CachoRequest();
			cachoRequest.setCacho(new MovieCacho(Integer.valueOf(from.split("\\.")[0]), Integer.valueOf(lenght.split("\\.")[0])));
			cachoRequest.setFileName(fileName);
			cachoRequest.setMovieId(videoId);

			
			CachoRetrieval cachoRetrieval = new CachoRetrieval();
			cachoRetrieval.setHost(userCachoUser.get("ip").toString());
			cachoRetrieval.setPort(Integer.valueOf(userCachoUser.get("port").toString().split("\\.")[0].toString()));
			cachoRetrieval.setRequest(cachoRequest);
			
			retrievalPlan.getRequests().add(cachoRetrieval);
		}
		return retrievalPlan;
		
	}

	public String registerChunks(String fileName, String userId, String chunks) {
		String url = conf.getNotifierUrl()+"video/registerChunks/"+fileName+"/"+userId+"/"+chunks;
		return new IndexRequester(url).post(null);	
	}

	public String registerVideo(String videoId, String fileName, long lenght, String chunks) {

		//register/{videoId}/{fileName}/{lenght}/{chunks}/{userId}", method = RequestMethod.POST)
		String url = conf.getNotifierUrl()+"video/register/"+videoId+"/"+fileName+"/"+lenght+"/"+conf.get("test.user.id");
		Map<String, String> params = new HashMap<String, String>();
		params.put("chunks", chunks);
		
		return new IndexRequester(url).post(params);
	}
	
//	//TODO meter esto en el objeto de MEtaDAta que ya existe
//	private String fileNameFromPart(String fileName) {
//		// Luther.S02E01.720p.HDTV.x264-3.mp4-67108864-67108864.part
//		String[] splittedFileName = fileName.split("-");
//		String result = StringUtils.EMPTY;
//		int pieces = splittedFileName.length-2;
//		for(int i=0; i<pieces; i++) {
//			result +=splittedFileName[i]+"-";
//		}
//		result = result.substring(0, result.length()-1);
//		System.out.println(result);
//		return result;
//	}

	public String registerUser(User user) {
		//add/{nombre}/{email}/{ip}/{port}
		String url = conf.getNotifierUrl()+"user/add/"+user.getId()+"/"+user.getEmail()+"/"+user.getIp()+"/"+user.getPort();
		return new IndexRequester(url).post(null);	
		
	}

}
