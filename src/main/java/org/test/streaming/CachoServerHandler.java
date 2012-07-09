package org.test.streaming;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class CachoServerHandler extends SimpleChannelHandler {
	protected static final Log log = LogFactory.getLog(CachoServerHandler.class);

	private MovieFileLocator movieFileLocator;
	private Map<Channel, CachoDirection> channelStatus = new HashMap<Channel, CachoDirection>();

	private static final double MegabitsPerSec = 100;
	private static final double KbitsPerSec = MegabitsPerSec * 1000;
	double BytesPerSec = KbitsPerSec * 1000 / 8;
	double BytesPerMili = BytesPerSec / 1000;
	double transferCostFactor = 2;
	private Conf conf;

	public CachoServerHandler(Conf conf) {
		this.setConf(conf);
		File cachosDir = this.getConf().getCachosDir();
		this.setMovieFileLocator(new CompositeMovieFileLocator(new CompleteMovieFileLocator(cachosDir), new CachoMovieFileLocator(cachosDir)));
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		CachoDirection cachoDirection = this.getChannelStatus().get(e.getChannel());
		if (cachoDirection == null) {
			log.debug("Message arrived thru a new channel, starting up...");
			CachoRequest request = (CachoRequest) e.getMessage();
			this.getChannelStatus().put(e.getChannel(), request.getDirection());
			cachoDirection = request.getDirection();
			if (cachoDirection == CachoDirection.PULL) {
				this.sendCacho(e);
				this.getChannelStatus().remove(e.getChannel());
			} else if (cachoDirection == CachoDirection.PUSH) {
				// just wait for next messsage with cachos' bytes
				ChannelHandler objectDecoder = ctx.getPipeline().removeFirst();
				log.debug("Removed handler " + objectDecoder);
			}
		} else {
			log.debug("Receiving PUSH data...");
			// must be a push with actual cachos' bytes
			this.receiveCacho(ctx, e);
		}
	}

	private void receiveCacho(ChannelHandlerContext ctx, MessageEvent e) {

	}

	private void sendCacho(MessageEvent e) throws FileNotFoundException, IOException {
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

	public Conf getConf() {
		return conf;
	}

	public void setConf(Conf conf) {
		this.conf = conf;
	}

	public Map<Channel, CachoDirection> getChannelStatus() {
		return channelStatus;
	}

	public void setChannelStatus(Map<Channel, CachoDirection> channelStatus) {
		this.channelStatus = channelStatus;
	}

}
