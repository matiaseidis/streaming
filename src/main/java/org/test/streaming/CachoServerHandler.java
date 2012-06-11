package org.test.streaming;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class CachoServerHandler extends SimpleChannelHandler {
	protected static final Log log = LogFactory.getLog(CachoServerHandler.class);

	private static final String LIBRARY_DIR_PATH = Conf.VIDEO_DIR;
	private MovieFileLocator movieFileLocator = new CompositeMovieFileLocator(new CompleteMovieFileLocator(LIBRARY_DIR_PATH), new CachoMovieFileLocator(LIBRARY_DIR_PATH));

	private static final double MegabitsPerSec = 100;
	private static final double KbitsPerSec = MegabitsPerSec * 1000;
	double BytesPerSec = KbitsPerSec * 1000 / 8;
	double BytesPerMili = BytesPerSec / 1000;
	double transferCostFactor = 2;

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		CachoRequest request = (CachoRequest) e.getMessage();
		log.debug("Cacho requested  " + request);
		MovieCachoFile mayBeMovieFile = this.getMovieFileLocator().locate(request);
		if (mayBeMovieFile == null) {
			log.error("This node cannot serve the request " + request + ", as an indication to thec counter-peer, the connection will be closed.");
			e.getChannel().close();
			return;
		}
		log.debug("Cacho file located: " + mayBeMovieFile);
		RandomAccessFile raf = new RandomAccessFile(mayBeMovieFile.getMovieFile(), "r");
		raf.seek(mayBeMovieFile.getCacho().getFirstByteIndex());
		int b = 1024 * 1024 * 4;
		InputStream fileInputStream = new BufferedInputStream(new FileInputStream(raf.getFD()));
		try {
			log.debug("Uploading cacho...");
			int s = request.getLength() / b;
			int t = 0;
			for (int i = 0; i < s; i++) {
				ChannelBuffer outBuffer = ChannelBuffers.buffer(b);
				outBuffer.writeBytes(fileInputStream, outBuffer.writableBytes());
				t += outBuffer.readableBytes();
				e.getChannel().write(outBuffer);
			}

			int r = request.getLength() % b;
			if (r != 0) {
				ChannelBuffer outBuffer = ChannelBuffers.buffer(r);
				outBuffer.writeBytes(fileInputStream, outBuffer.writableBytes());
				t += outBuffer.readableBytes();
				e.getChannel().write(outBuffer);
			}
			e.getChannel().write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
			log.debug("Uploaded " + t + " bytes.");
		} finally {
			fileInputStream.close();
		}
	}

	private ChannelFutureListener unlock(final CountDownLatch latch) {
		return new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				latch.countDown();
			}
		};
	}

	public MovieFileLocator getMovieFileLocator() {
		return movieFileLocator;
	}

	public void setMovieFileLocator(MovieFileLocator movieFileLocator) {
		this.movieFileLocator = movieFileLocator;
	}
}
