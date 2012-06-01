package org.test.streaming;
import java.io.OutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class CachoClientJandler extends SimpleChannelHandler {
	private byte cachoNumber;
	private OutputStream out;

	public CachoClientJandler(byte cachoNumber, OutputStream out) {
		this.cachoNumber = cachoNumber;
		this.out = out;
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		ChannelBuffer buffer = ChannelBuffers.buffer(1);
		buffer.writeByte(this.cachoNumber);
		e.getChannel().write(buffer);
		System.out.println("Cacho " + cachoNumber + " requested.");
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		System.out.println("CachoClientJandler.messageReceived()");
		ChannelBuffer cacho = (ChannelBuffer) e.getMessage();
		cacho.getBytes(cacho.arrayOffset(), out, cacho.readableBytes());
	}

}