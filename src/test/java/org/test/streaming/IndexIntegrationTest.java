package org.test.streaming;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.test.streaming.monitor.VideoRegistration;

public class IndexIntegrationTest {
	
	@Test
	public void registerToAndRetrieveFromIndex() {
		
		Conf conf = new Conf("/alt-test-conf.properties");
		
		File video = new File(conf.getSharedDir(), conf.get("test.video.file.name"));
		
		Assert.assertTrue(video.getAbsolutePath(), video.exists());
		
		VideoRegistration videoRegistration = new VideoRegistration(video, conf);
		videoRegistration.register();
		
		
	}

}
