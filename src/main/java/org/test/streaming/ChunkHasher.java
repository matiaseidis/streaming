package org.test.streaming;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChunkHasher {

	protected static final Log log = LogFactory.getLog(ChunkHasher.class);
	int chunkSize = 1024*1024;



	public String hashCacho(File file, int nextChunkFirstByteIndex, int cachoFirstByteIndex){
		return hash(file, nextChunkFirstByteIndex, cachoFirstByteIndex, false);
	}
	public String hashVideo(File file){
		return hash(file, 0, 0, true);
	}

	private String hash(File file, int nextChunkFirstByteIndex, int cachoFirstByteIndex, boolean fullVideoId) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.reset();

		byte[] bytes = new byte[chunkSize];
		try {
			FileInputStream fis = new FileInputStream(file);
			fis.skip(nextChunkFirstByteIndex - cachoFirstByteIndex);

			while(fis.read(bytes) != -1){
				md.update(bytes);
				if(!fullVideoId){
					break;
				}
			}
			bytes = null;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		byte[] thedigest = md.digest();

		BigInteger bigInt = new BigInteger(1,thedigest);
		String hashtext = bigInt.toString(16);
		// Now we need to zero pad it if you actually want the full 32 chars.
		while(hashtext.length() < 32 ){
			hashtext = "0"+hashtext;
		}
		
		long endByte = 0;
		
		if(fullVideoId){
			endByte = file.length();
		} else {
			endByte = nextChunkFirstByteIndex + chunkSize > file.length() ? file.length() : nextChunkFirstByteIndex + chunkSize - 1;
		}
		log.info("hashed chunk: " +file.getName()+" - "+hashtext+" - from "+nextChunkFirstByteIndex +" to " + endByte);
		return hashtext;
	}

}
