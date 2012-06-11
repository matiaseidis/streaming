package org.test.streaming;

import java.util.LinkedList;
import java.util.List;

public class DummyMovieRetrievalPlan implements MovieRetrievalPlan {

	@Override
	public List<CachoRetrieval> getRequests() {
		List<CachoRetrieval> requests = new LinkedList<CachoRetrieval>();

		int totalSize = Conf.VIDEO_SIZE;
		int totalRequested = 0;
		int requestSize = 1024 * 1024 * 64;
		int amountOfRequests = 0;
		
		String movieFileName = Conf.VIDEO;
		while (totalSize - totalRequested >= requestSize) {
			requests.add(new CachoRetrieval(Conf.DIMON_HOST, Conf.DIMON_PORT, new CachoRequest(null, movieFileName, totalRequested, requestSize)));
			totalRequested += requestSize;
			amountOfRequests++;
		}
		requests.add(new CachoRetrieval(Conf.DIMON_HOST, Conf.DIMON_PORT, new CachoRequest(null, movieFileName, totalRequested, totalSize - totalRequested)));
		return requests;
	}
}
		
