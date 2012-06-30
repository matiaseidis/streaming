package org.test.streaming;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.el.PropertyNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Conf {
	
	protected static final Log log = LogFactory.getLog(Conf.class);
	private String confPath = "/conf.properties";
	
	private String tempDir = "video.dir.temp";
	private String cachosDir = "video.dir.cachos";
	private String sharedDir = "video.dir.compartido";
	private String notifierUrl = "notifier.url.base";
	private String monitorInterval = "monitor.interval";
	private String daemonHost = "dimon.host";
	private String daemonPort = "dimon.port";
	
	private Properties properties;
	
	public Conf(){
			
			Properties props = new Properties();
	        try {
	        	InputStream is = this.getClass().getResourceAsStream(confPath);
				props.load(is);
			} catch (IOException e) {
				e.printStackTrace();
				log.fatal("Unable to load configuration values form "+confPath+" - context shutdown.");
				System.exit(1);
			}
	        this.properties = props;
	        
	        new File(this.getCachosDir()).mkdirs();
	        new File(this.getSharedDir()).mkdirs();
	        new File(this.getTempDir()).mkdirs();
	}
	
	public String getTempDir() {
		return get(tempDir);
	}

	public String getCachosDir() {
		return get(cachosDir);
	}

	public String getSharedDir() {
		return get(sharedDir);
	}

	public String getNotifierUrl() {
		return get(notifierUrl);
	}

	public long getMonitorInterval() {
		return Long.parseLong(get(monitorInterval));
	}

	public String get(String propertyKey){
		String result = properties.getProperty(propertyKey);
		if(result == null) {
			throw new PropertyNotFoundException(propertyKey);
		}
		return result;
	}

	public String getDaemonHost() {
		return get(daemonHost);
	}

	public int getDaemonPort() {
		return Integer.valueOf(get(daemonPort));
	}

	public int getIndexableSize() {
		return 1024*1024;
	}
}
