package org.test.streaming.prevalence;

import java.io.Serializable;

import lombok.Getter;

public class Video implements Serializable{

	@Getter private final String videoId;
	@Getter private final String fileName;
	@Getter private final long length; 
	
	public Video(String videoId, String fileName, long length) {
		this.videoId = videoId;
		this.fileName = fileName;
		this.length = length;
	}
}
