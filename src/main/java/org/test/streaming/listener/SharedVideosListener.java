package org.test.streaming.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.Conf;
import org.test.streaming.Dimon;
import org.test.streaming.monitor.SharedVideosMonitor;

public class SharedVideosListener implements ServletContextListener {
	
	protected static final Log log = LogFactory.getLog(SharedVideosListener.class);
	private String key = "sharedVideosMonitor";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Conf conf = new Conf();
		SharedVideosMonitor sharedVideoMonitor = new SharedVideosMonitor(conf);
		
		try {
			sharedVideoMonitor.begin();
		} catch (Exception e) {
			log.fatal("Unable to start minitor for shared videos", e);
			System.exit(1);
		}
		sce.getServletContext().setAttribute(key, sharedVideoMonitor);
		
		/*
		 * start dimon
		 */
		new Dimon(conf.getDaemonPort()).run();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		SharedVideosMonitor sharedVideoMonitor = (SharedVideosMonitor)sce.getServletContext().getAttribute(key);
		sharedVideoMonitor.end();
	}

}
