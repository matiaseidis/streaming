package org.test.streaming;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
	public List<MovieCachoFile> locate(CachoRequest request) {
		log.debug("Searching for cacho file in " + this.getLibraryPath());
		String[] allMovieFileNames = this.getLibraryPath().list();
		List<MoviePartMetadata> overlaps = new LinkedList<MoviePartMetadata>();
		List<MovieCachoFile> r = new LinkedList<MovieCachoFile>();
		for (int i = 0; i < allMovieFileNames.length; i++) {
			String eachMovieFileName = allMovieFileNames[i];
			if (eachMovieFileName.startsWith(request.getFileName())) {
				try {
					log.debug("Searching for cacho file: " + eachMovieFileName);
					MoviePartMetadata moviePartMetadata = new MoviePartMetadata(new File(this.getLibraryPath(), eachMovieFileName));
					MovieCacho candidate = moviePartMetadata.getCacho().getCacho();
					if (candidate.overlaps(request.getCacho()) != null) {
						overlaps.add(moviePartMetadata);
					}
				} catch (InvalidMovieMetadataException e2) {
					log.warn(e2.getMessage());
				}
			}
		}

		MovieCacho curentRequest = new MovieCacho(request.getCacho().getFirstByteIndex(), request.getCacho().getLength());
		int c = 0;
		while (c < request.getLength() && !overlaps.isEmpty()) {
			for (Iterator iterator = overlaps.iterator(); iterator.hasNext();) {
				MoviePartMetadata moviePartMetadata = (MoviePartMetadata) iterator.next();
				MovieCacho candidate = moviePartMetadata.getCacho().getCacho();
				MovieCacho overlap = curentRequest.overlaps(candidate);
				if (overlap == null) {
					iterator.remove();
				} else if (overlap.getFirstByteIndex() == curentRequest.getFirstByteIndex()) {
					r.add(new MovieCachoFile(candidate.translate(overlap), moviePartMetadata.getCacho().getMovieFile()));
					curentRequest.substract(overlap);
					c += overlap.getLength();
				}
			}
		}
		if (c == request.getLength()) {
			return r;
		} else {
			return null;
		}
	}
}
