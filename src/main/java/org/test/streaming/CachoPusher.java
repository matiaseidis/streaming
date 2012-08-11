package org.test.streaming;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class CachoPusher {

	private String remoteDaemonHost;
	private int port;

	public CachoPusher(String remoteDaemonHost, int port) {
		this.setRemoteDaemonHost(remoteDaemonHost);
		this.setPort(port);
	}

	public void push(InputStream input, String movieFileName, int zeroBasedFirstBytePosition, int amountOfBytes) {
		CachoRequest cachoRequest = new CachoRequest(null, movieFileName, zeroBasedFirstBytePosition, amountOfBytes, CachoDirection.PUSH);

		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the pipeline factory.
		BufferedInputStream input2 = new BufferedInputStream(input);
		final ChannelPipeline pipeline = Channels.pipeline(new ObjectEncoder(), new CachoClientPushJandler(cachoRequest, input2));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return pipeline;
			}
		});

		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(this.getRemoteDaemonHost(), this.getPort()));
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		// Wait until the connection is closed or the connection attempt fails.
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		// Shut down thread pools to exit.
		bootstrap.releaseExternalResources();
		try {
			input2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		if (args.length != 5) {
			System.out.println("CachoPusher movieFileName zeroBasedFirstByteIndex amountOfBytes remoteHost remotePort");
			System.exit(0);
		}
		String movieFileName = args[0];
		String firstBteArg = args[1];
		String lengthArg = args[2];
		String host = args[3];
		String portArg = args[4];
		File file = new File(movieFileName);
		try {
			int firstByte = Integer.parseInt(firstBteArg);
			int length = Integer.parseInt(lengthArg);
			int port = Integer.parseInt(portArg);
			FileInputStream fileInputStream = new FileInputStream(file);

			new CachoPusher(host, port).push(fileInputStream, movieFileName, firstByte, length);
		} catch (NumberFormatException e) {
			System.out.println("CachoPusher movieFileName zeroBasedFirstByteIndex amountOfBytes");
			System.exit(0);
		} catch (FileNotFoundException e) {
			System.err.println("File " + movieFileName + " does not exists.");
			System.exit(0);
		}

	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getRemoteDaemonHost() {
		return remoteDaemonHost;
	}

	public void setRemoteDaemonHost(String remoteDaemonHost) {
		this.remoteDaemonHost = remoteDaemonHost;
	}

}
