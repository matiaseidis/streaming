package org.test.streaming;

import java.util.ArrayList;
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
		String daemonHost = null;
		while (totalSize - totalRequested >= requestSize) {
			if (amountOfRequests % 2 == 0) {
				daemonHost = "ec2-54-214-146-17.us-west-2.compute.amazonaws.com";
			} else {
				daemonHost = "ec2-54-212-135-180.us-west-2.compute.amazonaws.com";
			}
			requests.add(new CachoRetrieval(daemonHost, conf.getDaemonPort(), new CachoRequest(null, movieFileName, totalRequested, requestSize)));
			totalRequested += requestSize;
			amountOfRequests++;
		}
		if (amountOfRequests % 2 == 0) {
			daemonHost = "ec2-54-214-146-17.us-west-2.compute.amazonaws.com";
		} else {
			daemonHost = "ec2-54-212-135-180.us-west-2.compute.amazonaws.com";
		}

		requests.add(new CachoRetrieval(daemonHost, conf.getDaemonPort(), new CachoRequest(null, movieFileName, totalRequested, totalSize - totalRequested)));
		return requests;
	}

	@Override
	public String getVideoId() {
		return videoId;
	}

}
