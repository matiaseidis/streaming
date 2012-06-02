package org.test.streaming;

public class InvalidMovieMetadataException extends RuntimeException {

	public InvalidMovieMetadataException() {
		super();
	}

	public InvalidMovieMetadataException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidMovieMetadataException(String message) {
		super(message);
	}

	public InvalidMovieMetadataException(Throwable cause) {
		super(cause);
	}

}
