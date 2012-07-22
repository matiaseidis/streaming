package org.test.streaming;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.test.streaming.monitor.Notifier;
import org.test.streaming.monitor.RegistrationResponse;
import org.test.streaming.monitor.UserRegistration;
import org.test.streaming.monitor.VideoRegistration;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class VideoRegisterTest {


	String videoChunks;
	
	@Test
	public void testRestrievalPlan(){

		Conf conf = new Conf();
		Assert.assertTrue(StringUtils.isNotEmpty(conf.get("test.user.id")));

		User user = new User(conf.get("test.user.id"), "user@test.com", "localhost", "8080");
		UserRegistration userRegistration = new UserRegistration(user, conf);
		userRegistration.go();
		
		String videoFileName = conf.get("test.video.file.name");
		
		File video = new File(conf.getCachosDir(), videoFileName);
		VideoRegistration videoRegistration = new VideoRegistration(video, conf);
		
		RegistrationResponse videoRegistrationResponse = videoRegistration.go();
		
		Assert.assertNotNull(videoRegistrationResponse);
		
		Assert.assertTrue(videoRegistrationResponse.getCode().equals("OK") || videoRegistrationResponse.getCode().equals("ERROR"));
		
		Notifier notifier = new Notifier(conf);
		
		WatchMovieRetrievalPlan retrievalPlan = (WatchMovieRetrievalPlan) notifier.getRetrievalPlan(videoRegistrationResponse.getId(), conf.get("test.user.id"));
		
		Assert.assertEquals(retrievalPlan.getRequests().size(), 1);
		Assert.assertEquals(retrievalPlan.getRequests().get(0).getHost(), user.getIp());
		Assert.assertEquals(retrievalPlan.getVideoId(), videoRegistrationResponse.getId());
		Assert.assertEquals(retrievalPlan.getRequests().get(0).getRequest().getLength(), video.length());
	}
}
