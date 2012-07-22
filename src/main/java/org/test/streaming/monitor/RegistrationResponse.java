package org.test.streaming.monitor;

public class RegistrationResponse {


	private final String id;	
	private final String fileName;
	private final String code;
	private final String message;
	private final int start;
	private final long lenght;
	private final int totalChunks;
	
	public RegistrationResponse(String id,String fileName, String code,String message, int start, long lenght,
			int totalChunks) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.code = code;
		this.message = message;
		this.start = start;
		this.lenght = lenght;
		this.totalChunks = totalChunks;
	}
	
	public String getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public int getStart() {
		return start;
	}

	public long getLenght() {
		return lenght;
	}

	public int getTotalChunks() {
		return totalChunks;
	}

	public String getFileName() {
		return fileName;
	}

	

}
