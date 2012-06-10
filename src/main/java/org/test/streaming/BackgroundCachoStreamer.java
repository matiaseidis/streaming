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
	private FileOutputStream cachoFileOut;

	public BackgroundCachoStreamer(File cachoFile, OutputStream out, int firstByte, int cachoLength, OnCachoComplete whatToDo) {
		this.setCachoFile(cachoFile);
		this.setOut(out);
		this.setCachoLength(cachoLength);
		this.setWhatToDo(whatToDo);
		this.setFirstByte(firstByte);
		try {
			this.setCachoFileOut(new FileOutputStream(this.getCachoFile()));
			this.setCurrentOut(new BufferedOutputStream(this.getCachoFileOut()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("[" + firstByte + ", " + (cachoLength - 1) + "] (" + cachoLength + ") - Downloading... ");
	}

	@Override
	public void stream() {
		log.debug("Streaming " + this.getCachoLength() + " bytes...");
		if (this.getSavedBytes() == this.getCachoLength()) {
			log.debug("Cacho already saved, streaming from file...");
			try {
				this.streamCachoFile();
				this.logCacho();
				this.getWhatToDo().onCachoComplete(this);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			log.debug("Still downloading cacho...");
			ChannelBuffer buffer = bufferNextBytes();
			try {
				this.streamCachoFile();
				this.streamBuffer(buffer);
				this.completCachoFile(buffer);
				logCacho();
				this.getWhatToDo().onCachoComplete(this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void logCacho() {
		log.debug("[" + this.getFirstByte() + "," + (this.getFirstByte() + this.getCachoLength() - 1) + "] - Downloaded, streamed and saved to " + this.getCachoFile());
	}

	private void completCachoFile(ChannelBuffer buffer) throws IOException, FileNotFoundException {
		buffer.resetReaderIndex();
		FileOutputStream fileOutputStream = new FileOutputStream(this.getCachoFile(), true);
		BufferedOutputStream out2 = new BufferedOutputStream(fileOutputStream);
		buffer.readBytes(out2, buffer.capacity());
		out2.flush();
		out2.close();
		log.debug("Cacho file complete with " + buffer.capacity() + " bytes from memory buffer.");

	}

	private void streamBuffer(ChannelBuffer buffer) throws IOException {
		int totalStreamed = 0;
		while (totalStreamed < buffer.capacity()) {
			synchronized (this) {
				int readableBytes = buffer.readableBytes();
				buffer.readBytes(this.getOut(), readableBytes);
				totalStreamed += readableBytes;
			}
		}
		log.debug("Streamed " + totalStreamed + " bytes from memory buffer.");
	}

	private void streamCachoFile() throws FileNotFoundException, IOException {
		FileInputStream cachoFileInputStream = new FileInputStream(this.getCachoFile());
		int copy = IOUtils.copy(cachoFileInputStream, this.getOut());
		cachoFileInputStream.close();
		log.info("Streamed  " + copy + " bytes from cacho file.");
	}

	private void closeCachoFile() throws IOException {
		FileOutputStream cachoFileOutputStream = this.getCachoFileOut();
		cachoFileOutputStream.flush();
		cachoFileOutputStream.close();
		log.info("Cacho file " + this.getCachoFile() + " closed");
	}

	private ChannelBuffer bufferNextBytes() {
		ChannelBuffer buffer;
		synchronized (this) {
			try {
				this.closeCachoFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int remain = (int) (this.getCachoLength() - this.getSavedBytes());
			buffer = ChannelBuffers.buffer(remain);
			this.setCurrentOut(new ChannelBufferOutputStream(buffer));
			log.debug("Cacho file contains " + this.getSavedBytes() + ", downloading remaining " + remain + " to memory...");
		}
		return buffer;
	}

	@Override
	public synchronized void write(int b) throws IOException {
		this.getCurrentOut().write(b);
		this.setSavedBytes(this.getSavedBytes() + 1);
	}

	@Override
	public void close() throws IOException {
		super.close();
		log.debug("Downloaded " + this.getSavedBytes() + " bytes.");
		this.getCurrentOut().close();
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

	public FileOutputStream getCachoFileOut() {
		return cachoFileOut;
	}

	public void setCachoFileOut(FileOutputStream cachoFileOut) {
		this.cachoFileOut = cachoFileOut;
	}

}
