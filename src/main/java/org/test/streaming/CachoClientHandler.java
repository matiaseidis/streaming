package org.test.streaming;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class CachoClientHandler extends SimpleChannelHandler {
	protected static final Log log = LogFactory.getLog(CachoClientHandler.class);
	private CachoRequest cachoRequest;

	public CachoClientHandler(CachoRequest cachoRequest) {
		this.setCachoRequest(cachoRequest);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		e.getChannel().write(this.getCachoRequest());
		log.debug("Cacho " + this.getCachoRequest() + " requested.");
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		// TODO El server cerro la conexion, no mas bytes disponibles para este
		// cacho, habria que disparar un mecanismo de busqueda de los bytes que
		// faltan.
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

}
