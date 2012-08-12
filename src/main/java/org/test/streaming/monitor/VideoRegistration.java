package org.test.streaming.monitor;

import java.io.File;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.Conf;
import org.test.streaming.ChunkHasher;

import com.google.gson.Gson;

public class VideoRegistration implements Registration{

	protected static final Log log = LogFactory.getLog(VideoRegistration.class);
	private final File video;
	private final ChunkHasher hasher;
	int indexableSize;
	private final Notifier notifier;
	private Conf conf;
	
	
	public VideoRegistration(File video, Conf conf){
		this.video = video;
		this.hasher = new ChunkHasher();
		this.conf = conf;
		notifier = new Notifier(conf);
		this.indexableSize = conf.getIndexableSize();
	} 
	
	public RegistrationResponse register(){
		String videoFileName = video.getName();
//		File file = new File(conf.getCachosDir()+videoFileName);
		String videoId = hasher.hashVideo(video); 
		
		long videoFileSize = video.length();
		int totalChunks = (int) videoFileSize / indexableSize;
		
		if (videoFileSize % totalChunks != 0) {
			totalChunks++;
		}
		
		String chunks = hasher.encodeChunksForRegistration(video, totalChunks, 0, indexableSize);
		
		String registrationResponse  = notifier.registerVideo(videoId, videoFileName, videoFileSize, chunks);
		log.info("Hashed full video: "+videoId+" - "+videoFileName+" - "+ videoFileSize +" - "+ chunks);
		
		LinkedHashMap json = new Gson().fromJson(registrationResponse, LinkedHashMap.class);
		
		String code = json == null ? "CONNECTION_ERROR" : (String)json.get("code");
		
		return new RegistrationResponse(videoId, video.getName(), code, json.get("body").toString(), 0, videoFileSize, totalChunks);
	}
}
