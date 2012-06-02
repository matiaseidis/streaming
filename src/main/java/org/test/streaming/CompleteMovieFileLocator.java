package org.test.streaming;

import java.io.File;

public class CompleteMovieFileLocator extends LibraryBasedMovieFileLocator {

	public CompleteMovieFileLocator(File libraryDirPath) {
		super(libraryDirPath);
	}

	public CompleteMovieFileLocator(String libraryDirPath) {
		super(libraryDirPath);
	}

	@Override
	public File locate(CachoRequest request) {
		File movieFile = new File(this.getLibraryPath(), request.getFileName());
		if (movieFile.exists()) {
			return movieFile;
		}
		return null;
	}

}
