package org.test.streaming;

import java.io.OutputStream;

public abstract class CachoStreamer extends OutputStream {

	private Index index = new Chasqui();

	public abstract void stream();

	public Index getIndex() {
		return index;
	}

	public void setIndex(Index index) {
		this.index = index;
	}

}
