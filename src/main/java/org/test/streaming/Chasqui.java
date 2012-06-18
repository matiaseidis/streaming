package org.test.streaming;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.monitor.Notifier;

public class Chasqui implements Index {

	protected static final Log log = LogFactory.getLog(Chasqui.class);
	private static final int INDEXABLE_SIZE = 1024 * 1024;
	private Notifier notifier = Notifier.getInstance();
	
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
		String fileName = movieCachoFile.getMovieFile().getName();
		
		/*
		 * partes de 1mb en la que indexo este cacho
		 */
		Map<Integer, String> pedazos = new HashMap<Integer, String>();

		for(int i = 0; i < totalPedazos; i++){
			
			String chunkId = generateId(movieCachoFile.getMovieFile(), nextPedazoFrom, movieCachoFile.getCacho().getFirstByteIndex(), false);
			int ordinal = nextPedazoFrom/INDEXABLE_SIZE; // base 0 ???
			pedazos.put(ordinal, chunkId);
			nextPedazoFrom += INDEXABLE_SIZE;
		}
		
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<Integer,String> entry : pedazos.entrySet()){
			sb.append(entry.getKey()+"!"+entry.getValue()+"&");
		}
		sb.replace(sb.length()-1, sb.length(), StringUtils.EMPTY);
		sendToRepository(fileName, sb.toString());
		log.info(totalPedazos+" fragmentos de "+INDEXABLE_SIZE+" reportados");
	}
	

	private void sendToRepository(String fileName, String chunks){

		notifier.registerParts(fileName, chunks);
		
		System.out.println("Sended by Chasqui to remote repo <fileName: "+fileName+" - chunks: "+chunks+"]>");
	}
	
	
	private String generateId(File file, int chunkFrom, int cachoFrom, boolean fullVideoId) {
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
				fis.skip(chunkFrom - cachoFrom);
				
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
			log.info("hashed chunk: " +file.getName()+" - "+hashtext+" - from "+chunkFrom);
			return hashtext;
	}
	
	public static void main(String[] args) {
		new Chasqui().registerVideo();
	}


	private void registerVideo() {
		File file = new File(Conf.VIDEO_DIR+Conf.VIDEO);
		String hash = this.generateId(file, 0, 0, true);
		
		int cachoFrom = 0;
		int nextPedazoFrom = cachoFrom % INDEXABLE_SIZE == 0 ? cachoFrom : cachoFrom + INDEXABLE_SIZE;
		int totalPedazos = (Conf.VIDEO_SIZE - (nextPedazoFrom - cachoFrom)) / INDEXABLE_SIZE;
		
		/*
		 * partes de 1mb en la que indexo este cacho
		 */
		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < totalPedazos; i++){
			
			String chunkId = generateId(file, nextPedazoFrom, 0, false);
			log.info("oridinal: " + i + "id: "+ chunkId);
			sb.append(chunkId+"!");
			nextPedazoFrom += INDEXABLE_SIZE;
		}
		
		sb.replace(sb.length()-1, sb.length(), StringUtils.EMPTY);
		
		notifier.registerVideo(hash, Conf.VIDEO, Conf.VIDEO_SIZE, sb.toString());
		log.info("Hashed full video: "+hash+" - "+Conf.VIDEO+" - "+ Conf.VIDEO_SIZE+" - "+ sb.toString());
		
	}

}
