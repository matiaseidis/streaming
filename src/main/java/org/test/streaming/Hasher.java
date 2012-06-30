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

public class Hasher {

	protected static final Log log = LogFactory.getLog(Hasher.class);



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

		byte[] bytes = new byte[1024];
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
		log.info("hashed chunk: " +file.getName()+" - "+hashtext+" - from "+nextChunkFirstByteIndex);
		return hashtext;
	}
	
	public String encodeChunksForRegistration(File file, int totalChunks,
			int nextChunkFirstByteIndex, int indexableSize) {
		/*
		 * partes de tamaño <indexableSize> bytes en la que indexo este cacho
		 */
		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < totalChunks; i++){

			String chunkId = this.hashCacho(file, nextChunkFirstByteIndex, 0); 

			log.info("oridinal: " + i + "id: "+ chunkId);
			sb.append(chunkId+"!");
			nextChunkFirstByteIndex += indexableSize;
		}

		sb.replace(sb.length()-1, sb.length(), StringUtils.EMPTY);
		return sb.toString();
	}
}
