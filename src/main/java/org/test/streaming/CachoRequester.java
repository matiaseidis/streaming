package org.test.streaming;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class CachoRequester {

	private final String host;
	private final int port;

	public static void main(String[] args) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CachoRequester cachoRequester = new CachoRequester("localhost", 10002);
		for (byte i = 0; i < 6; i++) {
			cachoRequester.requestCacho(i, baos);
		}
		System.out.println(baos);
	}

	public CachoRequester(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void requestCacho(final byte cachoNumber, final OutputStream out) {
		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		final ChannelPipeline pipeline = Channels
				.pipeline(new CachoClientJandler(cachoNumber, out));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return pipeline;
			}
		});

		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,
				port));

		// Wait until the connection is closed or the connection attempt fails.
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		System.out.println("CachoRequester.requestCacho()");
		// Shut down thread pools to exit.
		bootstrap.releaseExternalResources();
	}

}
