package org.test.streaming;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class CachoRequesterTest {

	/**
	 * Este test asume que el dimon corre con la configuracion por defecto, y
	 * que contiene todos los bytes del archivo determinado por
	 * test.video.file.name en alt-test-conf.properties. Los directorios
	 * determinados por las props, video.dir.temp y video.dir.cachos no deberian
	 * contener archivos que puedan colisionar con los cachos strimiados. Como
	 * resultado, sandonga1.mp4 en el working dir, contiente la pelicula
	 * strimiada.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStream() throws Exception {
		Conf conf = new Conf("/alt-test-conf.properties");
		BufferedOutputStream baos = new BufferedOutputStream(new FileOutputStream(new File("sandonga1.mp4")));
		new DefaultMovieRetrievalPlanInterpreter(conf.getCachosDir(), conf.getTempDir()).interpret(new DummyMovieRetrievalPlan(conf.get("test.video.file.name"), conf), baos, new ProgressLogger());
		baos.flush();
		baos.close();
		File streamedData = new File("sandonga1.mp4");
		Assert.assertTrue(streamedData.exists());
		Assert.assertEquals(streamedData.length(), Integer.parseInt(conf.get("test.video.file.size")));
	}

	/**
	 * Este test prueba que dos usuarios puedan strimiar al mismo tiempo, los
	 * mismos bytes. Para eso asume que el Dimon corre con la configuracion por
	 * defecto, y que contiene todos los bytes del archivo determinado por
	 * test.video.file.name en alt-test-conf.properties. Los 'clientes' se
	 * configuran cada uno con un archivo de configuracion distinto:
	 * alt-test-conf.properties y test-conf.properties. Los directorios
	 * determinados por las props, video.dir.temp y video.dir.cachos no deberian
	 * contener archivos que puedan colisionar con los cachos strimiados y deben
	 * apuntar a directorios diferentes en cada configuracion. Ambos archivos
	 * deben contener los mismos valores en las properties test.video.file.name
	 * y test.video.file.size, para que se strimee el mismo archivo, pero
	 * tambien se puede variar para probar concurrencia de streaming de
	 * diferentes archivos.
	 * 
	 * Como resultado, sandonga1.mp4 en el working dir, contiente la pelicula
	 * strimiada segun test-conf.properties y sandonga2.mp4 la strimiada segun
	 * alt-test-conf.properties.
	 * 
	 * @throws Exception
	 */

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
		new DefaultMovieRetrievalPlanInterpreter(conf.getCachosDir(), conf.getTempDir()).interpret(new DummyMovieRetrievalPlan(conf.get("test.video.file.name"), conf), baos, new ProgressLogger());
		baos.flush();
		baos.close();
	}

}
