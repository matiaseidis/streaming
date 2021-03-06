package org.test.streaming;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CompositeMovieFileLocator implements MovieFileLocator {

	public List<MovieFileLocator> locators = new LinkedList<MovieFileLocator>();

	public CompositeMovieFileLocator(MovieFileLocator... locators) {
		this.getLocators().addAll(Arrays.asList(locators));
	}

	@Override
	public List<MovieCachoFile> locate(CachoRequest request) {
		for (MovieFileLocator movieFileLocator : this.getLocators()) {
			List<MovieCachoFile> maybeMovieFile = movieFileLocator.locate(request);
			if (maybeMovieFile != null) {
				return maybeMovieFile;
			}
		}
		return null;
	}

	public List<MovieFileLocator> getLocators() {
		return locators;
	}

	public void setLocators(List<MovieFileLocator> locators) {
		this.locators = locators;
	}

}
