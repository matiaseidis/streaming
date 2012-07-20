package org.test.streaming;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	private Index index = new NullIndex();

	private OutputStream pushedCachoStream;
	private CachoRequest currentRequest;
	private int receivedBytes = 0;
	private MoviePartMetadata receivingCachoMetadata;

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
				this.sendCacho(ctx, e);
				this.getChannelStatus().remove(e.getChannel());
			} else if (cachoDirection == CachoDirection.PUSH) {
				// just wait for next messsage with cachos' bytes
				ChannelHandler objectDecoder = ctx.getPipeline().removeFirst();
				log.debug("Removed handler " + objectDecoder);

				this.setCurrentRequest(request);
				this.setReceivedBytes(0);
				MoviePartMetadata moviePartMetadata = new MoviePartMetadata(this.getConf().getTempDir(), request.getFileName(), request.getFirstByteIndex(), request.getLength());
				this.setReceivingCachoMetadata(moviePartMetadata);
				this.setPushedCachoStream(new BufferedOutputStream(new FileOutputStream(moviePartMetadata.getCacho().getMovieFile())));
			}
		} else {
			// must be a push with actual cachos' bytes
			this.receiveCacho(ctx, e);
		}
	}

	private void receiveCacho(ChannelHandlerContext ctx, MessageEvent e) throws IOException {
		ChannelBuffer cacho = (ChannelBuffer) e.getMessage();
		int readableBytes = cacho.readableBytes();
		cacho.readBytes(this.getPushedCachoStream(), readableBytes);
		this.setReceivedBytes(this.getReceivedBytes() + readableBytes);
		if (this.getReceivedBytes() == this.getCurrentRequest().getLength()) {
			this.getPushedCachoStream().close();
			log.info("Cacho received successfully.");
			File receivedCacho = this.getReceivingCachoMetadata().getCacho().getMovieFile();
			File dest = new File(this.getConf().getCachosDir(), receivedCacho.getName());
			if (!receivedCacho.renameTo(dest)) {
				log.warn("Failed to move cacho file " + receivedCacho + " to share dir: " + dest);
				dest = receivedCacho;
			}
			this.getIndex().newCachoAvailableLocally(this.getReceivingCachoMetadata().getCacho());
			this.getChannelStatus().remove(e.getChannel());
		}
	}

	private void sendCacho(ChannelHandlerContext ctx, MessageEvent e) throws FileNotFoundException, IOException {
		CachoRequest request = (CachoRequest) e.getMessage();
		log.debug("Cacho requested  " + request);
		List<MovieCachoFile> files = this.getMovieFileLocator().locate(request);
		if (files == null) {
			log.error("This node cannot serve the request " + request + ", as an indication to thec counter-peer, the connection will be closed.");
			e.getChannel().close();
			return;
		}
		log.debug("Cacho file located: " + files);
		log.debug("Uploading cacho...");
		for (MovieCachoFile mayBeMovieFile : files) {
			RandomAccessFile raf = new RandomAccessFile(mayBeMovieFile.getMovieFile(), "r");
			raf.seek(mayBeMovieFile.getCacho().getFirstByteIndex());
			InputStream fileInputStream = new BufferedInputStream(new FileInputStream(raf.getFD()));
			new CachoWriter().uploadCacho(e.getChannel(), fileInputStream, mayBeMovieFile.getCacho().getLength());
		}
		e.getChannel().write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
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

	public OutputStream getPushedCachoStream() {
		return pushedCachoStream;
	}

	public void setPushedCachoStream(OutputStream pushedCachoStream) {
		this.pushedCachoStream = pushedCachoStream;
	}

	public CachoRequest getCurrentRequest() {
		return currentRequest;
	}

	public void setCurrentRequest(CachoRequest currentRequest) {
		this.currentRequest = currentRequest;
	}

	public int getReceivedBytes() {
		return receivedBytes;
	}

	public void setReceivedBytes(int receivedBytes) {
		this.receivedBytes = receivedBytes;
	}

	public MoviePartMetadata getReceivingCachoMetadata() {
		return receivingCachoMetadata;
	}

	public void setReceivingCachoMetadata(MoviePartMetadata receivingCachoMetadata) {
		this.receivingCachoMetadata = receivingCachoMetadata;
	}

	public Index getIndex() {
		return index;
	}

	public void setIndex(Index index) {
		this.index = index;
	}

}
