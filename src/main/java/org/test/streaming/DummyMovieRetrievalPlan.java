package org.test.streaming;

import java.util.LinkedList;
import java.util.List;

public class DummyMovieRetrievalPlan implements MovieRetrievalPlan {

	private final String videoId;
	private final Conf conf;

	public DummyMovieRetrievalPlan(String videoId, Conf conf) {
		super();
		this.videoId = videoId;
		this.conf = conf;
	}

	@Override
	public List<CachoRetrieval> getRequests() {
		List<CachoRetrieval> requests = new LinkedList<CachoRetrieval>();

		int totalSize = Integer.parseInt(conf.get("test.video.file.size"));
		int totalRequested = 0;
		int requestSize = 1024 * 1024 * 64;
		int amountOfRequests = 0;

		String movieFileName = conf.get("test.video.file.name");
		while (totalSize - totalRequested >= requestSize) {
			requests.add(new CachoRetrieval(conf.getDaemonHost(), conf.getDaemonPort(), new CachoRequest(null, movieFileName, totalRequested, requestSize)));
			totalRequested += requestSize;
			amountOfRequests++;
		}
		requests.add(new CachoRetrieval(conf.getDaemonHost(), conf.getDaemonPort(), new CachoRequest(null, movieFileName, totalRequested, totalSize - totalRequested)));
		return requests;
	}

	@Override
	public String getVideoId() {
		return videoId;
	}

}
