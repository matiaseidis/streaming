package org.test.streaming.prevalence;

import org.apache.log4j.Logger;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.monitor.SimpleMonitor;
import org.prevayler.foundation.serialization.XStreamSerializer;
import org.test.streaming.Conf;

public class BaseModel {
	
public Logger logger = Logger.getLogger(getClass());
	
	private String prevalenceDirectory = "Conf.PREVALENCE_DIR";
	private Prevayler prevayler;
	private final PrevaylerFactory prevaylerFactory;
	
	public BaseModel(){
		
		prevaylerFactory = new PrevaylerFactory();
		prevaylerFactory.configurePrevalenceDirectory(prevalenceDirectory);
		prevaylerFactory.configurePrevalentSystem(new LocalTracking());
		prevaylerFactory.configureMonitor(new SimpleMonitor(System.err));
		/*
		 * food taster off
		 */
		prevaylerFactory.configureTransactionFiltering(false);
		
		/*
		 * XML format
		 */
		XStreamSerializer s = new XStreamSerializer();
		prevaylerFactory.configureJournalSerializer(s);
		prevaylerFactory.configureSnapshotSerializer(s);
		
		try{
			prevayler = prevaylerFactory.create(); 
		}catch(Exception e){
			logger.error("FAILED TO LOAD PREVALENT SYSTEM " + e);
			logger.error(e.getMessage());
			System.exit(1);
		} 
	}
	
	public LocalTracking getModel(){
		return (LocalTracking) prevayler.prevalentSystem();
	}

	public Prevayler getPrevayler() {
		return prevayler;
	}
	
}
