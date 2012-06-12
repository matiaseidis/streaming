package org.test.streaming;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Subtitle extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			
			InputStream fis = new FileInputStream(Conf.VIDEO_DIR + Conf.SUBS);

			resp.setBufferSize(256);
			resp.setContentType("text/plain");
			resp.addHeader("Content-disposition", "attachment;filename=" + Conf.SUBS);

			resp.flushBuffer();

			OutputStream os = resp.getOutputStream();

			byte[] b = new byte[1];
			while (fis.read(b) != -1){
				os.write(b);
			}

			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
