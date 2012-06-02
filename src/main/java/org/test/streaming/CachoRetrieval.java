package org.test.streaming;

public class CachoRetrieval {
	private String host;
	private int port;
	private CachoRequest request;

	public CachoRetrieval() {
	}

	public CachoRetrieval(String host, int port, CachoRequest request) {
		super();
		this.host = host;
		this.port = port;
		this.request = request;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public CachoRequest getRequest() {
		return request;
	}

	public void setRequest(CachoRequest request) {
		this.request = request;
	}

}
