package org.test.streaming;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class CachoRequester {

	private final String host;
	private final int port;

	public static void main(String[] args) throws IOException {
		BufferedOutputStream baos = new BufferedOutputStream(new FileOutputStream(new File("sandonga.mp4")));
		new DefaultMovieRetrievalPlanInterpreter().interpret(new DummyMovieRetrievalPlan(), baos);
		// 421732944
		// 5570947
		baos.flush();
		baos.close();

	}

	public CachoRequester(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void requestCacho(String movieFileName, int zeroBasedFirstBytePosition, int amountOfBytes, final OutputStream out) {
		CachoRequest cachoRequest = new CachoRequest(null, movieFileName, zeroBasedFirstBytePosition, amountOfBytes);

		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		final ChannelPipeline pipeline = Channels.pipeline(new ObjectEncoder(), new CachoClientJandler(cachoRequest, out));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return pipeline;
			}
		});

		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		// Wait until the connection is closed or the connection attempt fails.
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		// Shut down thread pools to exit.
		bootstrap.releaseExternalResources();
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
