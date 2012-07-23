package org.test.streaming;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.test.streaming.monitor.CachoRegistration;
import org.test.streaming.monitor.Notifier;
import org.test.streaming.monitor.RegistrationResponse;
import org.test.streaming.monitor.UserRegistration;
import org.test.streaming.monitor.VideoRegistration;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class VideoRegistrationFullCycleTest {


	String videoChunks;
	
	@Test
	public void testRestrievalPlan(){

		Conf conf = new Conf();
		String videoFileName = conf.get("test.video.file.name");
		File video = new File(conf.getCachosDir(), videoFileName);
		Notifier notifier = new Notifier(conf);

		/*
		 * parte 1:
		 * registro usuario
		 * el usuario regitra un video
		 * pide el retrieval plan para ese video
		 */
		Assert.assertTrue(StringUtils.isNotEmpty(conf.get("test.user.id")));

		User user = new User(conf.get("test.user.id"), "user@test.com", "localhost", "8080");
		UserRegistration userRegistration = new UserRegistration(user, conf);
		userRegistration.go();
		
		VideoRegistration videoRegistration = new VideoRegistration(video, conf);
		RegistrationResponse videoRegistrationResponse = videoRegistration.register();
		
		Assert.assertNotNull(videoRegistrationResponse);
		/*
		 * no hay error de conexion (CONN_ERROR)
		 */
		Assert.assertTrue(videoRegistrationResponse.getCode().equals("OK") || videoRegistrationResponse.getCode().equals("ERROR"));
		
		WatchMovieRetrievalPlan retrievalPlan = (WatchMovieRetrievalPlan) notifier.getRetrievalPlan(videoRegistrationResponse.getId(), conf.get("test.user.id"));
		
		Assert.assertEquals(retrievalPlan.getRequests().size(), 1);
		Assert.assertEquals(retrievalPlan.getRequests().get(0).getHost(), user.getIp());
		Assert.assertEquals(retrievalPlan.getVideoId(), videoRegistrationResponse.getId());
		Assert.assertEquals(retrievalPlan.getRequests().get(0).getRequest().getLength(), video.length());
		
		/*
		 * registro otro usuario
		 * el usuario registra un cacho del mismo video
		 * pido el retrieval plan  
		 */
		User otroUser = new User("otro-user-test", "otro-user@test.com", "1.1.1.1", "8080");
		UserRegistration otherUserRegistration = new UserRegistration(otroUser, conf);
		otherUserRegistration.go();
		int cachoLenght = 1024*1024;
		
		
		MovieCachoFile movieCachoFile = new MovieCachoFile(new MovieCacho(0,cachoLenght), video);
		CachoRegistration cachoRegistration = new CachoRegistration(movieCachoFile, conf);
		
		cachoRegistration.setUserId(otroUser.getId());
		
		RegistrationResponse cachoRegistrationResponse = cachoRegistration.register();
		
		Assert.assertNotNull(cachoRegistrationResponse);
		/*
		 * no hay error de conexion (CONN_ERROR)
		 */
		Assert.assertTrue(cachoRegistrationResponse.getCode().equals("OK") || cachoRegistrationResponse.getCode().equals("ERROR"));
		
		WatchMovieRetrievalPlan secondRetrievalPlan = (WatchMovieRetrievalPlan) notifier.getRetrievalPlan(videoRegistrationResponse.getId(), otroUser.getId());
		
		Assert.assertEquals(secondRetrievalPlan.getRequests().size(), 2);
		Assert.assertEquals(secondRetrievalPlan.getRequests().get(0).getHost(), user.getIp());
		Assert.assertEquals(secondRetrievalPlan.getRequests().get(1).getHost(), otroUser.getIp());
		Assert.assertNotSame(secondRetrievalPlan.getVideoId(), cachoRegistrationResponse.getId());
		Assert.assertEquals(secondRetrievalPlan.getRequests().get(0).getRequest().getLength(), video.length() - cachoLenght);
		Assert.assertEquals(secondRetrievalPlan.getRequests().get(1).getRequest().getLength(), cachoLenght);
		
	}
}
