package org.test.streaming.prevalence;

import java.io.Serializable;

public class Video implements Serializable{

	private final String videoId;
	private final String fileName;
	private final long length; 
	
	public Video(String videoId, String fileName, long length) {
		this.videoId = videoId;
		this.fileName = fileName;
		this.length = length;
	}

	public String getVideoId() {
		return videoId;
	}

	public String getFileName() {
		return fileName;
	}

	public long getLength() {
		return length;
	}
}
