package org.test.streaming;

import java.io.File;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class CachoStreamer extends OutputStream {

	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	private Index index = new NullIndex();
	private File sharingDir;

	public abstract void stream();

	public Index getIndex() {
		return index;
	}

	public void setIndex(Index index) {
		this.index = index;
	}

	public static ExecutorService getThreadPool() {
		return threadPool;
	}

	public static void setThreadPool(ExecutorService threadPool) {
		CachoStreamer.threadPool = threadPool;
	}

	public File getSharingDir() {
		return this.sharingDir;
	}

	public void setSharingDir(File sharingDir) {
		this.sharingDir = sharingDir;
	}
}
