package org.test.streaming.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.test.streaming.Conf;
import org.test.streaming.Dimon;

public class DimonStarterListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Conf conf = new Conf();
		Dimon dimon = new Dimon(conf.getDaemonPort());
		sce.getServletContext().setAttribute("dimon", dimon);
		dimon.run();
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Dimon dimon = (Dimon) sce.getServletContext().getAttribute("dimon");
		dimon.stop();
		
	}

}
