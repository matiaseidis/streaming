package org.test.streaming.monitor;

import java.util.LinkedHashMap;

import org.test.streaming.Conf;
import org.test.streaming.User;

import com.google.gson.Gson;

public class UserRegistration {
	
	private User user;
	private Conf conf;
	private Notifier notifier;

	public UserRegistration(User user, Conf conf) {
		this.conf = conf;
		this.user = user;
		notifier = new Notifier(conf);
	}

	public void go() {
		
		String registrationResponse  = notifier.registerUser(user);
		
		LinkedHashMap json = new Gson().fromJson(registrationResponse, LinkedHashMap.class);
		
		String code = json == null ? "CONNECTION_ERROR" : (String)json.get("code");
	}

}
