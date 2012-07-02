package org.test.streaming.encoding;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InputHandler extends Thread {

	protected static final Log log = LogFactory
			.getLog(InputHandler.class);
	
	InputStream input_;

	InputHandler(InputStream input, String name) {
		super(name);
		input_ = input;
	}

	public void run() {
		try {
			int c;
			while ((c = input_.read()) != -1) {
				/*
				 * TODO logger sin carriage return para procesos de ffmpeg
				 */
				System.out.print((char)c);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}