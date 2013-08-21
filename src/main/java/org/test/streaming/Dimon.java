package org.test.streaming;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.oio.OioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;

public class Dimon extends SimpleChannelUpstreamHandler {
	protected static final Log log = LogFactory.getLog(Dimon.class);

	private final int port;
	private Conf conf;

	public Dimon(int port) {
		this.port = port;
		this.conf = new Conf();
	}

	public void run() {
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(new OioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		// bootstrap.setOption("child.sendBufferSize", 1024);
		// bootstrap.setOption("sendBufferSize", 1024);

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new ObjectDecoder(), new CachoServerHandler(conf));
			}
		});
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress("localhost", port));
		log.info("Dimon is ready, awaiting for Cacho requests on port " + this.port + "...");
		log.info(this.conf.getCachosDir());
	}

	public void stop() {
		/*
		 * TODO
		 */
	}

	public static void main(String[] args) throws Exception {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 10002;
		}
		new Dimon(port).run();
	}

}
