package org.test.streaming;

import java.io.File;

public abstract class LibraryBasedMovieFileLocator implements MovieFileLocator {

	private File libraryPath = new File("./");

	public LibraryBasedMovieFileLocator(String libraryDirPath) {
		this.setLibraryPath(new File(libraryDirPath));
	}

	public LibraryBasedMovieFileLocator(File libraryDirPath) {
		this.setLibraryPath(libraryDirPath);
	}

	public File getLibraryPath() {
		return libraryPath;
	}

	public void setLibraryPath(File libraryPath) {
		this.libraryPath = libraryPath;
	}

}
