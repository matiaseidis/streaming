package org.test.streaming;

import java.util.Map;

public interface StreamingProgressObserver {

	public void progressed(Map<CachoRequest, ProgressReport> progress);

	public void done(Map<CachoRequest, ProgressReport> progress);

}
