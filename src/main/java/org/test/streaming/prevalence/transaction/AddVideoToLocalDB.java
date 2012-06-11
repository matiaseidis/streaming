package org.test.streaming.prevalence.transaction;

import java.util.Date;

import org.prevayler.Transaction;
import org.test.streaming.prevalence.LocalTracking;

public class AddVideoToLocalDB implements Transaction {

	private final String videoId;
	private final String fileName;
	private final long length; 
	
	public AddVideoToLocalDB(String videoId, String fileName, long length) {
		this.videoId = videoId;
		this.fileName = fileName;
		this.length = length;
	}

	@Override
	public void executeOn(Object prevalentSystem, Date executionTime) {
		LocalTracking lt = (LocalTracking) prevalentSystem;
		lt.addVideo(videoId, fileName, length);
	}

}
