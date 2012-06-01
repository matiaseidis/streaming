package org.test.streaming;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class CachoServerHandler extends SimpleChannelHandler {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		ChannelBuffer buf = dynamicBuffer();
		buf.writeBytes((ChannelBuffer) e.getMessage());
		if (buf.readableBytes() == 1) {
			this.sendCacho(buf.readByte(), e.getChannel());
		}
	}

	private void sendCacho(byte partNumber, Channel channel) {
		System.out.println("Cacho requested  " + partNumber);
		// File file = new
		// File("E:\\Lucas\\workspaces\\sts-2.5.1\\streamin\\a.mp4.part." +
		// partNumber);
		File file = new File("./a.mp4.part." + partNumber);
		InputStream cachoInputStream = null;
		try {
			cachoInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int read = -1;
		ChannelBuffer outBuffer = dynamicBuffer();
		try {
			while ((read = cachoInputStream.read()) != -1) {
				outBuffer.writeByte(read);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Sending " + outBuffer.readableBytes());
		channel.write(outBuffer).addListener(ChannelFutureListener.CLOSE);
		System.out.println("Cacho written.");
	}
}
