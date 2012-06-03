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
	public MovieCachoFile locate(CachoRequest request) {
		File movieFile = new File(this.getLibraryPath(), request.getFileName());
		if (movieFile.exists()) {
			return new MovieCachoFile(request.getCacho(), movieFile);
		}
		return null;
	}

}
