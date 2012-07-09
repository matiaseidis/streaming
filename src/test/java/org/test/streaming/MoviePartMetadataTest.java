package org.test.streaming;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import junit.framework.Assert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

public class MoviePartMetadataTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testParse() {
		assertThat(moviePartMetadatForFileName("a-0-2.part"), hasMovieCacho(equalTo(make(aCacho().startingFrom(0).withLenght(2)))));
		assertThat(moviePartMetadatForFileName("b-a-0-2.part"), hasMovieCacho(equalTo(make(aCacho().startingFrom(0).withLenght(2)))));
		assertThat(moviePartMetadatForFileName("--------b-a-0-2.part"), hasMovieCacho(equalTo(make(aCacho().startingFrom(0).withLenght(2)))));
		assertThat(moviePartMetadatForFileName("-0-2.part"), hasMovieCacho(equalTo(make(aCacho().startingFrom(0).withLenght(2)))));
	}

	private MoviePartMetadata moviePartMetadatForFileName(String movieFileName) {
		return new MoviePartMetadata(new File(movieFileName));
	}

	public MoviePartMetadataCachoMatcher hasMovieCacho(Matcher<MovieCacho> cachoMatcher) {
		return new MoviePartMetadataCachoMatcher(cachoMatcher);
	}

	@Test
	public void testMovieFileIsProperlyCreated() throws Exception {
		Conf conf = new Conf("/test-conf.properties");
		String movieName = "Luther.S02E01.720p.HDTV.x264-3.mp3";
		MoviePartMetadata moviePartMetadata = new MoviePartMetadata(conf.getCachosDir(), movieName, 0, 1024);
		File actualMovieFile = moviePartMetadata.getCacho().getMovieFile();
		File expectedMovieFile = new File(conf.getCachosDir(), movieName + "-0-1024.part");
		Assert.assertEquals(expectedMovieFile, actualMovieFile);

	}

	public static class MoviePartMetadataCachoMatcher extends BaseMatcher {
		private Matcher<MovieCacho> cachoMatcher;

		public MoviePartMetadataCachoMatcher(Matcher<MovieCacho> cachoMatcher) {
			this.setCachoMatcher(cachoMatcher);
		}

		@Override
		public boolean matches(Object arg0) {
			MovieCacho cacho = ((MoviePartMetadata) arg0).getCacho().getCacho();
			return this.getCachoMatcher().matches(cacho);
		}

		@Override
		public void describeTo(Description arg0) {
			arg0.appendText("MovieCacho produced ").appendDescriptionOf(this.getCachoMatcher());
		}

		public Matcher<MovieCacho> getCachoMatcher() {
			return cachoMatcher;
		}

		public void setCachoMatcher(Matcher<MovieCacho> cachoMatcher) {
			this.cachoMatcher = cachoMatcher;
		}

	}

	public MovieCachoBuilder aCacho() {
		return new MovieCachoBuilder().aCacho();
	}

	public MovieCacho make(MovieCachoBuilder builder) {
		return builder.done();
	}

}
