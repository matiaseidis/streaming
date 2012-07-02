package org.test.streaming;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.junit.Test;

public class CachoRequesterTest {

	@Test
	public void testCachoRequester() throws Exception {
		Conf conf = new Conf("/test-conf.properties");
		BufferedOutputStream baos = new BufferedOutputStream(
				new FileOutputStream(new File("sandonga1.mp4")));
		new DefaultMovieRetrievalPlanInterpreter(conf.getCachosDir(),
				conf.getTempDir()).interpret(
				new DummyMovieRetrievalPlan(conf.get("test.video.file.name"),
						conf), baos);
		baos.flush();
		baos.close();
	}

}
