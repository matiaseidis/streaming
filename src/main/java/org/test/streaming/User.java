package org.test.streaming;

public class User {
	
	private final String id;
	private String ip;
	private String port;
	private String email;

	public User(String id, String email, String ip, String port) {
		this.id = id;
		this.ip = ip;
		this.port = port;
		this.email = email;
	}
	
	public String getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
