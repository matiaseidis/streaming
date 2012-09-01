package org.test.streaming.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		
		String response = new IndexRequester(conf.getNotifierUrl()+"VideoService/getChunks/"+videoId+"/"+conf.get("test.user.id")).get();
		@SuppressWarnings("unchecked")
		List<String> chunkIds = new Gson().fromJson(response, List.class);
		return chunkIds;  	
	}

	public String listVideos(String videoId){
		
		return new IndexRequester("VideoService/list").get();	
	}

	public MovieRetrievalPlan getRetrievalPlan(String videoId, String userId){

		String url = conf.getNotifierUrl()+"planservice/getRetrievalPlan?videoId="+videoId+"&userId="+userId;
		String rp = new IndexRequester(url).get();
		
		log.info("retrieval plan response at <"+url+">: "+rp);
		
		LinkedHashMap json = new Gson().fromJson(rp, LinkedHashMap.class);
		
		log.info("JSON: "+json.toString());

		String fileName = ((StringMap)((StringMap)json.get("body")).get("video")).get("fileName").toString();
		String videoLenght = ((StringMap)((StringMap)json.get("body")).get("video")).get("lenght").toString();
		
		/*
		 * viene otro videoId del indice
		 */
		log.info("-------------");
		log.info(((StringMap)json.get("body")).toString());
		log.info("-------------");
		String id = ((StringMap)((StringMap)json.get("body")).get("video")).get("videoId").toString();
		log.info(id);
		if (!videoId.equals(id)) {
			throw new RuntimeException("no  puede pasar esto");
		}

		WatchMovieRetrievalPlan retrievalPlan = new WatchMovieRetrievalPlan(videoId	);
		retrievalPlan.setVideoLenght(Double.valueOf(videoLenght).longValue());
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
			cachoRetrieval.setPort(Integer.valueOf(userCachoUser.get("dimonPort").toString().split("\\.")[0].toString()));
			cachoRetrieval.setRequest(cachoRequest);
			
			retrievalPlan.getRequests().add(cachoRetrieval);
		}
		return retrievalPlan;
		
	}

	public String registerChunks(String fileName, String userId, String chunks) {
		String url = conf.getNotifierUrl()+"VideoService/registerChunks/"+fileName+"/"+userId+"/"+chunks;
		return new IndexRequester(url).post(null);	
	}

	public String registerVideo(String videoId, String fileName, long lenght, String chunks) {

		//register/{videoId}/{fileName}/{lenght}/{chunks}/{userId}", method = RequestMethod.POST)
		
//		 registerVideo(@NotNull String videoId, @NotNull  String fileName, @NotNull Long lenght, @NotNull String userId, @NotNull String chunks){
				
//		String url = conf.getNotifierUrl()+"VideoService/registerVideo/"+videoId+"/"+fileName+"/"+lenght+"/"+conf.get("test.user.id")+"/"+chunks;
		String url = conf.getNotifierUrl()+"videoService/registerVideo";
		Map<String, String> params = new HashMap<String, String>();
		params.put("videoId", videoId);
		params.put("fileName", fileName);
		params.put("lenght", Long.toString(lenght));
		params.put("chunks", chunks);
		params.put("userId", conf.get("test.user.id"));
		
		return new IndexRequester(url).post(params);
//		return new IndexRequester(url).get();
	}
	
	public String registerUser(User user) {
		//add/{nombre}/{email}/{ip}/{port}
		String url = conf.getNotifierUrl()+"UserService/add/"+user.getId()+"/"+user.getEmail()+"/"+user.getIp()+"/"+user.getPort();
		return new IndexRequester(url).post(null);	
		
	}

}
