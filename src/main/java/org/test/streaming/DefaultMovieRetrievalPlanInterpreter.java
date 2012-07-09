package org.test.streaming;

import java.io.File;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultMovieRetrievalPlanInterpreter implements MovieRetrievalPlanInterpreter {
	protected static final Log log = LogFactory.getLog(DefaultMovieRetrievalPlanInterpreter.class);
	private File tempDir;
	private File shareDir;

	public DefaultMovieRetrievalPlanInterpreter(File shareDir, File tempDir) {
		this.setShareDir(shareDir);
		this.setTempDir(tempDir);
	}

	@Override
	public void interpret(MovieRetrievalPlan plan, OutputStream out) {
		List<CachoRetrieval> requests = plan.getRequests();
		final List<CachoStreamer> streamers = new LinkedList<CachoStreamer>();
		List<Runnable> tasks = new LinkedList<Runnable>();
		final CountDownLatch streamFinishedLatch = new CountDownLatch(1);
		CachoRetrieval firstCacho = requests.get(0);
		final CachoRequester cachoRequester = new CachoRequester(firstCacho.getHost(), firstCacho.getPort());
		final CachoRequest request = firstCacho.getRequest();
		final DirectCachoStreamer firstStreamer = new DirectCachoStreamer(this.getShareDir(), out, request.getLength(), this.createPartFile(request), new OnCachoComplete() {

			@Override
			public void onCachoComplete(CachoStreamer streamer) {
				streamers.get(0).stream();
			}
		});
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				cachoRequester.requestCacho(request.getFileName(), request.getFirstByteIndex(), request.getLength(), firstStreamer);
			}
		};
		tasks.add(runnable);
		for (final CachoRetrieval a : requests.subList(1, requests.size())) {
			final BackgroundCachoStreamer cachoStreamer = new BackgroundCachoStreamer(this.getShareDir(), this.createPartFile(a.getRequest()), out, a.getRequest().getFirstByteIndex(), a.getRequest().getLength(), new OnCachoComplete() {

				@Override
				public void onCachoComplete(CachoStreamer streamer) {
					log.debug("Looking for next streamer...");
					int indexOf = streamers.indexOf(streamer);
					if (indexOf < streamers.size() - 1) {
						log.debug("Next streamer found: " + indexOf + "->" + (indexOf + 1));
						streamers.get(indexOf + 1).stream();
					} else {
						log.debug("No more streamers, streaming finished.");
						streamFinishedLatch.countDown();
					}
				}
			});
			streamers.add(cachoStreamer);
			Runnable task = new Runnable() {

				@Override
				public void run() {
					cachoRequester.requestCacho(a.getRequest().getFileName(), a.getRequest().getFirstByteIndex(), a.getRequest().getLength(), cachoStreamer);
				}
			};
			tasks.add(task);
		}

		List<Thread> pool = new LinkedList<Thread>();
		for (Runnable r : tasks) {
			Thread thread = new Thread(r);
			pool.add(thread);
			thread.start();
		}

		for (Thread thread : pool) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.debug("Downloading finished, still streaming...");
		try {
			streamFinishedLatch.await();
			log.debug("Streaming finished OK");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File createPartFile(CachoRequest request) {
		return new MoviePartMetadata(this.getTempDir(), request.getFileName(), request.getFirstByteIndex(), request.getLength()).getCacho().getMovieFile();
	}

	public File getTempDir() {
		return tempDir;
	}

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

	public File getShareDir() {
		return shareDir;
	}

	public void setShareDir(File shareDir) {
		this.shareDir = shareDir;
	}

}
