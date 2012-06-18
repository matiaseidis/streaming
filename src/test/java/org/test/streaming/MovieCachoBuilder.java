package org.test.streaming;

public class MovieCachoBuilder {

	private MovieCacho cachoUnderConstruction;

	public MovieCachoBuilder() {
	}

	public MovieCachoBuilder aCacho() {
		this.setCachoUnderConstruction(new MovieCacho());
		return this;
	}

	public MovieCachoBuilder startingFrom(int startingFrom) {
		this.getCachoUnderConstruction().setFirstByteIndex(startingFrom);
		return this;
	}

	public MovieCachoBuilder withLenght(int length) {
		this.getCachoUnderConstruction().setLength(length);
		return this;
	}

	public MovieCacho done() {
		return this.getCachoUnderConstruction();
	}

	public MovieCacho getCachoUnderConstruction() {
		return cachoUnderConstruction;
	}

	public void setCachoUnderConstruction(MovieCacho cachoUnderConstruction) {
		this.cachoUnderConstruction = cachoUnderConstruction;
	}

}
