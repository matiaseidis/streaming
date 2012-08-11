package org.test.streaming;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class CachoRequester implements ProgressObserver {

	private String host;
	private int port;
	private Map<CachoRequest, ProgressReport> progress = Collections.synchronizedMap(new TreeMap<CachoRequest, ProgressReport>());
	/**
	 * Maybe null
	 */
	private StreamingProgressObserver progressObserver;

	public CachoRequester(String host, int port) {
		this.setHost(host);
		this.setPort(port);
	}

	public void requestCacho(String movieFileName, int zeroBasedFirstBytePosition, int amountOfBytes, final OutputStream out) {
		CachoRequest cachoRequest = new CachoRequest(null, movieFileName, zeroBasedFirstBytePosition, amountOfBytes);

		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		CachoClientPullJandler cachoClientPullJandler = new CachoClientPullJandler(cachoRequest, out);
		cachoClientPullJandler.setProgressObserver(this);
		this.getProgress().put(cachoRequest, cachoClientPullJandler.getProgressReport());

		final ChannelPipeline pipeline = Channels.pipeline(new ObjectEncoder(), cachoClientPullJandler);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return pipeline;
			}
		});

		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(this.getHost(), this.getPort()));
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

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void progressed(ProgressReport progressReport) {
		this.getProgress().put((CachoRequest) progressReport.getTarget(), progressReport);
		if (this.getProgressObserver() != null) {
			this.getProgressObserver().progressed(this.getProgress());
		}
	}

	public Map<CachoRequest, ProgressReport> getProgress() {
		return progress;
	}

	public void setProgress(Map<CachoRequest, ProgressReport> progress) {
		this.progress = progress;
	}

	public StreamingProgressObserver getProgressObserver() {
		return progressObserver;
	}

	public void setProgressObserver(StreamingProgressObserver progressObserver) {
		this.progressObserver = progressObserver;
	}

}
