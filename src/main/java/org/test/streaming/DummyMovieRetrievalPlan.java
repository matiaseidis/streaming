package org.test.streaming;

import java.util.LinkedList;
import java.util.List;

public class DummyMovieRetrievalPlan implements MovieRetrievalPlan {

	@Override
	public List<CachoRetrieval> getRequests() {
		List<CachoRetrieval> requests = new LinkedList<CachoRetrieval>();
		// int totalSize = 5570947;
		int totalSize = 421732944;
		int totalRequested = 0;
		int requestSize = 1024 * 1024 * 64;
		int amountOfRequests = 0;
		System.err.println((totalSize / requestSize) + 1);
		// String movieFileName = "a.mp4";
		String movieFileName = "Luther.S02E01.720p.HDTV.x264.2.mp4";
		while (totalSize - totalRequested >= requestSize) {
			requests.add(new CachoRetrieval("localhost", 10002, new CachoRequest(null, movieFileName, totalRequested, requestSize)));
			totalRequested += requestSize;
			amountOfRequests++;
		}
		requests.add(new CachoRetrieval("localhost", 10002, new CachoRequest(null, movieFileName, totalRequested, totalSize - totalRequested)));
		return requests;
	}
}
		