package org.test.streaming;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class MovieCacho implements Serializable {
	private int firstByteIndex;
	private int length;

	public MovieCacho() {
	}

	public MovieCacho(int firstByteIndex, int length) {
		super();
		this.firstByteIndex = firstByteIndex;
		this.length = length;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public int getFirstByteIndex() {
		return firstByteIndex;
	}

	public void setFirstByteIndex(int firstByteIndex) {
		this.firstByteIndex = firstByteIndex;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean contains(MovieCacho cacho) {
		return cacho.getFirstByteIndex() >= this.getFirstByteIndex() && cacho.getLastByteIndex() <= this.getLastByteIndex();
	}

	public int getLastByteIndex() {
		return this.getFirstByteIndex() + this.getLength() - 1;
	}

	public MovieCacho subCacho(MovieCacho cacho) {
		return new MovieCacho(cacho.getFirstByteIndex() - this.getFirstByteIndex(), cacho.getLength());
	}

}
