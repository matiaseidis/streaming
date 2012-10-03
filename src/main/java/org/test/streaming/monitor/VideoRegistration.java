package org.test.streaming.monitor;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.ChunkHasher;
import org.test.streaming.Conf;
import org.test.streaming.MovieCacho;
import org.test.streaming.MovieCachoFile;
import org.test.streaming.MovieCachoHasher;

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
		String videoId = hasher.hashVideo(video); 
		
		long videoFileSize = video.length();
		
		MovieCachoFile movieCachoFile = new MovieCachoFile(new MovieCacho(0, (int)video.length()), video); 
		Map<Integer, String> hashes = new MovieCachoHasher().hashMovieCachoFile(movieCachoFile, indexableSize);
		
		String chunks = new ChunkEncoder().encodeVideoChunks(hashes);
		
		String registrationResponse  = notifier.registerVideo(videoId, videoFileName, videoFileSize, chunks);
		log.info("Hashed full video: "+videoId+" - "+videoFileName+" - "+ videoFileSize +" - "+ chunks.split("-").length +" chunks");
		
		LinkedHashMap json = new Gson().fromJson(registrationResponse, LinkedHashMap.class);
		
		String code = json == null ? "CONNECTION_ERROR" : (String)json.get("code");
		
		return new RegistrationResponse(videoId, video.getName(), code, json.get("body").toString(), 0, videoFileSize, hashes.size());
	}
}
