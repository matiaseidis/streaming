package org.test.streaming;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CompleteMovieFileLocator extends LibraryBasedMovieFileLocator {

	protected static final Log log = LogFactory.getLog(CompleteMovieFileLocator.class);

	public CompleteMovieFileLocator(File libraryDirPath) {
		super(libraryDirPath);
	}

	public CompleteMovieFileLocator(String libraryDirPath) {
		super(libraryDirPath);
	}

	@Override
	public List<MovieCachoFile> locate(CachoRequest request) {
		File movieFile = new File(this.getLibraryPath(), request.getFileName());
		if (movieFile.exists()) {
			LinkedList<MovieCachoFile> r = new LinkedList<MovieCachoFile>();
			r.add(new MovieCachoFile(request.getCacho(), movieFile));
			return r;
		} else {
			log.debug("Cacho request " + request + " counldn't be satisfied with a full movie file.");
		}
		return null;
	}

}
