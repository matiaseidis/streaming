package org.test.streaming;

import java.util.LinkedList;
import java.util.List;

public class DummyMovieRetrievalPlan implements MovieRetrievalPlan {

	@Override
	public List<CachoRetrieval> getRequests() {
		List<CachoRetrieval> requests = new LinkedList<CachoRetrieval>();
		int totalSize = 5570947;
		int totalRequested = 0;
		int requestSize = 1024 * 512;
		int amountOfRequests = 0;
		System.err.println((totalSize / requestSize) + 1);
		while (totalSize - totalRequested >= requestSize) {
			requests.add(new CachoRetrieval("localhost", 10002, new CachoRequest(null, "a.mp4", totalRequested, requestSize)));
			totalRequested += requestSize;
			amountOfRequests++;
		}
		requests.add(new CachoRetrieval("localhost", 10002, new CachoRequest(null, "a.mp4", totalRequested, totalSize - totalRequested)));
		return requests;
	}
}
