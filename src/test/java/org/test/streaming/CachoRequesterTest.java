package org.test.streaming;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class CachoRequesterTest {

	@Test
	public void testStream() throws Exception {
		Conf conf = new Conf("/alt-test-conf.properties");
		BufferedOutputStream baos = new BufferedOutputStream(new FileOutputStream(new File("sandonga1.mp4")));
		new DefaultMovieRetrievalPlanInterpreter(conf.getCachosDir(), conf.getTempDir()).interpret(new DummyMovieRetrievalPlan(conf.get("test.video.file.name"), conf), baos);
		baos.flush();
		baos.close();
		File streamedData = new File("sandonga1.mp4");
		Assert.assertTrue(streamedData.exists());
		Assert.assertEquals(streamedData.length(), Integer.parseInt(conf.get("test.video.file.size")));
	}

	@Test
	public void testConcurrentStream() throws Exception {
		final Conf conf = new Conf("/test-conf.properties");
		final Conf altConf = new Conf("/alt-test-conf.properties");
		Runnable runnable1 = new Runnable() {

			@Override
			public void run() {
				try {
					streamTo(conf, "sandonga1.mp4");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Runnable runnable2 = new Runnable() {

			@Override
			public void run() {
				try {
					streamTo(altConf, "sandonga2.mp4");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		Thread thread1 = new Thread(runnable1);
		Thread thread2 = new Thread(runnable2);
		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();

		File streamedData = new File("sandonga1.mp4");
		Assert.assertTrue(streamedData.exists());
		Assert.assertEquals(Integer.parseInt(conf.get("test.video.file.size")), streamedData.length());

		streamedData = new File("sandonga2.mp4");
		Assert.assertTrue(streamedData.exists());
		Assert.assertEquals(Integer.parseInt(conf.get("test.video.file.size")), streamedData.length());
	}

	private void streamTo(Conf conf, String streamedOutputFileName) throws FileNotFoundException, IOException {
		BufferedOutputStream baos = new BufferedOutputStream(new FileOutputStream(new File(streamedOutputFileName)));
		new DefaultMovieRetrievalPlanInterpreter(conf.getCachosDir(), conf.getTempDir()).interpret(new DummyMovieRetrievalPlan(conf.get("test.video.file.name"), conf), baos);
		baos.flush();
		baos.close();
	}
}
