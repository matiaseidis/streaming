package org.test.streaming;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.common.collect.Iterables;

public class Upload extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	// private String contentType = "video/x-flv";
	
	public static final String targetDir = System.getProperty("user.home")+"/videitos/";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("up.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String url = request.getRequestURL().toString();
//		String[] u = url.split("/");

//		String fileAbsPath = u[u.length - 1];
		String fileAbsPath = request.getParameter("file");

		if(StringUtils.isEmpty(fileAbsPath)){
			// TODO validar que el archivo exista
			System.out.println("NO FILE PASSED");
			return;
		}
		
		try {
			createParts(fileAbsPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.getRequestDispatcher("up.jsp").forward(request, response);
	}

	public void createParts(String fileAbsPath) throws Exception {

		new File(targetDir).mkdirs();
		
		String[] splittedAbsPath = fileAbsPath.trim().split(File.separator);
		String name = splittedAbsPath[splittedAbsPath.length-1];
		InputStream is = new FileInputStream(fileAbsPath);
		
		OutputStream ous = new FileOutputStream(newPartFile(targetDir + name, 0));

		int part = 0;
		int b = 0;
		int count = 0;
		try {
			while ((b = is.read()) != -1) {
				count++;
				ous.write(b);
				if (count == 1024 * 1024) {
					ous.close();
					part++;
					File file = newPartFile(targetDir + name, part);
					ous = new FileOutputStream(file);
					count = 0;
				}
			}
			ous.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static File newPartFile(String name, int part) {
		File file = new File(name + ".part." + part);
		return file;
	}
}