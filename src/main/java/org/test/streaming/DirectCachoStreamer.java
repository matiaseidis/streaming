package org.test.streaming;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Lo manda de una al out. Despues lo bufferea. Cuando se cierra, escribe el
 * buffer en disco.
 * 
 * @author Cronopio
 * 
 */
public class DirectCachoStreamer extends CachoStreamer {

	protected static final Log log = LogFactory.getLog(DirectCachoStreamer.class);

	private OutputStream out;
	private ByteArrayOutputStream buffer;
	private int cachoLength;
	private File cachoFile;
	private OnCachoComplete whatToDo;
	private int count;

	public DirectCachoStreamer(File shareDir, OutputStream out, int cachoLength, File cachoFile, OnCachoComplete whatToDo) {
		this.setSharingDir(shareDir);
		this.setOut(out);
		this.setCachoLength(cachoLength);
		this.setBuffer(new ByteArrayOutputStream(this.getCachoLength()));
		this.setCachoFile(cachoFile);
		this.setWhatToDo(whatToDo);
		log.debug("[0," + (cachoLength - 1) + "] (" + cachoLength + ") - Downloading and streaming...");
	}

	@Override
	public void write(int b) throws IOException {
		this.getOut().write(b);
		this.getBuffer().write(b);
		this.setCount(this.getCount() + 1);
	}

	@Override
	public void close() throws IOException {
		log.debug("Downloaded and streamed " + this.getCount() + " bytes.");
		super.close();
		getThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				DirectCachoStreamer.this.saveStreamedCacho();
			}
		});

		this.getWhatToDo().onCachoComplete(this);
	}

	protected void saveStreamedCacho() {
		BufferedOutputStream stream = null;
		File cachoFile = this.getCachoFile();
		try {
			stream = new BufferedOutputStream(new FileOutputStream(cachoFile));
			this.getBuffer().writeTo(stream);
			this.getBuffer().flush();
			stream.flush();
			log.debug("[" + 0 + "," + (getCount() - 1) + "] -  Downladed, streamed and saved to " + cachoFile);
		} catch (FileNotFoundException e) {
			log.error("Failed to open cacho file " + cachoFile + " when saving it after direct streaming.", e);
		} catch (IOException e) {
			log.error("Failed to write cacho file " + cachoFile + " when saving it after direct streaming.", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
					this.cachoDownloaded();
				} catch (IOException e) {
					log.warn("Failed to close cacho " + cachoFile + " after saving it after direct streaming.", e);
				}
			}
		}
	}

	private void cachoDownloaded() {
		if (!this.getCachoFile().renameTo(this.sharedCachoFile())) {
			log.warn("Failed to move cacho file " + this.getCachoFile() + " to share dir: " + sharedCachoFile());
		}
		MovieCachoFile newCacho = new MovieCachoFile(new MovieCacho(0, this.getCachoLength()), sharedCachoFile());
		this.getIndex().newCachoAvailableLocally(newCacho);
	}

	private File sharedCachoFile() {
		return new File(this.getSharingDir(), this.getCachoFile().getName());
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public ByteArrayOutputStream getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteArrayOutputStream buffer) {
		this.buffer = buffer;
	}

	public int getCachoLength() {
		return cachoLength;
	}

	public void setCachoLength(int cachoLength) {
		this.cachoLength = cachoLength;
	}

	public File getCachoFile() {
		return cachoFile;
	}

	public void setCachoFile(File cachoFile) {
		this.cachoFile = cachoFile;
	}

	@Override
	public void stream() {
		// no hacmo nada, somo el 'diret' strimer
	}

	public OnCachoComplete getWhatToDo() {
		return whatToDo;
	}

	public void setWhatToDo(OnCachoComplete whatToDo) {
		this.whatToDo = whatToDo;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
