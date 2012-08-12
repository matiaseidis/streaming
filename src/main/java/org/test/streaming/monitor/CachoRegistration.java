package org.test.streaming.monitor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.Conf;
import org.test.streaming.MovieCachoFile;

import com.google.gson.Gson;

public class CachoRegistration implements Registration{
	
	protected static final Log log = LogFactory.getLog(CachoRegistration.class);
	private final MovieCachoFile movieCachoFile;
	private int indexableSize;
	private final Notifier notifier;
	private Conf conf;
	private String videoId;
	private String userId;
	private ChunkEncoder chunkEncoder;
	private Map<Integer, String> chunksToRegiter;
	
	public CachoRegistration(Conf conf, MovieCachoFile cacho, Map<Integer, String> chunksToRegiter){
		this.movieCachoFile = cacho;
		this.conf = conf;
		
		notifier = new Notifier(conf);
		this.indexableSize = conf.getIndexableSize();
		/*
		 * TODO FIXME aca tengo que tener el id porque estoy descargandome el video (param) ...
		 */
		this.videoId =  conf.get("test.video.file.id"); 
		this.userId =  conf.get("test.user.id");
		this.chunkEncoder = new ChunkEncoder();
		this.chunksToRegiter = chunksToRegiter;
	} 
	
	@Override
	public RegistrationResponse register() {
		
		String fileName = movieCachoFile.getMovieFile().getName();
		String chunks = chunkEncoder.encodeCacho(chunksToRegiter);
		
		String registrationResponse  = notifier.registerChunks(fileName, this.getUserId(), chunks);
		log.info("Sended by Chasqui to remote repo <fileName: "+fileName+" - chunks: "+chunks+"]> - "+chunksToRegiter.size()+" fragmentos de "+indexableSize+" reportados");
		
		LinkedHashMap json = new Gson().fromJson(registrationResponse, LinkedHashMap.class);
		String code = json == null ? "CONNECTION_ERROR" : (String)json.get("code");
		
		return new RegistrationResponse(videoId, movieCachoFile.getMovieFile().getName(), code, json.get("body").toString(), 0, movieCachoFile.getCacho().getLength(), chunksToRegiter.size());
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
