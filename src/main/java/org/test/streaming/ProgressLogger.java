package org.test.streaming;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class ProgressLogger implements StreamingProgressObserver {

	int count;

	@Override
	public void progressed(Map<CachoRequest, ProgressReport> progress) {
		count++;
		if (count % 20 == 0) {
			print(progress);
		}
	}

	private void print(Map<CachoRequest, ProgressReport> progress) {
		Set<Entry<CachoRequest, ProgressReport>> entrySet = progress.entrySet();
		for (Entry<CachoRequest, ProgressReport> entry : entrySet) {
			String firstByteIndex = String.valueOf(entry.getKey().getFirstByteIndex());
			long msToComplete = entry.getValue().getMsToComplete();
			String ttg = String.valueOf(msToComplete >= 0 ? msToComplete : "-");
			System.out.println(firstByteIndex + StringUtils.repeat("-", 15 - firstByteIndex.length()) + entry.getValue().getProgressPct() + "%, " + ttg + " ms to complete.");
		}
		System.out.println("-------------------------------------------");
	}

	@Override
	public void done(Map<CachoRequest, ProgressReport> progress) {
		print(progress);
		Set<Entry<CachoRequest, ProgressReport>> entrySet = progress.entrySet();
		for (Entry<CachoRequest, ProgressReport> entry : entrySet) {
			String firstByteIndex = String.valueOf(entry.getKey().getFirstByteIndex());
			String elapsed = String.valueOf(entry.getValue().getMsDownloading());
			System.out.println(firstByteIndex + StringUtils.repeat("-", 15 - firstByteIndex.length()) + " downloaded in " + elapsed + " ms.");
		}
		System.out.println("-------------------------------------------");

	}
}
