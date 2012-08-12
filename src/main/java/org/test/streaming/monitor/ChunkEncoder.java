package org.test.streaming.monitor;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class ChunkEncoder {

	public String encodeChunks(Map<Integer, String> chunksToRegiter) {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<Integer,String> entry : chunksToRegiter.entrySet()){
				sb.append(entry.getKey()+"!"+entry.getValue()+"&");
		}
		if(sb.length()>1)
			sb.replace(sb.length()-1, sb.length(), StringUtils.EMPTY);
		
		return sb.toString();
	}

}
