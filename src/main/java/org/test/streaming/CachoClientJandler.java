package org.test.streaming;

import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class CachoClientJandler extends SimpleChannelHandler {
	protected static final Log log = LogFactory.getLog(CachoClientJandler.class);
	private CachoRequest cachoRequest;
	private OutputStream out;

	public CachoClientJandler(CachoRequest cachoRequest, OutputStream out) {
		this.setCachoRequest(cachoRequest);
		this.setOut(out);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		e.getChannel().write(this.getCachoRequest());
		log.debug("Cacho " + this.getCachoRequest() + " requested.");
	}

	@Override
	public synchronized void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer cacho = (ChannelBuffer) e.getMessage();
		int readableBytes = cacho.readableBytes();
		cacho.readBytes(out, readableBytes);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		log.error("Exception downloading cacho " + this.getCachoRequest(), e.getCause());
		if (ctx.getChannel().isOpen()) {
			log.debug("Closing peer connection...");
			ctx.getChannel().close();
		} else {
			log.debug("Connection already closed.");
		}
	}

	public CachoRequest getCachoRequest() {
		return cachoRequest;
	}

	public void setCachoRequest(CachoRequest cachoRequest) {
		this.cachoRequest = cachoRequest;
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

}
