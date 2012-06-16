package org.test.streaming.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.test.streaming.Conf;
import org.test.streaming.prevalence.BaseModel;
import org.test.streaming.prevalence.LocalTracking;
import org.test.streaming.prevalence.transaction.AddCachoToLocalDB;
import org.test.streaming.prevalence.transaction.AddVideoToLocalDB;

public class SharedVideosListener {
//	implements FileAlterationListener {

//	public Logger logger = Logger.getLogger(getClass());
//	
//	private Notifier notifier = new Notifier();
//	private final BaseModel baseModel;
//	
//	private final ExecutorService pool = Executors.newFixedThreadPool(20);
//
//	/*
//	 * TODO pedirle el user al dimon 
//	 */
//	private static final String USER_ID = Conf.USER_ID;
//
//	private static final String IP = Conf.DIMON_HOST;
//	private static final int PORT = Conf.DIMON_PORT;
//	private static final String MONITORED_DIR = Conf.VIDEO_DIR;
//	private static final long INTERVAL = Conf.MONITOR_INTERVAL;
//
//	FileAlterationObserver observer = new FileAlterationObserver(MONITORED_DIR);
//	FileAlterationMonitor monitor = new FileAlterationMonitor(INTERVAL);
//	
//	public SharedVideosListener(BaseModel baseModel){
//		this.baseModel = baseModel;
//	}
//	
//
//	public void begin() throws Exception {
//		
//		observer.addListener(this);
//		monitor.addObserver(observer);
//		monitor.start();
//		
//		System.out.println("SharedVideosListener.begin()");
//	}
//
//	public void onDirectoryDelete(final File directory) {
//		
//		pool.submit(new Callable<String>(){
//
//			@Override
//			public String call() throws Exception {
//
//				logger.info("El directorio compartido se borro, eliminamos todo del repo");
//				String result = null;
//				for (File file : directory.listFiles()){
//					
//					String videoId = getBaseModel().getModel().getHashByVideoFileName(file.getName());
//					
//					String [] meta = meta(file);
//					result = notifier.removeCacho(USER_ID, videoId, Long.valueOf(meta[0]), Long.valueOf(meta[1]));
//
//				}
//				return result;
//			}
//		});
//	}
//
//	@Override
//	public void onFileCreate(final File newFile) {
//
//		/*
//		 * por ahora, solo soporte para mp4 y parts
//		 */
//		if ( !newFile.getName().endsWith(".mp4") && !newFile.getName().endsWith(".part")){
//			return;
//		}
//		
//		checkCompletness(newFile);
//		
//		pool.submit(new Callable<String>(){
//
//			String result = null;
//			
//			@Override
//			public String call() throws Exception {
//
//				File file = newFile;
//				File dest = null;
//
//				if(file.getName().contains(" ")) {
//					dest = new File(org.apache.commons.lang.StringUtils.replace(file.getAbsolutePath(), " ", "_"));
//					if ( !file.renameTo(dest) ){
//						throw new RuntimeException("no puedo renombrar");
//					}
//					file= dest;
//				}
//
//				String videoId = md5(file);
//
//				if(isPart(file)) {
//
//					String [] meta = meta(file);
//					long start  = Long.valueOf(meta[0]);
//					long lenght = Long.valueOf(meta[1]);
//
//					if( isComplete(file, lenght) ){
//						result = notifier.addCacho(USER_ID, IP, PORT, videoId, file.getName(), start, lenght);
//						getBaseModel().getPrevayler().execute(new AddVideoToLocalDB(videoId, file.getName(), lenght));
//						getBaseModel().getPrevayler().execute(new AddCachoToLocalDB(videoId, start, lenght));
//					}
//				} else {
//					notifier.addCacho(USER_ID, IP, PORT, videoId, file.getName(), 1, file.length());
//					getBaseModel().getPrevayler().execute(new AddVideoToLocalDB(videoId, file.getName(), file.length()));
//				}
//				
//				return result;
//
//			}
//		});
//	}
//
//	private void checkCompletness(File newFile) {
//		long oldSize = 0L;
//	      long newSize = 1L;
//	      boolean fileIsOpen = true;
//
//	      while((newSize > oldSize) || fileIsOpen){
//	          oldSize = newFile.length();
//	          try {
//	            Thread.sleep(2000);
//	          } catch (InterruptedException e) {
//	            e.printStackTrace();
//	          }
//	          newSize = newFile.length();
//
//	          try{
//	              new FileInputStream(newFile);
//	              fileIsOpen = false;
//	          }catch(Exception e){}
//	      }
//
//	      logger.info("New file complete: " + newFile.toString());
//
//	}
//
//	@Override
//	public void onFileDelete(final File file) {
//
//		logger.info("video borrado <"+file.getName()+">, actualizamos repo");
//
//		pool.submit(new Callable<String>(){
//
//			String result = null;
//			String videoId = null;
////			Cacho cacho = null;
//			
//			@Override
//			public String call() throws Exception {
//				
//				try {
//					LocalTracking tracking = getBaseModel().getModel();
//					videoId = tracking.getHashByVideoFileName(file.getName());
////					cacho = tracking.getCacho()
//				} catch (Exception e){
//					logger.error("Video id not found in local repo for file " + file.getName());
//					return null;
//				}
//
//				String [] meta = meta(file);
//				result = notifier.removeCacho(USER_ID, videoId, Long.valueOf(meta[0]), Long.valueOf(meta[1]));
//
//				return result;
//			}
//		});
//	}
//
//	private boolean isComplete(File file, long lenght) {
//		return file.length() == lenght;
//	}
//
//	private String[] meta(File file) {
//		//a.mp4-1048576-1048576.part
//		String fileName = file.getName(); 
//		String[] meta = fileName.split("\\.");
//		Assert.isTrue(meta.length == 3);
//		String[] splittedMeta = meta[1].split("-");
//		Assert.isTrue(splittedMeta.length == 3);
//
//		return new String[]{splittedMeta[1], splittedMeta[2]};
//	}
//
//	private boolean isPart(File file) {
//		return file.getName().endsWith(".part");
//	}
//
//	private String md5(File file) {
//		MessageDigest md = null;
//		try {
//			md = MessageDigest.getInstance("MD5");
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//		md.reset();
//
//		byte[] bytes = new byte[1024];
//		
//		try {
//			FileInputStream fis = new FileInputStream(file);
//			while(fis.read(bytes) != -1){
//				md.update(bytes);
//			}
//			bytes = null;
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//
//		byte[] thedigest = md.digest();
//		
//		BigInteger bigInt = new BigInteger(1,thedigest);
//		String hashtext = bigInt.toString(16);
//		// Now we need to zero pad it if you actually want the full 32 chars.
//		while(hashtext.length() < 32 ){
//			hashtext = "0"+hashtext;
//		}
//		logger.info("hashed video: " +file.getName()+" - "+hashtext);
//		return hashtext;
//	}
//
//
//	@Override
//	public void onStart(FileAlterationObserver observer) {
////		System.out
////		.println("FSM.begin().new FileAlterationListener() {...}.onStart()");
//
//	}
//
//	@Override
//	public void onDirectoryCreate(File directory) {
//		System.out
//		.println("FSM.begin().new FileAlterationListener() {...}.onDirectoryCreate()");
//
//	}
//
//	@Override
//	public void onDirectoryChange(File directory) {
//		System.out
//		.println("FSM.begin().new FileAlterationListener() {...}.onDirectoryChange()");			
//	}
//
//	@Override
//	public void onFileChange(File file) {
//		System.out
//		.println("FSM.begin().new FileAlterationListener() {...}.onFileChange()");
//
//	}
//
//	@Override
//	public void onStop(FileAlterationObserver observer) {
////		System.out.println("FSM.begin().new FileAlterationListener() {...}.onStop()");
//	}
//
//
//	public BaseModel getBaseModel() {
//		return baseModel;
//	}

}
