package org.test.streaming;

import java.io.File;

public class MoviePartMetadata {
	private String metadataSeparator = "-";
	private String partFileExtension = "part";
	private String extensionSeparator = ".";

	private MovieCachoFile cacho;

	public MoviePartMetadata(File dir, String fileName, int firstByte, int length) {
		this.setCacho(new MovieCachoFile(new MovieCacho(firstByte, length), new File(dir, fileName + this.getMetadataSeparator() + firstByte + this.getMetadataSeparator() + length + this.getExtensionSeparator() + this.getPartFileExtension())));
	}

	public MoviePartMetadata(File file) {
		String movieFileName = file.getName();
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
		if (metadataAtts.length < 2) {
			throw new InvalidMovieMetadataException("Failed to parse metadata from file name " + movieFileName + " (expected 2 fields, found " + metadataAtts.length + " )");
		}

		try {
			int firstByte = Integer.parseInt(metadataAtts[metadataAtts.length - 2]);
			int length = Integer.parseInt(metadataAtts[metadataAtts.length - 1]);
			this.setCacho(new MovieCachoFile(new MovieCacho(firstByte, length), file));
		} catch (NumberFormatException e) {
			throw new InvalidMovieMetadataException("Failed to parse metadata from file name " + movieFileName + " (expected 2 int filed, found " + metadataAtts[0] + " and " + metadataAtts[1] + " )");
		}

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

	public MovieCachoFile getCacho() {
		return cacho;
	}

	public void setCacho(MovieCachoFile cacho) {
		this.cacho = cacho;
	}

}
