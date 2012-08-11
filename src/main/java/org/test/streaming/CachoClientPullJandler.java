package org.test.streaming;

import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

public class CachoClientPullJandler extends CachoClientHandler {
	protected static final Log log = LogFactory.getLog(CachoClientPullJandler.class);
	private OutputStream out;
	private long firstChunnkTimestamp;
	private ProgressReport progressReport;
	private ProgressObserver progressObserver;

	public CachoClientPullJandler(CachoRequest cachoRequest, OutputStream out) {
		super(cachoRequest);
		this.setOut(out);
		this.setProgressReport(new ProgressReport(cachoRequest));
		this.setFirstChunnkTimestamp(System.currentTimeMillis());
	}

	@Override
	public synchronized void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer cacho = (ChannelBuffer) e.getMessage();
		int readableBytes = cacho.readableBytes();
		cacho.readBytes(this.getOut(), readableBytes);
		long now = System.currentTimeMillis();
		this.setAmountOfReceivedBytes(this.getAmountOfReceivedBytes() + readableBytes);
		int length = this.getCachoRequest().getLength();
		int remainingBytes = length - this.getAmountOfReceivedBytes();
		long deltaT = now - this.getFirstChunnkTimestamp();
		this.setMsDownloading(deltaT);
		long remainingTime = remainingBytes * deltaT / this.getAmountOfReceivedBytes();
		this.setMsToComplete(remainingTime);
		this.setProgressPct((int) (((double) this.getAmountOfReceivedBytes() / length) * 100));
		this.getProgressObserver().progressed(this.getProgressReport());
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(OutputStream out) {
		this.out = out;
	}

	public int getAmountOfReceivedBytes() {
		return this.getProgressReport().getAmountOfReceivedBytes();
	}

	public void setAmountOfReceivedBytes(int amountOfReceivedBytes) {
		this.getProgressReport().setAmountOfReceivedBytes(amountOfReceivedBytes);
	}

	public long getMsToComplete() {
		return this.getProgressReport().getMsToComplete();
	}

	public void setMsToComplete(long msToComplete) {
		this.getProgressReport().setMsToComplete(msToComplete);
	}

	public long getFirstChunnkTimestamp() {
		return firstChunnkTimestamp;
	}

	public void setFirstChunnkTimestamp(long firstChunnkTimestamp) {
		this.firstChunnkTimestamp = firstChunnkTimestamp;
	}

	public void setProgressPct(int progressPct) {
		this.getProgressReport().setProgressPct(progressPct);
	}

	public void setMsDownloading(long msDownloading) {
		this.getProgressReport().setMsDownloading(msDownloading);
	}

	public ProgressReport getProgressReport() {
		return progressReport;
	}

	public void setProgressReport(ProgressReport progressReport) {
		this.progressReport = progressReport;
	}

	public ProgressObserver getProgressObserver() {
		return progressObserver;
	}

	public void setProgressObserver(ProgressObserver progressObserver) {
		this.progressObserver = progressObserver;
	}

}
