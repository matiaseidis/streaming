package org.test.streaming;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class LibraryBasedMovieFileLocator implements MovieFileLocator {

	protected static final Log log = LogFactory.getLog(LibraryBasedMovieFileLocator.class);

	private File libraryPath;

	public LibraryBasedMovieFileLocator(String libraryDirPath) {
		this(new File(libraryDirPath));
	}

	public LibraryBasedMovieFileLocator(File libraryDirPath) {
		if (libraryDirPath.exists()) {
			this.setLibraryPath(libraryDirPath);
			log.info("Library path: " + libraryDirPath);
		} else {
			throw new IllegalArgumentException("Library path " + libraryDirPath + " does not exist.");
		}

	}

	public File getLibraryPath() {
		return libraryPath;
	}

	public void setLibraryPath(File libraryPath) {
		this.libraryPath = libraryPath;
	}

}
