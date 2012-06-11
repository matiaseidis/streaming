package org.test.streaming;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CachoMovieFileLocator extends LibraryBasedMovieFileLocator {

	private static final Log log = LogFactory.getLog(CachoMovieFileLocator.class);

	public CachoMovieFileLocator(File libraryDirPath) {
		super(libraryDirPath);
	}

	public CachoMovieFileLocator(String libraryDirPath) {
		super(libraryDirPath);
	}

	@Override
	public MovieCachoFile locate(CachoRequest request) {
		log.debug("Searching for cacho file in " + this.getLibraryPath());
		String[] allMovieFileNames = this.getLibraryPath().list();
		for (int i = 0; i < allMovieFileNames.length; i++) {
			String eachMovieFileName = allMovieFileNames[i];
			if (eachMovieFileName.startsWith(request.getFileName())) {
				try {
					MoviePartMetadata moviePartMetadata = new MoviePartMetadata(eachMovieFileName);
					if (moviePartMetadata.getCacho().contains(request.getCacho())) {
						File movieFile = new File(this.getLibraryPath(), eachMovieFileName);
						if (movieFile.exists()) {
							return new MovieCachoFile(moviePartMetadata.getCacho().subCacho(request.getCacho()), movieFile);
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
