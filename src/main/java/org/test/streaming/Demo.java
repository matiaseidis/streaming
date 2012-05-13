package org.test.streaming;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Demo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private final static String contentType = "application/octet-stream";
	private final static String contentType = "video/mp4";
	
//		private String contentType = "video/x-flv";



	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getRequestURL().toString();
		String[] u=url.split( "/" );

		String name=u[ u.length-1 ];
		String id=u[ u.length-2 ];
		String start=request.getParameter("start");

		int st=0;
		if ( start!=null ) st=Integer.parseInt( start );

//		downloadFile( response, id, name, st );
		
		getFile(response);
	}

	private void getFile(HttpServletResponse response) {
		BufferedInputStream bis = null;
		try
		{

			// Send data
			String urlStr = "http://186.23.108.31/streaming/test/blablabla.mp4";

			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection ();

			// Get the response
			bis = new BufferedInputStream(conn.getInputStream());
			
			OutputStream os = response.getOutputStream();
			int read = 0;
			byte[] bytes = new byte[1024];
			
			byte[] headerMP4 = new byte[] {
					(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x14, (byte) 0x66, (byte) 0x74, (byte) 0x79, (byte) 0x70,
					(byte) 0x71, (byte) 0x74, (byte) 0x20, (byte) 0x20
			};
			
//			byte[] headerFLV= new byte[] {
//					(byte) 0x46, (byte) 0x4C, (byte) 0x56,
//					(byte) 0x01,
//					(byte) 0x05,
//					(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09,
//					(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09
//			};

			while((read = bis.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}

			os.flush();
			os.close();
		} catch (Exception ex) { ex.printStackTrace(); }

		try {
			if (bis!=null) bis.close();
		} catch (Exception ex) { ex.printStackTrace(); }
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet( req, resp );
	}

	static public void downloadFile( HttpServletResponse response, String id, String name, int start ) {
		InputStream is = null;

		try {
			File fdesc=new File( "/home/matias/.videos/"+name );
			//getContentType( fdesc.getName() )
			response.setContentType(contentType);

			response.addHeader( "Content-disposition", "attachment;filename="+URLEncoder.encode(fdesc.getName(),"UTF-8") );
			response.addHeader( "Content-Length", ""+fdesc.length() );

			OutputStream os = response.getOutputStream();
			is= (InputStream) new FileInputStream( fdesc ) ;
			int read = 0;
			byte[] bytes = new byte[1024];
			
			byte[] headerMP4 = new byte[] {
					(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x14, (byte) 0x66, (byte) 0x74, (byte) 0x79, (byte) 0x70,
					(byte) 0x71, (byte) 0x74, (byte) 0x20, (byte) 0x20
			};
			
//			byte[] headerFLV= new byte[] {
//					(byte) 0x46, (byte) 0x4C, (byte) 0x56,
//					(byte) 0x01,
//					(byte) 0x05,
//					(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09,
//					(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09
//			};

			if ( start>0 ) {
				is.skip( (long) start );
//				os.write( headerFLV );
				os.write( headerMP4 );
			}

			while((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}

			os.flush();
			os.close();
		} catch (Exception ex) { ex.printStackTrace(); }

		try {
			if (is!=null) is.close();
		} catch (Exception ex) { ex.printStackTrace(); }

	}
}