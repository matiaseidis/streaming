package org.test.streaming.prevalence;

import java.io.Serializable;

public class Cacho implements Serializable{

	final private long from;
	final private long lenght;

	public Cacho(Long from, Long lenght) {
		this.from = from;
		this.lenght = lenght;
	}
	
	public long lastByte(){
		return from + lenght - 1;
	}

	public boolean isChoterThan(Cacho newCacho) {
		if (newCacho == null) 
			return false;
		return this.from >= newCacho.getFrom() && this.lastByte() <= newCacho.lastByte();
	}

	public long getFrom() {
		return from;
	}

	public long getLenght() {
		return lenght;
	}
}
