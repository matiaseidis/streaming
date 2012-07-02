package org.test.streaming.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.Conf;

import com.google.gson.Gson;

public class Notifier {
	
	protected static final Log log = LogFactory.getLog(Notifier.class);
	private Conf conf;
	
	public Notifier(Conf conf){
		this.conf = conf;
	}
	
	public List<String> listChunks(String videoId){
		
		String response = new IndexRequester("video/getChunks/"+videoId+"/"+conf.get("test.user.id")).get();
		@SuppressWarnings("unchecked")
		List<String> chunkIds = new Gson().fromJson(response, List.class);
		return chunkIds;  	
	}

	public String listVideos(String videoId){
		
		return new IndexRequester("video/list").get();	
	}

	public String getGrafo(String videoId, String userId){

		return new IndexRequester("plan/"+videoId+"/"+userId).get();
	}

	public String registerParts(String fileName, String chunks) {

		return new IndexRequester("video/registerChunks/"+fileNameFromPart(fileName)+"/"+conf.get("test.user.id")+"/"+chunks).get();	
	}

	public String registerVideo(String videoId, String fileName, long lenght, String chunks) {

		//register/{videoId}/{fileName}/{lenght}/{chunks}/{userId}", method = RequestMethod.POST)
		String url = conf.getNotifierUrl()+"video/register/"+videoId+"/"+fileName+"/"+lenght+"/"+conf.get("test.user.id");
		Map<String, String> params = new HashMap<String, String>();
		params.put("chunks", chunks);
		
		return new IndexRequester(url).post(params);
	}
	
	//TODO meter esto en el objeto de MEtaDAta que ya existe
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

}
