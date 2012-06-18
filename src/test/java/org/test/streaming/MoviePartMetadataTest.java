package org.test.streaming;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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
		return new MoviePartMetadata(movieFileName);
	}

	public MoviePartMetadataCachoMatcher hasMovieCacho(Matcher<MovieCacho> cachoMatcher) {
		return new MoviePartMetadataCachoMatcher(cachoMatcher);
	}

	public static class MoviePartMetadataCachoMatcher extends BaseMatcher {
		private Matcher<MovieCacho> cachoMatcher;

		public MoviePartMetadataCachoMatcher(Matcher<MovieCacho> cachoMatcher) {
			this.setCachoMatcher(cachoMatcher);
		}

		@Override
		public boolean matches(Object arg0) {
			MovieCacho cacho = ((MoviePartMetadata) arg0).getCacho();
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
