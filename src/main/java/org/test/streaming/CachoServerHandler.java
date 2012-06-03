package org.test.streaming;

import java.io.IOException;
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
		int read = -1;
		ChannelBuffer outBuffer = ChannelBuffers.buffer(request.getLength());
		long readBytesCOunt = 0;
		try {
			while ((read = raf.read()) != -1 && readBytesCOunt < request.getLength()) {
				// TODO mandar el archivo de a rafagas, no esperar a leerlo
				// todo, para no ocupar mucha memoria
				outBuffer.writeByte(read);
				readBytesCOunt++;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			raf.close();
		}
		System.out.println("Sending " + outBuffer.readableBytes() + " bytes");
		e.getChannel().write(outBuffer).addListener(ChannelFutureListener.CLOSE);
		System.out.println("Cacho written.");
	}

	public MovieFileLocator getMovieFileLocator() {
		return movieFileLocator;
	}

	public void setMovieFileLocator(MovieFileLocator movieFileLocator) {
		this.movieFileLocator = movieFileLocator;
	}
}
