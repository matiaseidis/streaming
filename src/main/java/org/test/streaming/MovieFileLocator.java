package org.test.streaming;

import java.io.File;

public interface MovieFileLocator {

	public File locate(CachoRequest request);

}
