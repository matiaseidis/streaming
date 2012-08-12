package org.test.streaming;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class MovieCachoHasherTest {

	protected static final Log log = LogFactory.getLog(MovieCachoHasherTest.class);

	@Test
	public void testChunkLimitsAreCorrect() {
		
		
		int chunkSize = 1024*1024;
		
		Conf conf = new Conf("/alt-test-conf.properties");
		File file = new File(conf.getCachosDir(), conf.get("test.video.file.name"));
		
		log.info("File size: "+file.length());
		
		Assert.assertTrue("file: "+file.getAbsolutePath()+" does not exist", file.exists());
		Assert.assertTrue("file: "+file.getAbsolutePath()+" should be bigger than 1MB",file.length()>chunkSize);
		Assert.assertTrue("file: "+file.getAbsolutePath()+" should not be (multiplo) of chunkSize("+chunkSize+")",file.length() % chunkSize != 0);
		
		MovieCacho movieCacho = new MovieCacho(0, (int)file.length());
		MovieCachoFile movieCachoFile = new MovieCachoFile(movieCacho, file); 
		MovieCachoHasher mch = new MovieCachoHasher();
		Map<Integer, String> hashes = mch.hashMovieCachoFile(movieCachoFile, chunkSize);
		
		long expectedChunks = file.length() % chunkSize == 0 ? file.length() / chunkSize : ((file.length() / chunkSize) + 1); 
		Assert.assertEquals(expectedChunks, hashes.size());
		
		for(Map.Entry<Integer, String> hashEntry: hashes.entrySet()) {
			String hashedFromFile = hashes.get(hashEntry.getKey());
			String hashedFromChunk = new ChunkHasher().hashCacho(file, hashEntry.getKey()*chunkSize, 0); 
			Assert.assertEquals(hashedFromFile+" should be equal to "+hashedFromChunk,hashedFromFile, hashedFromChunk);	
		}
		
		
	}

}
