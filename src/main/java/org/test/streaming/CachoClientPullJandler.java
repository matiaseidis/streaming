package org.test.streaming;

import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

public class CachoClientPullJandler extends CachoClientHandler {
	protected static final Log log = LogFactory.getLog(CachoClientPullJandler.class);
	private OutputStream out;

	public CachoClientPullJandler(CachoRequest cachoRequest, OutputStream out) {
		super(cachoRequest);
		this.setOut(out);
	}

	@Override
	public synchronized void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer cacho = (ChannelBuffer) e.getMessage();
		int readableBytes = cacho.readableBytes();
		cacho.readBytes(out, readableBytes);
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

}
