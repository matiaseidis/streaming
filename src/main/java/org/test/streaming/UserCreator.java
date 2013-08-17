package org.test.streaming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.monitor.Notifier;

public class UserCreator extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final Log log = LogFactory.getLog(UserCreator.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		req.getRequestDispatcher("user.jsp").forward(req, resp);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String id = req.getParameter("userId");
		String ip = req.getParameter("userIp");
		String servlePort = req.getParameter("userServlePort");
		String dimonPort = req.getParameter("userDimonPort");
		String email = req.getParameter("userEmail");

		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(ip)
				|| StringUtils.isEmpty(servlePort) || StringUtils.isEmpty(dimonPort)
				|| StringUtils.isEmpty(email)) {

			log.error("no puede haber campos vacios para crear usuario");
			return;
		}
		
		User user = new User(id, email, ip, servlePort, dimonPort);
		
		Conf conf = new Conf();
		new Notifier(conf).registerUser(user);
		
		log.info("user ok -> to index -"+id+" - "+email+" - "+servlePort+" - "+dimonPort+" - "+ip);
		
	}

}
