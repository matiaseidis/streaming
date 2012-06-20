package org.test.streaming;

import java.util.List;


public interface MovieFileLocator {

	public List<MovieCachoFile> locate(CachoRequest request);

}
