package org.test.streaming;

import java.util.Properties;

import javax.el.PropertyNotFoundException;

public class Conf {
	
	private static String tempDir = "video.dir.temp";
	private static String cachosDir = "video.dir.cachos";
	private static String sharedDir = "video.dir.compartido";
	private static String notifierUrl = "notifier.url.base";
	private static String monitorInterval = "monitor.interval";
	private static String daemonHost = "dimon.host";
	private static String daemonPort = "dimon.port";
	
	
	private static Properties properties;

	public static String getTempDir() {
		return get(tempDir);
	}

	public static String getCachosDir() {
		return get(cachosDir);
	}

	public static String getSharedDir() {
		return get(sharedDir);
	}

	public static String getNotifierUrl() {
		return get(notifierUrl);
	}

	public static String getMonitorInterval() {
		return get(monitorInterval);
	}


	public static void setProperties(Properties properties){
		Conf.properties = properties;
	}
	
	public static String get(String propertyKey){
		String result = properties.getProperty(propertyKey);
		if(result == null) {
			throw new PropertyNotFoundException(propertyKey);
		}
		return result;
	}

	public static String getDaemonHost() {
		return get(daemonHost);
	}

	public static int getDaemonPort() {
		return Integer.valueOf(get(daemonPort));
	}

//	// public static final String VIDEO_DIR = "/home/matias/cachos/files/";
//	// public static final String VIDEO_DIR = "/home/meidis/Videos/";
//	public static final String VIDEO_DIR = "C:\\cachos";
//
//	// public static final String VIDEO =
//	// "Alcatraz.S01E11.Webb.Porter.HDTV.x264-LOL.mp4";
//	// public static final int VIDEO_SIZE = 248079992;
//
//	public static final String VIDEO = "Luther.S02E01.720p.HDTV.x264-3.mp4";
//	public static final int VIDEO_SIZE = 421732944;
//
//	// public static final String VIDEO = "Pixar_-_Boundin_(Short_Film).avi";
//	// public static final int VIDEO_SIZE = 31127552 ;
//
//	// public static final String VIDEO =
//	// "Game.of.Thrones.S02E01.HDTV.RM-ASAP.mp4";
//	// public static final int VIDEO_SIZE = 393031408;
//
//	public static final String DIMON_HOST = "localhost";
//	// public static final String DIMON_HOST = "cronopio.dyndns.org";
//	public static final int DIMON_PORT = 10002;
//
//	public static final String USER_ID = "demo.user.0";
//
//	// public static final String NOTIFIER_URL_BASE =
//	// "http://indice.herokuapp.com/";
//	public static final String NOTIFIER_URL_BASE = "http://localhost:8090/ce/";
//	public static final long MONITOR_INTERVAL = 3000;
//
//	public static final String PREVALENCE_DIR = "streaming-demo/prevalence";
//	// public static final String PREVALENCE_DIR =
//	// "/home/matias/servle.prevalence";
//	public static final String SUBS = "Luther-02x01-SP.srt";
//
//	public static final String VIDEO_ID_FOR_TESTING = "d1a27cdb480dbd3f9d1adbca766cb44e98fd6db8"; // TODO
//																									// sacar
//																									// del
//																									// param
//																									// del
//																									// request

}
