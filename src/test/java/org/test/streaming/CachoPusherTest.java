package org.test.streaming;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import junit.framework.Assert;

import org.junit.Test;

public class CachoPusherTest {

	/**
	 * Asume que el dimon esta ejecutando con la configuracion por default y que
	 * el directorio determinado por la property video.dir.cachos en
	 * alt-test-conf.properties contiene la pelicula determinada por
	 * test.video.file.name del mismo archivo. Pushea la pelicula de a cachos de
	 * 64M, y luego la stremea. El archivo ssandonga1.mp4 en el working dir
	 * contiene la pelicula original, luego de ser pusheada y strimiada.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPush() throws Exception {

		Conf conf = new Conf("/alt-test-conf.properties");
		new DefaultMovieSharingPlanInterpreter(conf).interpret(new DummyMovieRetrievalPlan("videoId", conf));
		Thread.sleep(10000);
		BufferedOutputStream baos = new BufferedOutputStream(new FileOutputStream(new File("sandonga1.mp4")));
		new DefaultMovieRetrievalPlanInterpreter(conf.getCachosDir(), conf.getTempDir()).interpret(new DummyMovieRetrievalPlan(conf.get("test.video.file.name"), conf), baos);
		baos.flush();
		baos.close();

		File streamedData = new File("sandonga1.mp4");
		Assert.assertTrue(streamedData.exists());
		Assert.assertEquals(Integer.parseInt(conf.get("test.video.file.size")), streamedData.length());
	}
}
