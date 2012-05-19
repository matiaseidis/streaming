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

public class Demo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final double MegabitsPerSec = 0.2;
	private static final double KbitsPerSec = MegabitsPerSec * 1000;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String contentTypeoctet = "application/octet-stream";
	private final static String contentTypeMP4 = "video/mp4";

	// private String contentType = "video/x-flv";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getRequestURL().toString();
		String[] u = url.split("/");

		String name = u[u.length - 1];
		String id = u[u.length - 2];
		String start = request.getParameter("start");

		int st = 0;
		if (start != null)
			st = Integer.parseInt(start);

		try {
			downloadFile(response, id, name, st);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public static void main(String[] args) throws Exception {
		String name = "./a.mp4";
		InputStream is = new FileInputStream(name);
		int part = 0;
		OutputStream ous = new FileOutputStream(newPartFile(name, 0));

		int b = 0;
		int count = 0;
		try {
			while ((b = is.read()) != -1) {
				count++;
				ous.write(b);
				if (count == 1024 * 1024) {
					ous.close();
					part++;
					File file = newPartFile(name, part);
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

	static public void downloadFile(HttpServletResponse response, String id, String name, int start) throws Exception {
		InputStream is = null;

		try {
			// File fdesc = new
			// File("E:\\Downloads\\ffmpeg-20120503-git-c1fe2db-win32-static\\ffmpeg-20120503-git-c1fe2db-win32-static\\bin\\test2.flv");
			// String pathname = "E:\\Lucas\\Dropbox\\lucas\\testest2Meta.flv";
			String pathname = "./a.mp4";
			File fdesc = new File(pathname);
			// getContentType( fdesc.getName() )
			response.setBufferSize(1024 * 1024);
			// response.setBufferSize(0);
			response.setContentType(contentTypeMP4);
			response.addHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(pathname, "UTF-8"));
			List<FileInputStream> parts = parts(fdesc);

			response.addHeader("Content-Length", "" + length(fdesc));
			response.flushBuffer();

			OutputStream os = response.getOutputStream();
			is = createStream(fdesc);
			int read = 0;
			byte[] headerFLV = new byte[] { (byte) 0x46, (byte) 0x4C, (byte) 0x56, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09 };

			byte[] headerMP4 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x14, (byte) 0x66, (byte) 0x74, (byte) 0x79, (byte) 0x70, (byte) 0x69, (byte) 0x73, (byte) 0x6F, (byte) 0x6D };

			if (start > 0) {
				is.skip((long) start);
				os.write(headerMP4);
			}

			while ((read = is.read()) != -1) {
				os.write(read);
			}

			os.flush();
			os.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			if (is != null)
				is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static InputStream createStream(File fdesc) throws FileNotFoundException {
		final List<FileInputStream> parts = parts(fdesc);

		System.err.println(parts.size());
		return new InputStream() {

			InputStream current;
			int count = 0;

			@Override
			public int read() throws IOException {
				if (current == null) {
					current = parts.get(0);
				}
				int read = readByte();
				if (read == -1) {
					int currentIndex = parts.indexOf(current);
					if (parts.size() == currentIndex + 1) {
						read = -1;
					} else {
						current = parts.get(currentIndex + 1);
						read = readByte();
					}
				}
				return read;
			}

			double BytesPerSec = KbitsPerSec * 1000 / 8;
			double BytesPerMili = BytesPerSec / 1000;
			double transferCostFactor = 2;

			private int readByte() throws IOException {
				int read = current.read();
				count++;
				if (count >= BytesPerMili * transferCostFactor) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					count = 0;
				}
				return read;
			}
		};
	}

	private static int length(File fdesc) throws FileNotFoundException {
		int length = 0;
		String name = fdesc.getAbsolutePath();
		File partFile = newPartFile(name, 0);
		for (int part = 1; partFile.exists(); part++) {
			length += partFile.length();
			partFile = newPartFile(name, part);

		}
		return length;
	}

	private static List<FileInputStream> parts(File fdesc) throws FileNotFoundException {
		final List<FileInputStream> parts = new LinkedList<FileInputStream>();
		String name = fdesc.getAbsolutePath();
		File partFile = newPartFile(name, 0);
		for (int part = 1; partFile.exists(); part++) {
			parts.add(new FileInputStream(partFile));
			partFile = newPartFile(name, part);
		}
		return parts;
	}
}