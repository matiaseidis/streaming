package org.test.streaming.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.Chasqui;
import org.test.streaming.Conf;

public class PropertiesLoader implements ServletContextListener{
	
	protected static final Log log = LogFactory.getLog(Chasqui.class);
	private String confPath = "/conf.properties";
	
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		Properties props = new Properties();
        try {
        	InputStream is = this.getClass().getResourceAsStream(confPath);
			props.load(is);
		} catch (IOException e) {
			e.printStackTrace();
			log.fatal("Unable to load configuration values form "+confPath+" - context shutdown.");
			System.exit(1);
		}
        Conf.setProperties(props);
        
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
