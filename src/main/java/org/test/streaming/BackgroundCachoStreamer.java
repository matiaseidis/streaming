package org.test.streaming;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;

public class BackgroundCachoStreamer extends CachoStreamer {
	protected static final Log log = LogFactory.getLog(BackgroundCachoStreamer.class);

	private File cachoFile;
	private OutputStream out;
	private OutputStream currentOut;
	private int cachoLength;
	private int savedBytes;
	private OnCachoComplete whatToDo;
	private int firstByte;
	private boolean cachoDownloaded = false;

	public BackgroundCachoStreamer(File cachoFile, OutputStream out, int firstByte, int cachoLength, OnCachoComplete whatToDo) {
		this.setCachoFile(cachoFile);
		this.setOut(out);
		this.setCachoLength(cachoLength);
		this.setWhatToDo(whatToDo);
		this.setFirstByte(firstByte);
		try {
			this.setCurrentOut(new BufferedOutputStream(new FileOutputStream(this.getCachoFile())));
		} catch (FileNotFoundException e) {
			log.fatal("Faile to create new local cacho file " + this.getCachoFile(), e);
		}
		log.debug("[" + firstByte + ", " + (cachoLength - 1) + "] (" + cachoLength + ") - Downloading... ");
	}

	@Override
	public void stream() {
		log.debug("Streaming " + this.getCachoLength() + " bytes...");
		if (this.isCachoDownloaded()) {
			log.debug("Cacho already saved, streaming from file...");
			try {
				this.streamCachoFile();
				this.logCacho();
			} catch (IOException e) {
				log.error("Stream failed, canceled.");
				return;
			}
		} else {
			log.debug("Still downloading cacho...");
			final ChannelBuffer buffer = bufferNextBytes();
			try {
				this.streamCachoFile();
			} catch (IOException e) {
				log.error("Stream failed, canceled.");
				return;
			}
			this.streamBuffer(buffer);
			getThreadPool().execute(new Runnable() {

				@Override
				public void run() {
					BackgroundCachoStreamer.this.completCachoFile(buffer);
				}
			});
		}
		this.getWhatToDo().onCachoComplete(this);
	}

	private void logCacho() {
		log.debug("[" + this.getFirstByte() + "," + (this.getFirstByte() + this.getCachoLength() - 1) + "] - Downloaded, streamed and saved to " + this.getCachoFile());
	}

	private void completCachoFile(ChannelBuffer buffer) {
		buffer.resetReaderIndex();
		BufferedOutputStream bufferedOut = null;
		try {
			bufferedOut = new BufferedOutputStream(new FileOutputStream(this.getCachoFile(), true));
			buffer.readBytes(bufferedOut, buffer.capacity());
			bufferedOut.flush();
			MovieCachoFile newCacho = new MovieCachoFile(new MovieCacho(this.getFirstByte(), this.getCachoLength()), this.getCachoFile());
			this.getIndex().newCachoAvailableLocally(newCacho);
			log.debug("Cacho file complete with " + buffer.capacity() + " bytes from memory buffer.");
			this.logCacho();
		} catch (FileNotFoundException e) {
			log.error("Failed to open cacho file " + this.getCachoFile() + ", when trying to append memory buffer.", e);
		} catch (IOException e) {
			log.error("Failed to append cacho file with memory buffer." + this.getCachoFile(), e);
		} finally {
			if (bufferedOut != null) {
				try {
					bufferedOut.close();
				} catch (IOException e) {
					log.warn("Failed to close cacho file " + this.getCachoFile() + " after appending memory buffer.", e);
				}
			}
		}

	}

	private void streamBuffer(ChannelBuffer buffer) {
		int totalStreamed = 0;
		while (totalStreamed < buffer.capacity()) {
			synchronized (this) {
				int readableBytes = buffer.readableBytes();
				try {
					buffer.readBytes(this.getOut(), readableBytes);
					totalStreamed += readableBytes;
				} catch (IOException e) {
					log.error("Failed to stream memory buffer.", e);
					return;
				}
			}
		}
		log.debug("Streamed " + totalStreamed + " bytes from memory buffer.");
	}

	private synchronized void streamCachoFile() throws IOException {
		FileInputStream cachoFileInputStream = null;
		try {
			cachoFileInputStream = new FileInputStream(this.getCachoFile());
			int copy = IOUtils.copy(cachoFileInputStream, this.getOut());
			log.info("Streamed  " + copy + " bytes from cacho file.");
		} catch (FileNotFoundException e1) {
			log.error("Failed to open cacho flie " + this.getCachoFile() + " to stream, nothing will be streamed.", e1);
		} catch (IOException e) {
			log.error("Failed to stream cacho file " + this.getCachoFile(), e);
			throw e;
		} finally {
			if (cachoFileInputStream != null) {
				try {
					cachoFileInputStream.close();
				} catch (IOException e) {
					log.warn("Failed to close cacho file after streaming.", e);
				}
			}
		}
	}

	private ChannelBuffer bufferNextBytes() {
		ChannelBuffer buffer;
		synchronized (this) {
			try {
				this.getCurrentOut().close();
			} catch (IOException e) {
				log.error("Failed to close initial streamer.", e);
			}
			int remain = (int) (this.getCachoLength() - this.getSavedBytes());
			buffer = ChannelBuffers.buffer(remain);
			this.setCurrentOut(new ChannelBufferOutputStream(buffer));
			log.debug("Cacho file contains " + this.getSavedBytes() + " bytes, downloading remaining " + remain + " bytes to memory...");
		}
		return buffer;
	}

	@Override
	public synchronized void write(int b) throws IOException {
		this.getCurrentOut().write(b);
		this.setSavedBytes(this.getSavedBytes() + 1);
	}

	@Override
	public synchronized void close() throws IOException {
		super.close();
		log.debug("Downloaded " + this.getSavedBytes() + " bytes.");
		this.getCurrentOut().close();
		this.setCachoDownloaded(true);
	}

	public File getCachoFile() {
		return cachoFile;
	}

	public void setCachoFile(File cachoFile) {
		this.cachoFile = cachoFile;
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public OutputStream getCurrentOut() {
		return currentOut;
	}

	public void setCurrentOut(OutputStream currentOut) {
		this.currentOut = currentOut;
	}

	public int getCachoLength() {
		return cachoLength;
	}

	public void setCachoLength(int cachoLength) {
		this.cachoLength = cachoLength;
	}

	public int getSavedBytes() {
		return savedBytes;
	}

	public void setSavedBytes(int savedBytes) {
		this.savedBytes = savedBytes;
	}

	public OnCachoComplete getWhatToDo() {
		return whatToDo;
	}

	public void setWhatToDo(OnCachoComplete whatToDo) {
		this.whatToDo = whatToDo;
	}

	public int getFirstByte() {
		return firstByte;
	}

	public void setFirstByte(int firstByte) {
		this.firstByte = firstByte;
	}

	public boolean isCachoDownloaded() {
		return cachoDownloaded;
	}

	public void setCachoDownloaded(boolean cachoDownloaded) {
		this.cachoDownloaded = cachoDownloaded;
	}

}
