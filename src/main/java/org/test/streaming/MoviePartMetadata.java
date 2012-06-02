package org.test.streaming;

public class MoviePartMetadata {
	private String metadataSeparator = "-";
	private String partFileExtension = "part";
	private String extensionSeparator = ".";

	private MovieCacho cacho;

	public MoviePartMetadata(String movieFileName) {
		int beginIndex = movieFileName.indexOf(this.getMetadataSeparator()) + 1;
		if (beginIndex == -1) {
			throw new InvalidMovieMetadataException("Failed to parse metadata from file name " + movieFileName + "  (first metadata separator not found)");
		}
		int endIndex = movieFileName.lastIndexOf(this.getExtensionSeparator() + this.getPartFileExtension());
		if (endIndex == -1) {
			throw new InvalidMovieMetadataException("Failed to parse metadata from file name " + movieFileName + " (invalid extension or no extension found)");
		}
		String metadata = null;
		try {
			metadata = movieFileName.substring(beginIndex, endIndex);
		} catch (IndexOutOfBoundsException e) {
			throw new InvalidMovieMetadataException("Failed to parse metadata from file name " + movieFileName + " (invalid metada substring)");
		}

		String[] metadataAtts = metadata.split(this.getMetadataSeparator());
		if (metadataAtts.length != 2) {
			throw new InvalidMovieMetadataException("Failed to parse metadata from file name " + movieFileName + " (expected 2 fields, found " + metadataAtts.length + " )");
		}

		try {
			int firstByte = Integer.parseInt(metadataAtts[0]);
			int length = Integer.parseInt(metadataAtts[1]);
			this.setCacho(new MovieCacho(firstByte, length));
		} catch (NumberFormatException e) {
			throw new InvalidMovieMetadataException("Failed to parse metadata from file name " + movieFileName + " (expected 2 int filed, found " + metadataAtts[0] + " and " + metadataAtts[1] + " )");
		}

	}

	public MovieCacho getCacho() {
		return cacho;
	}

	public void setCacho(MovieCacho cacho) {
		this.cacho = cacho;
	}

	public String getMetadataSeparator() {
		return metadataSeparator;
	}

	public void setMetadataSeparator(String metadataSeparator) {
		this.metadataSeparator = metadataSeparator;
	}

	public String getPartFileExtension() {
		return partFileExtension;
	}

	public void setPartFileExtension(String partFileExtension) {
		this.partFileExtension = partFileExtension;
	}

	public String getExtensionSeparator() {
		return extensionSeparator;
	}

	public void setExtensionSeparator(String extensionSeparator) {
		this.extensionSeparator = extensionSeparator;
	}

}
