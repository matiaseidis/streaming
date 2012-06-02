package org.test.streaming;

import java.io.OutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class CachoClientJandler extends SimpleChannelHandler {
	private CachoRequest cachoRequest;
	private OutputStream out;

	public CachoClientJandler(CachoRequest cachoRequest, OutputStream out) {
		this.setCachoRequest(cachoRequest);
		this.setOut(out);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		e.getChannel().write(this.getCachoRequest());
		System.out.println("Cacho " + this.getCachoRequest() + " requested.");
	}

	@Override
	public synchronized void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer cacho = (ChannelBuffer) e.getMessage();
		System.out.println("Received " + cacho.readableBytes() + " bytes");
		cacho.readBytes(out, cacho.readableBytes());
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
