package org.test.streaming.prevalence;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalTracking implements Serializable {
	
	private final ConcurrentMap<String, Video> videosRepo = new ConcurrentHashMap<String, Video>();
	
	private final ConcurrentMap<String, Cachos> cachosRepo = new ConcurrentHashMap<String, Cachos>();

	public void addVideo(String videoId, String fileName, long length) {
		
		videosRepo.putIfAbsent(videoId, new Video(videoId, fileName, length));
		/*
		 * no mas cachos porque tenemos el video entero
		 */
		cachosRepo.remove(videoId);
	}

	public void addCacho(String videoId, long start, long lenght) {
		
		boolean newCacho = cachosRepo.putIfAbsent(videoId, new Cachos()) == null; 
		
		if (!newCacho) {
			cachosRepo.get(videoId).addCacho(new Cacho(start, lenght));
		};
		
	}
	
	public String getHashByVideoFileName(String videoFileName){
		

		for ( Video video : videosRepo.values() ) {
			if ( video.getFileName().equals(videoFileName) ){
				return video.getVideoId();
			}
		}
		
		throw new RuntimeException("Video id not found in local repo for file " + videoFileName); 
	}
	

}
