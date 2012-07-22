package org.test.streaming;

import java.util.LinkedList;
import java.util.List;

public class WatchMovieRetrievalPlan implements MovieRetrievalPlan {
	
	private final String videoId;
//	private final Conf conf;
	private final List<CachoRetrieval> requests = new LinkedList<CachoRetrieval>();

	public WatchMovieRetrievalPlan(String videoId/*, Conf conf*/) {
		super();
		this.videoId = videoId;
//		this.conf = conf;
	}

	@Override
	public List<CachoRetrieval> getRequests() {
		return requests;
	}

	@Override
	public String getVideoId() {
		return videoId;
	}
}
