package org.test.streaming;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class CachoServerHandler extends SimpleChannelHandler {
	protected static final Log log = LogFactory.getLog(CachoServerHandler.class);

	private final String libraryDirPath;
	private MovieFileLocator movieFileLocator;

	private static final double MegabitsPerSec = 100;
	private static final double KbitsPerSec = MegabitsPerSec * 1000;
	double BytesPerSec = KbitsPerSec * 1000 / 8;
	double BytesPerMili = BytesPerSec / 1000;
	double transferCostFactor = 2;
	private Conf conf;
	
	public CachoServerHandler(Conf conf){
		this.conf = conf;
		libraryDirPath = conf.getCachosDir();
		movieFileLocator = new CompositeMovieFileLocator(new CompleteMovieFileLocator(libraryDirPath), new CachoMovieFileLocator(libraryDirPath));
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		CachoRequest request = (CachoRequest) e.getMessage();
		log.debug("Cacho requested  " + request);
		List<MovieCachoFile> files = this.getMovieFileLocator().locate(request);
		if (files == null) {
			log.error("This node cannot serve the request " + request + ", as an indication to thec counter-peer, the connection will be closed.");
			e.getChannel().close();
			return;
		}
		log.debug("Cacho file located: " + files);
		int t = 0;
		int b = 1024 * 1024 * 4;
		log.debug("Uploading cacho...");
		for (MovieCachoFile mayBeMovieFile : files) {
			RandomAccessFile raf = new RandomAccessFile(mayBeMovieFile.getMovieFile(), "r");
			raf.seek(mayBeMovieFile.getCacho().getFirstByteIndex());
			InputStream fileInputStream = new BufferedInputStream(new FileInputStream(raf.getFD()));
			try {
				int s = mayBeMovieFile.getCacho().getLength() / b;
				int r = mayBeMovieFile.getCacho().getLength() % b;
				for (int i = 0; i < s; i++) {
					ChannelBuffer outBuffer = ChannelBuffers.buffer(b);
					outBuffer.writeBytes(fileInputStream, outBuffer.writableBytes());
					t += outBuffer.readableBytes();
					e.getChannel().write(outBuffer);
				}

				if (r != 0) {
					ChannelBuffer outBuffer = ChannelBuffers.buffer(r);
					outBuffer.writeBytes(fileInputStream, outBuffer.writableBytes());
					t += outBuffer.readableBytes();
					e.getChannel().write(outBuffer);
				}
			} finally {
				fileInputStream.close();
			}
		}
		e.getChannel().write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		log.debug("Uploaded " + t + " bytes.");
	}

	public MovieFileLocator getMovieFileLocator() {
		return movieFileLocator;
	}

	public void setMovieFileLocator(MovieFileLocator movieFileLocator) {
		this.movieFileLocator = movieFileLocator;
	}
}
