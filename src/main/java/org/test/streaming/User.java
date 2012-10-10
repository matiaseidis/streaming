package org.test.streaming;

public class User {
	
	private final String id;
	private String ip;
	private String servlePort;
	private String dimonPort;
	private String email;

	public User(String id, String email, String ip, String servlePort, String dimonPort) {
		this.id = id;
		this.ip = ip;
		this.servlePort = servlePort;
		this.dimonPort = dimonPort;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getServlePort() {
		return servlePort;
	}

	public void setServlePort(String servlePort) {
		this.servlePort = servlePort;
	}

	public String getDimonPort() {
		return dimonPort;
	}

	public void setDimonPort(String dimonPort) {
		this.dimonPort = dimonPort;
	}

}
