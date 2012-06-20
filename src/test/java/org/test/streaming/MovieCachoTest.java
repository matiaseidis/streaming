package org.test.streaming;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;

public class MovieCachoTest {
	private Random random = new Random(System.currentTimeMillis());

	private int aNegativeInt() {
		return -1 * aPositiveNonZeroInt();
	}

	private int aPositiveNonZeroInt() {
		return aPositiveOrZeroInt() + 1;
	}

	private int aPositiveOrZeroInt() {
		return Math.abs(this.getRandom().nextInt());
	}

	public static class MovieCachoInstantiationMatcher extends BaseMatcher<int[]> {

		@Override
		public boolean matches(Object arg0) {
			int[] args = (int[]) arg0;
			try {
				new MovieCacho(args[0], args[1]);
				return false;
			} catch (IllegalArgumentException e) {
				return true;
			}

		}

		@Override
		public void describeTo(Description arg0) {
			arg0.appendText(" must throw IllegalArgumentException");
		}

	}

	public static class MovieCachoContructorArgsBuilder extends MovieCacho {
		public MovieCachoContructorArgsBuilder withFirstByteIndex(int firstByteIndex) {
			this.setFirstByteIndex(firstByteIndex);
			return this;
		}

		public MovieCachoContructorArgsBuilder andLength(int length) {
			this.setLength(length);
			return this;
		}

		public int[] done() {
			return new int[] { this.getFirstByteIndex(), this.getLength() };
		}
	}

	public MovieCachoContructorArgsBuilder aMovieCacho() {
		return new MovieCachoContructorArgsBuilder();
	}

	public int[] instantiationOf(MovieCachoContructorArgsBuilder b) {
		return b.done();
	}

	private MovieCachoInstantiationMatcher throwsIllegalArgumentException() {
		return new MovieCachoInstantiationMatcher();
	}

	@Test
	public void testThatInstantiationWithZeroLengthThrowsIAE() throws Exception {
		assertThat(instantiationOf(aMovieCacho().withFirstByteIndex(0).andLength(0)), throwsIllegalArgumentException());
	}

	@Test
	public void testThatInstantiationWithNegativeLengthThrowsIAE() throws Exception {
		assertThat(instantiationOf(aMovieCacho().withFirstByteIndex(0).andLength(aNegativeInt())), throwsIllegalArgumentException());
	}

	@Test
	public void testThatInstantiationWithPositiveNonzeroLengthDoesntThrowIAE() throws Exception {
		assertThat(instantiationOf(aMovieCacho().withFirstByteIndex(0).andLength(aPositiveNonZeroInt())), not(throwsIllegalArgumentException()));
	}

	@Test
	public void testThatInstantiationWithNegativFirstByteIndexThrowsIAE() throws Exception {
		assertThat(instantiationOf(aMovieCacho().withFirstByteIndex(aNegativeInt()).andLength(aPositiveNonZeroInt())), throwsIllegalArgumentException());
	}

	@Test
	public void testThatInstantiationWithPositiveOrZeroFirstByteIndexDoesntThrowIAE() throws Exception {
		assertThat(instantiationOf(aMovieCacho().withFirstByteIndex(aPositiveOrZeroInt()).andLength(aPositiveNonZeroInt())), not(throwsIllegalArgumentException()));
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}
}
