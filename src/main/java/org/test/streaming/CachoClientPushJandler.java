package org.test.streaming;

import java.io.InputStream;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;

public class CachoClientPushJandler extends CachoClientHandler {

	private InputStream input;

	public CachoClientPushJandler(CachoRequest cachoRequest, InputStream input) {
		super(cachoRequest);
		this.setInput(input);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelConnected(ctx, e);
		ctx.getPipeline().removeFirst();
		long skip = this.getInput().skip(this.getCachoRequest().getFirstByteIndex());
		if (skip != this.getCachoRequest().getFirstByteIndex()) {
			log.fatal("Failed to skip requested offset in " + this.getCachoRequest() + " cacho upload canceled.");
			e.getChannel().write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		} else {
			new CachoWriter().uploadCacho(e.getChannel(), this.getInput(), this.getCachoRequest().getLength());
			e.getChannel().write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}

	public InputStream getInput() {
		return input;
	}

	public void setInput(InputStream input) {
		this.input = input;
	}

}
