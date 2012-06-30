package org.test.streaming.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.Conf;
import org.test.streaming.Hasher;
import org.test.streaming.MovieCachoFile;

public class CachoRegistration {
	
	protected static final Log log = LogFactory.getLog(CachoRegistration.class);
	private final MovieCachoFile movieCachoFile;
	private final Hasher hasher;
	int indexableSize;
	private final Notifier notifier;
	private Conf conf;
	
	public CachoRegistration(MovieCachoFile cacho, Conf conf){
		this.movieCachoFile = cacho;
		this.conf = conf;
		this.hasher = new Hasher();
		notifier = new Notifier(conf);
		this.indexableSize = conf.getIndexableSize();
	} 
	

	public void go() {
		int cachoFirstByteIndex = movieCachoFile.getCacho().getFirstByteIndex();
		int cachoLenght = movieCachoFile.getCacho().getLength();
		int nextChunkFirstByteIndex = cachoFirstByteIndex % indexableSize == 0 ? cachoFirstByteIndex : cachoFirstByteIndex + indexableSize;
		int totalChunks = (cachoLenght - (nextChunkFirstByteIndex - cachoFirstByteIndex)) / indexableSize;
		String fileName = movieCachoFile.getMovieFile().getName();
		
		/*
		 * partes de 1mb en la que indexo este cacho
		 */
		Map<Integer, String> chunksToRegiter = new HashMap<Integer, String>();

		
		/*
		 * TODO FIXME aca tengo que tener el id porque estoy descargandome el video (param) ...
		 */
		String videoId = conf.get("test.video.file.id"); 
		
		/*
		 * TODO cachear esto?
		 */
		List<String> videoChunks = notifier.listChunks(videoId); 
		
		for(int i = 0; i < totalChunks; i++){
			
			String chunkId = hasher.hashCacho(movieCachoFile.getMovieFile(), nextChunkFirstByteIndex, movieCachoFile.getCacho().getFirstByteIndex()); 
					
			int ordinal = nextChunkFirstByteIndex/indexableSize; // base 0 ???
			chunksToRegiter.put(ordinal, chunkId);
			nextChunkFirstByteIndex += indexableSize;
		}
		
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<Integer,String> entry : chunksToRegiter.entrySet()){
			if(entry.getValue().equals(videoChunks.get(entry.getKey())))
				sb.append(entry.getKey()+"!"+entry.getValue()+"&");
		}
		if(sb.length()>1)
			sb.replace(sb.length()-1, sb.length(), StringUtils.EMPTY);
		
		String chunks = sb.toString();
		notifier.registerParts(fileName, chunks);
		log.info("Sended by Chasqui to remote repo <fileName: "+fileName+" - chunks: "+chunks+"]> - "+totalChunks+" fragmentos de "+indexableSize+" reportados");
	}

}
