package org.test.streaming.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.Conf;
import org.test.streaming.encoding.H264Encoder;

public class SharedVideosMonitor extends FileAlterationListenerAdaptor {

	protected static final Log log = LogFactory
			.getLog(SharedVideosMonitor.class);

	// private BaseModel baseModel;

	private final ExecutorService pool = Executors.newFixedThreadPool(20);

	private String userId;

	private File monitoredDir;
	private long monitorInterval;

	private Conf conf;

	private FileAlterationObserver observer;
	private FileAlterationMonitor monitor;

	public SharedVideosMonitor(Conf conf) {

		monitoredDir = conf.getSharedDir();
		monitorInterval = conf.getMonitorInterval();
		observer = new FileAlterationObserver(monitoredDir);
		monitor = new FileAlterationMonitor(monitorInterval);
		this.conf = conf;
		/*
		 * TODO pedirle el user al dimon
		 */
		userId = conf.get("test.user.id");
	}

	public void begin() throws Exception {

		observer.addListener(this);
		monitor.addObserver(observer);
		monitor.start();
		log.debug("SharedVideosListener.begin()");
	}

	public void end() {
		try {
			monitor.stop();
		} catch (Exception e) {
			log.error("Unable to stop the monitor", e);
		}
	}

	@Override
	public void onFileCreate(final File newFile) {

		/*
		 * por ahora, solo soporte para mp4 y parts
		 */
		// if ( !newFile.getName().endsWith(".mp4")/* &&
		// !newFile.getName().endsWith(".part")*/){
		// return;
		// }

		if (newFile.getAbsolutePath().contains("_TEMP_ENCODING_")) {
			return;
		}

		checkCompletness(newFile);

		pool.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {

				File file = newFile;
				File dest = null;

				if (file.getName().contains(" ")) {
					dest = new File(org.apache.commons.lang.StringUtils
							.replace(file.getAbsolutePath(), " ", "_"));
					if (!file.renameTo(dest)) {
						throw new RuntimeException("no puedo renombrar");
					}
					file = dest;
				}
				H264Encoder encoder = new H264Encoder(file.getName(), conf
						.getSharedDir(), conf.getCachosDir());
				File readyToShareVideo = encoder.encode();

				return new VideoRegistration(readyToShareVideo, conf).go();
			}
		});
	}

	private void checkCompletness(File newFile) {
		long oldSize = 0L;
		long newSize = 1L;
		boolean fileIsOpen = true;

		while ((newSize > oldSize) || fileIsOpen) {
			oldSize = newFile.length();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			newSize = newFile.length();

			try {
				new FileInputStream(newFile);
				fileIsOpen = false;
			} catch (Exception e) {
			}
		}
		log.info("New file complete: " + newFile.toString());
	}

	@Override
	public void onFileDelete(final File file) {
		// TODO ver como hacer en caso de borrado de file
		// de donde saco el ID?

		// log.info("video borrado <"+file.getName()+">, actualizamos repo");
		//
		// pool.submit(new Callable<String>(){
		//
		// String result = null;
		// String videoId = null;
		// // Cacho cacho = null;
		//
		// @Override
		// public String call() throws Exception {
		//
		// try {
		// LocalTracking tracking = getBaseModel().getModel();
		// videoId = tracking.getHashByVideoFileName(file.getName());
		// // cacho = tracking.getCacho()
		// } catch (Exception e){
		// log.error("Video id not found in local repo for file " +
		// file.getName());
		// return null;
		// }
		//
		// String [] meta = meta(file);
		// result = notifier.removeCacho(USER_ID, videoId,
		// Long.valueOf(meta[0]), Long.valueOf(meta[1]));
		//
		// return result;
		// }
		// });
	}

	// private String[] meta(File file) {
	// //a.mp4-1048576-1048576.part
	// String fileName = file.getName();
	// String[] meta = fileName.split("\\.");
	// Assert.isTrue(meta.length == 3);
	// String[] splittedMeta = meta[1].split("-");
	// Assert.isTrue(splittedMeta.length == 3);
	//
	// return new String[]{splittedMeta[1], splittedMeta[2]};
	// }
	public void onDirectoryDelete(final File directory) {

		// pool.submit(new Callable<String>(){
		//
		// @Override
		// public String call() throws Exception {
		//
		// log.info("El directorio compartido se borro, eliminamos todo del repo");
		// String result = null;
		// for (File file : directory.listFiles()){
		//
		// String videoId =
		// getBaseModel().getModel().getHashByVideoFileName(file.getName());
		//
		// String [] meta = meta(file);
		// result = notifier.removeCacho(userId, videoId, Long.valueOf(meta[0]),
		// Long.valueOf(meta[1]));
		//
		// }
		// return result;
		// }
		// });
	}

}
