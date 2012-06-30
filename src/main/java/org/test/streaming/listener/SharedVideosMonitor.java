package org.test.streaming.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.Conf;
import org.test.streaming.monitor.SharedVideosListener;

public class SharedVideosMonitor implements ServletContextListener {
	
	protected static final Log log = LogFactory.getLog(SharedVideosMonitor.class);
	private String key = "sharedVideoListener";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Conf conf = new Conf();
		SharedVideosListener sharedVideoListener = new SharedVideosListener(conf);
		
		try {
			sharedVideoListener.begin();
		} catch (Exception e) {
			log.fatal("Unable to start minitor for shared videos", e);
			System.exit(1);
		}
		sce.getServletContext().setAttribute(key, sharedVideoListener);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		SharedVideosListener sharedVideoListener = (SharedVideosListener)sce.getServletContext().getAttribute(key);
		sharedVideoListener.end();
	}

}
