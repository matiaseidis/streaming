package org.test.streaming.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.test.streaming.CachoRequest;
import org.test.streaming.CachoRetrieval;
import org.test.streaming.Conf;
import org.test.streaming.MovieCacho;
import org.test.streaming.MovieRetrievalPlan;
import org.test.streaming.User;
import org.test.streaming.WatchMovieRetrievalPlan;

import com.google.gson.Gson;

public class Notifier {

	protected static final Log log = LogFactory.getLog(Notifier.class);
	private Conf conf;

	public Notifier(Conf conf) {
		this.conf = conf;
	}

	public List<String> listChunks(String videoId) {

		String response = new IndexRequester(conf.getNotifierUrl() + "VideoService/getChunks/" + videoId + "/" + conf.get("test.user.id")).get();
		@SuppressWarnings("unchecked")
		List<String> chunkIds = new Gson().fromJson(response, List.class);
		return chunkIds;
	}

	public String listVideos(String videoId) {

		return new IndexRequester("VideoService/list").get();
	}

	public MovieRetrievalPlan getRetrievalPlan(String videoId, String userId) {

		log.info("requesting retrieval plan for video " + videoId + " for user: " + userId);

		String url = conf.getNotifierUrl() + "planservice/getRetrievalPlan?videoId=" + videoId + "&userId=" + userId;
		String rp = new IndexRequester(url).get();

		if (rp == null) {
			return null;
		}

		// log.info("retrieval plan response at <"+url+">: "+rp);

		ObjectMapper mapper = new ObjectMapper();
		WatchMovieRetrievalPlan retrievalPlan = new WatchMovieRetrievalPlan(videoId);
		try {

			LinkedHashMap<String, Object> result = mapper.readValue(rp, new TypeReference<LinkedHashMap<String, Object>>() {
			});
			String code = result.get("code").toString();
			if ("ERROR".equalsIgnoreCase(code)) {
				// TODO handle not enough sources o algo asi
				return new WatchMovieRetrievalPlan(videoId);
			}

			LinkedHashMap<String, Object> body = ((LinkedHashMap<String, Object>) result.get("body"));
			LinkedHashMap<String, Object> video = ((LinkedHashMap<String, Object>) body.get("video"));

			String fileName = video.get("fileName").toString();
			String videoLenght = video.get("lenght").toString();
			;
			String vId = video.get("videoId").toString();
			;
			if (!videoId.equals(vId)) {
				throw new RuntimeException("no  puede pasar esto");
			}

			retrievalPlan.setVideoLenght(Double.valueOf(videoLenght).longValue());

			ArrayList<LinkedHashMap<String, Object>> userCachos = (ArrayList<LinkedHashMap<String, Object>>) video.get("userCachos");
			for (LinkedHashMap<String, Object> userCacho : userCachos) {

				LinkedHashMap<String, Object> userCachoUser = (LinkedHashMap<String, Object>) userCacho.get("user");
				ArrayList<LinkedHashMap<String, Object>> userCachoMap = (ArrayList<LinkedHashMap<String, Object>>) userCacho.get("cachos");
				for (LinkedHashMap<String, Object> cacho : userCachoMap) {

					String from = cacho.get("start").toString();
					String lenght = cacho.get("lenght").toString();

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
			}
		} catch (Exception e) {
			log.error("ERROR", e);
		}
		return retrievalPlan;

	}

	public String registerChunks(String fileName, String userId, String chunks) {
		String url = conf.getNotifierUrl() + "VideoService/registerChunks/" + fileName + "/" + userId + "/" + chunks;
		return new IndexRequester(url).post(null);
	}

	public String registerVideo(String videoId, String fileName, long lenght, String chunks) {

		String url = conf.getNotifierUrl() + "videoService/registerVideo";
		Map<String, String> params = new HashMap<String, String>();
		params.put("videoId", videoId);
		params.put("fileName", fileName);
		params.put("lenght", Long.toString(lenght));
		params.put("chunks", chunks);
		params.put("userId", conf.get("test.user.id"));

		return new IndexRequester(url).post(params);
	}

	public String registerUser(User user) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("name", user.getId());
		params.put("email", user.getEmail());
		params.put("ip", user.getIp());
		params.put("servlePort", user.getServlePort());
		params.put("dimonPort", user.getDimonPort());

		String url = conf.getNotifierUrl() + "UserService/create";
		return new IndexRequester(url).post(params);
	}

}
