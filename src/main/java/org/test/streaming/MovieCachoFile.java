package org.test.streaming;

import java.io.File;

import org.apache.commons.lang.builder.ToStringBuilder;

public class MovieCachoFile {

	private MovieCacho cacho;
	private File movieFile;

	public MovieCachoFile() {
	}

	public MovieCachoFile(MovieCacho cacho, File movieFile) {
		super();
		this.cacho = cacho;
		this.movieFile = movieFile;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public MovieCacho getCacho() {
		return cacho;
	}

	public void setCacho(MovieCacho cacho) {
		this.cacho = cacho;
	}

	public File getMovieFile() {
		return movieFile;
	}

	public void setMovieFile(File movieFile) {
		this.movieFile = movieFile;
	}

}
