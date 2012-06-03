package org.test.streaming;

import java.io.File;

public interface MovieFileLocator {

	public MovieCachoFile locate(CachoRequest request);

}
