package org.test.streaming.monitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.Conf;
import org.test.streaming.ChunkHasher;
import org.test.streaming.MovieCachoFile;
import org.test.streaming.MovieCachoHasher;

import com.google.gson.Gson;

public class CachoRegistration implements Registration{
	
	protected static final Log log = LogFactory.getLog(CachoRegistration.class);
	private final MovieCachoFile movieCachoFile;
	private MovieCachoHasher movieCachoHasher;
	int indexableSize;
	private final Notifier notifier;
	private Conf conf;
	private String videoId;
	private String userId;
	private ChunkEncoder chunkEncoder;
	
	public CachoRegistration(MovieCachoFile cacho, Conf conf){
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
		this.movieCachoHasher= new MovieCachoHasher(); 
	} 
	
	@Override
	public RegistrationResponse register() {
		
		int cachoFirstByteIndex = movieCachoFile.getCacho().getFirstByteIndex();
		int cachoLenght = movieCachoFile.getCacho().getLength();
		
		int resto = cachoFirstByteIndex % indexableSize;
		int nextChunkFirstByteIndex = resto == 0 
						? cachoFirstByteIndex 
						: cachoFirstByteIndex + resto;
		
		int totalChunks = (cachoLenght - (nextChunkFirstByteIndex - cachoFirstByteIndex)) / indexableSize;
		
		
		String fileName = movieCachoFile.getMovieFile().getName();
		Map<Integer, String> chunksToRegiter = movieCachoHasher.hashMovieCachoFile(movieCachoFile, indexableSize);
		String chunks = chunkEncoder.encodeChunks(chunksToRegiter);
		
		String registrationResponse  = notifier.registerChunks(fileName, this.getUserId(), chunks);
		log.info("Sended by Chasqui to remote repo <fileName: "+fileName+" - chunks: "+chunks+"]> - "+totalChunks+" fragmentos de "+indexableSize+" reportados");
		
		LinkedHashMap json = new Gson().fromJson(registrationResponse, LinkedHashMap.class);
		String code = json == null ? "CONNECTION_ERROR" : (String)json.get("code");
		
		return new RegistrationResponse(videoId, movieCachoFile.getMovieFile().getName(), code, json.get("body").toString(), 0, movieCachoFile.getCacho().getLength(), totalChunks);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
