package org.test.streaming;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class CachoServerHandler extends SimpleChannelHandler {

	private static final String LIBRARY_DIR_PATH = Conf.VIDEO_DIR;
	private MovieFileLocator movieFileLocator = new CompositeMovieFileLocator(new CompleteMovieFileLocator(LIBRARY_DIR_PATH), new CachoMovieFileLocator(LIBRARY_DIR_PATH));

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		CachoRequest request = (CachoRequest) e.getMessage();
		System.out.println("Cacho requested  " + request);
		MovieCachoFile mayBeMovieFile = this.getMovieFileLocator().locate(request);
		if (mayBeMovieFile == null) {
			// TODO devolver error
			System.err.println("This node cannot serve the request " + request);
			return;
		}
		System.out.println("Cacho file " + mayBeMovieFile);
		RandomAccessFile raf = new RandomAccessFile(mayBeMovieFile.getMovieFile(), "r");
		raf.seek(mayBeMovieFile.getCacho().getFirstByteIndex());
		int b = 1024 * 1024 * 4;
		InputStream fileInputStream = new BufferedInputStream(new FileInputStream(raf.getFD()));
		try {
			int s = request.getLength() / b;
			for (int i = 0; i < s; i++) {
				ChannelBuffer outBuffer = ChannelBuffers.buffer(b);
				while (outBuffer.writable()) {
					outBuffer.writeByte(fileInputStream.read());
				}
				e.getChannel().write(outBuffer);
			}
			ChannelBuffer outBuffer = ChannelBuffers.buffer(request.getLength() % b);
			while (outBuffer.writable()) {
				outBuffer.writeByte(fileInputStream.read());
			}
			e.getChannel().write(outBuffer).addListener(ChannelFutureListener.CLOSE);
			System.out.println("cacho sent ");
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
