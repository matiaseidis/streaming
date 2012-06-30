package org.test.streaming.monitor;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.Conf;
import org.test.streaming.Hasher;

public class VideoRegistration {

	protected static final Log log = LogFactory.getLog(VideoRegistration.class);
	private final File video;
	private final Hasher hasher;
	int indexableSize;
	private final Notifier notifier;
	private Conf conf;
	
	
	public VideoRegistration(File video, Conf conf){
		this.video = video;
		this.hasher = new Hasher();
		this.conf = conf;
		notifier = new Notifier(conf);
		this.indexableSize = conf.getIndexableSize();
	} 
	
	String go(){
		String videoFileName = video.getName();
		File file = new File(conf.getCachosDir()+videoFileName);
		String hash = hasher.hashVideo(file); 
		
		long videoFileSize = video.length();
		int totalChunks = (int) videoFileSize / indexableSize;
		
		if (videoFileSize % totalChunks != 0) {
			totalChunks++;
		}
		
		String chunks = hasher.encodeChunksForRegistration(file, totalChunks, 0, indexableSize);
		
		String registrationResponse  = notifier.registerVideo(hash, videoFileName, videoFileSize, chunks);
		log.info("Hashed full video: "+hash+" - "+videoFileName+" - "+ videoFileSize +" - "+ chunks);
		
		return registrationResponse;
	}
}
