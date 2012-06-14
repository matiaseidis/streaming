package org.test.streaming;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Chasqui implements Index {

	protected static final Log log = LogFactory.getLog(Chasqui.class);
	private static final int INDEXABLE_SIZE = 1024;
	
	@Override
	public void newCachoAvailableLocally(MovieCachoFile movieCachoFile) {
		
		if(movieCachoFile.getCacho().getLength() / INDEXABLE_SIZE == 0){
			log.info("No se indexan cachos mas chicos que " +INDEXABLE_SIZE+" bytes");
			return;
		}
		
		int cachoFrom = movieCachoFile.getCacho().getFirstByteIndex();
		int cachoLenght = movieCachoFile.getCacho().getLength();
		int nextPedazoFrom = cachoFrom % INDEXABLE_SIZE == 0 ? cachoFrom : cachoFrom + INDEXABLE_SIZE;
		int totalPedazos = (cachoLenght - (nextPedazoFrom - cachoFrom)) / INDEXABLE_SIZE;
		
		for(int i = 0; i < totalPedazos; i++){
			String pedazoId = generateId(movieCachoFile.getMovieFile());
			int ordinal = nextPedazoFrom/INDEXABLE_SIZE - 1; // base 0 ???
			String fileName = movieCachoFile.getMovieFile().getName();
			sendToRepository(fileName, pedazoId, ordinal);
			nextPedazoFrom += INDEXABLE_SIZE;
		}
		log.info(totalPedazos+" fragmentos de "+INDEXABLE_SIZE+" reportados");
	}
	

	private void sendToRepository(String fileName, String pedazoId, int pedazoOrdinal){
		System.out.println("Sended by Chasqui to remote repo <fileName: "+fileName+" - pedazo: "+pedazoId+" ["+pedazoOrdinal+"]>");
	}
	
	
	private String generateId(File file) {
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
				while(fis.read(bytes) != -1){
					md.update(bytes);
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
			log.info("hashed video: " +file.getName()+" - "+hashtext);
			return hashtext;
	}

}
