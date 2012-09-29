package org.test.streaming;

import java.util.List;

public class ManualPushRetrievalPlan implements MovieRetrievalPlan {

	private List<CachoRetrieval> requests;
	private String videoId;
	
	public ManualPushRetrievalPlan(String videoId, List<CachoRetrieval> requests){
		this.requests = requests;
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
