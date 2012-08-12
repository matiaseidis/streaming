package org.test.streaming;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.monitor.CachoRegistration;

public class Chasqui implements Index {

	protected static final Log log = LogFactory.getLog(Chasqui.class);
	private int indexableSize;
	private Conf conf;
	private MovieCachoHasher movieCachoHasher;

	
	public Chasqui(Conf conf){
		this.conf = conf;
		indexableSize = conf.getIndexableSize();
		this.movieCachoHasher= new MovieCachoHasher(); 
	}
	
	@Override
	public void newCachoAvailableLocally(MovieCachoFile movieCachoFile) {
		
		if(movieCachoFile.getCacho().getLength() / indexableSize == 0){
			log.info("No se indexan cachos mas chicos que " +indexableSize+" bytes");
			return;
		}
		
		Map<Integer, String> chunksToRegiter = movieCachoHasher.hashMovieCachoFile(movieCachoFile, indexableSize);
		new CachoRegistration(conf, movieCachoFile, chunksToRegiter).register();
	}
}
