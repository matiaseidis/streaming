package org.test.streaming;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.monitor.CachoRegistration;

public class MovieCachoHasher {
	
	protected static final Log log = LogFactory.getLog(MovieCachoHasher.class);
	
	private final Hasher hasher;

	public MovieCachoHasher(){
		this.hasher = new Hasher();
	}

	public Map<Integer, String> hashMovieCachoFile(MovieCachoFile movieCachoFile, int indexableSize) {
		
		int cachoFirstByteIndex = movieCachoFile.getCacho().getFirstByteIndex();
		int cachoLenght = movieCachoFile.getCacho().getLength();
//		int resto = cachoFirstByteIndex % indexableSize;
		int nextChunkFirstByteIndex = cachoFirstByteIndex % indexableSize == 0 ? cachoFirstByteIndex : cachoFirstByteIndex + indexableSize;
		int totalChunks = (cachoLenght - (nextChunkFirstByteIndex - cachoFirstByteIndex)) / indexableSize;
		String fileName = movieCachoFile.getMovieFile().getName();
		
		Map<Integer, String> result = new HashMap<Integer, String>();
		
		for(int i = 0; i < totalChunks; i++){
			
			String chunkId = hasher.hashCacho(movieCachoFile.getMovieFile(), nextChunkFirstByteIndex, movieCachoFile.getCacho().getFirstByteIndex()); 
					
			int ordinal = nextChunkFirstByteIndex/indexableSize; // base 0 ???
			result.put(ordinal, chunkId);
			nextChunkFirstByteIndex += indexableSize;
		}
		
		return result;
	}

}
