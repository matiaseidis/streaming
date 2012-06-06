package org.test.streaming;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class CachoServerHandler extends SimpleChannelHandler {

	private static final String LIBRARY_DIR_PATH = "C:\\cachos\\";
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
		InputStream fileInputStream = new BufferedInputStream(new FileInputStream(raf.getFD()), b);
		try {
			int s = request.getLength() / b;
			ChannelBuffer outBuffer = ChannelBuffers.buffer(b);
			for (int i = 0; i < s; i++) {
				while (outBuffer.writable()) {
					outBuffer.writeByte(fileInputStream.read());
				}
				e.getChannel().write(outBuffer);
				outBuffer.clear();
			}
			while (outBuffer.readableBytes() < s % b) {
				outBuffer.writeByte(fileInputStream.read());
			}
			e.getChannel().write(outBuffer).addListener(ChannelFutureListener.CLOSE);
		} finally {
			fileInputStream.close();
		}
	}

	public MovieFileLocator getMovieFileLocator() {
		return movieFileLocator;
	}

	public void setMovieFileLocator(MovieFileLocator movieFileLocator) {
		this.movieFileLocator = movieFileLocator;
	}
}
