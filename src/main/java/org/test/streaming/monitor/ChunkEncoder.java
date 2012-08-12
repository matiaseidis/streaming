package org.test.streaming.monitor;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChunkEncoder {
	
	protected static final Log log = LogFactory.getLog(ChunkEncoder.class);

	public String encodeCacho(Map<Integer, String> chunksToRegiter) {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<Integer,String> entry : chunksToRegiter.entrySet()){
				sb.append(entry.getKey()+"!"+entry.getValue()+"&");
		}
		if(sb.length()>1)
			sb.replace(sb.length()-1, sb.length(), StringUtils.EMPTY);
		
		return sb.toString();
	}
	
	private String encodeVideo(Map<Integer, String> chunks) {
		/*
		 * partes de tamaño <indexableSize> bytes en la que indexo este cacho
		 */
		StringBuilder sb = new StringBuilder();

		for(Map.Entry<Integer, String> chunkEntry : chunks.entrySet() ) {
			
			log.info("oridinal: " + chunkEntry.getKey() + "id: "+ chunkEntry.getValue());
			sb.append(chunkEntry.getValue()+"!");
		}
		
		sb.replace(sb.length()-1, sb.length(), StringUtils.EMPTY);
		return sb.toString();
	}

}
