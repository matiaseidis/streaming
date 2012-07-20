package org.test.streaming;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

public class CachoWriter {

	protected static Log log = LogFactory.getLog(CachoWriter.class);

	public void uploadCacho(Channel output, InputStream input, int lenght) throws IOException {
		int t = 0;
		int b = 1024 * 1024 * 4;
		try {
			log.debug("Uploading cacho...");
			int s = lenght / b;
			int r = lenght % b;
			for (int i = 0; i < s; i++) {
				ChannelBuffer outBuffer = ChannelBuffers.buffer(b);
				outBuffer.writeBytes(input, outBuffer.writableBytes());
				t += outBuffer.readableBytes();
				output.write(outBuffer);
			}

			if (r != 0) {
				ChannelBuffer outBuffer = ChannelBuffers.buffer(r);
				outBuffer.writeBytes(input, outBuffer.writableBytes());
				t += outBuffer.readableBytes();
				output.write(outBuffer);
			}
		} finally {
			input.close();
		}
		log.debug("Uploaded " + t + " bytes.");
	}

}
