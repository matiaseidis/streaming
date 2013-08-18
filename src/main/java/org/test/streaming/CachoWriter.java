package org.test.streaming;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

public class CachoWriter implements ChannelFutureListener {

	protected static Log log = LogFactory.getLog(CachoWriter.class);

	int readableBytes;
	int total;
	int written;

	public CachoWriter() {
	}

	public void uploadCacho(Channel output, InputStream input, int lenght) throws IOException {
		this.total = lenght;
		int t = 0;
		int b = 1024 * 1024 * 4;
		try {
			log.debug("Uploading cacho...");
			int s = lenght / b;
			int r = lenght % b;
			for (int i = 0; i < s; i++) {
				ChannelBuffer outBuffer = ChannelBuffers.buffer(b);
				outBuffer.writeBytes(input, outBuffer.writableBytes());
				readableBytes = outBuffer.readableBytes();
				t += readableBytes;
				ChannelFuture write = output.write(outBuffer);
				write.addListener(this);
				write.removeListener(this);
			}

			if (r != 0) {
				ChannelBuffer outBuffer = ChannelBuffers.buffer(r);
				outBuffer.writeBytes(input, outBuffer.writableBytes());
				readableBytes = outBuffer.readableBytes();
				t += readableBytes;
				ChannelFuture write = output.write(outBuffer);
				write.addListener(this);
				write.removeListener(this);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			input.close();
		}
		log.debug("Uploaded " + t + " bytes.");
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		written += readableBytes;
		System.out.println("Written " + written + " " + (total - written) + " to go ( " + written * 100 / (double) total + "%)");
	}
}
