package org.test.streaming;

import java.io.File;

public class CachoMovieFileLocator extends LibraryBasedMovieFileLocator {

	public CachoMovieFileLocator(File libraryDirPath) {
		super(libraryDirPath);
	}

	public CachoMovieFileLocator(String libraryDirPath) {
		super(libraryDirPath);
	}

	@Override
	public File locate(CachoRequest request) {
		String[] allMovieFileNames = this.getLibraryPath().list();
		for (int i = 0; i < allMovieFileNames.length; i++) {
			String eachMovieFileName = allMovieFileNames[i];
			if (eachMovieFileName.startsWith(request.getFileName())) {
				try {
					MoviePartMetadata moviePartMetadata = new MoviePartMetadata(eachMovieFileName);
					if (moviePartMetadata.getCacho().contains(request.getCacho())) {
						File movieFile = new File(this.getLibraryPath(), eachMovieFileName);
						if (movieFile.exists()) {
							return movieFile;
						}
					}
				} catch (InvalidMovieMetadataException e2) {
					e2.printStackTrace();
				}
			}
		}
		return null;
	}

}
