package org.test.streaming.prevalence.transaction;

import java.util.Date;

import org.prevayler.Transaction;
import org.test.streaming.prevalence.LocalTracking;

public class AddCachoToLocalDB implements Transaction {

	private final String videoId; 
	private final long start;
	private final long lenght;
	
	public AddCachoToLocalDB(String videoId, long start, long lenght) {
		this.videoId = videoId;
		this.start =start; 
		this.lenght = lenght;
	}

	@Override
	public void executeOn(Object prevalentSystem, Date executionTime) {
		LocalTracking lt = (LocalTracking) prevalentSystem;
		lt.addCacho(videoId, start, lenght);
	}

}
