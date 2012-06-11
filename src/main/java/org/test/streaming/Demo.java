package org.test.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Demo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

//	private static final double MegabitsPerSec = 9;
//	private static final double KbitsPerSec = MegabitsPerSec * 1000;

	private static int bufferSize = 256 * 256;

	private static final long serialVersionUID = 1L;
	private final static String contentTypeMP4 = "video/mp4";
	byte[] headerMP4 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x14, (byte) 0x66, (byte) 0x74, (byte) 0x79, (byte) 0x70, (byte) 0x69, (byte) 0x73, (byte) 0x6F, (byte) 0x6D };


	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			downloadFile(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	public void downloadFile(HttpServletResponse response) throws Exception {
		
		try {
			response.setBufferSize(bufferSize);
			response.setContentType(contentTypeMP4);
			response.setContentLength(Conf.VIDEO_SIZE);
			response.addHeader("Content-disposition", "attachment;filename=" + Conf.VIDEO);
			
			response.flushBuffer();
			
			OutputStream os = response.getOutputStream();

			List<CachoRetrieval> requests = new DummyMovieRetrievalPlan().getRequests();
			for (CachoRetrieval cachoRetrieval : requests) {
				CachoRequester cachoRequester = new CachoRequester(cachoRetrieval.getHost(), cachoRetrieval.getPort());
				CachoRequest request = cachoRetrieval.getRequest();
				cachoRequester.requestCacho(request.getFileName(), request.getFirstByteIndex(), request.getLength(), os);
			}
			
			os.flush();
			os.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// public void createParts() throws Exception {
	// String name = "./a.mp4";
	// InputStream is = new FileInputStream(name);
	// int part = 0;
	// OutputStream ous = new FileOutputStream(newPartFile(name, 0));
	//
	// int b = 0;
	// int count = 0;
	// try {
	// while ((b = is.read()) != -1) {
	// count++;
	// ous.write(b);
	// if (count == 2048 * 1024) {
	// ous.close();
	// part++;
	// File file = newPartFile(name, part);
	// ous = new FileOutputStream(file);
	// count = 0;
	// }
	// }
	// ous.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

//	private static File newPartFile(String name, int part) {
//		File file = new File(name + ".part." + part);
//		return file;
//	}

	public void downloadFile(HttpServletResponse response, String id, String name, int start) throws Exception {
		InputStream is = null;

		try {
//			String pathname = Upload.targetDir + name;
//			File fdesc = new File(pathname);

			response.setBufferSize(bufferSize);
			response.setContentType(contentTypeMP4);
			response.setContentLength(Conf.VIDEO_SIZE);
			response.addHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("videoDemo", "UTF-8"));

			response.flushBuffer();

			OutputStream os = response.getOutputStream();
//			is = createStream(fdesc);
//			int read = 0;
//			byte[] headerFLV = new byte[] { (byte) 0x46, (byte) 0x4C, (byte) 0x56, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09 };


			os.write(headerMP4);
//			if (start > 0) {
//				is.skip((long) start);
//				System.out.println("Demo.downloadFile()");
//			}
			new DefaultMovieRetrievalPlanInterpreter().interpret(new DummyMovieRetrievalPlan(), os);

			os.flush();
			os.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
//
//		try {
//			if (is != null)
//				is.close();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}

	}

//	private static InputStream createStream(File fdesc) throws FileNotFoundException {
//		final List<FileInputStream> parts = parts(fdesc);
//
//		System.err.println(parts.size());
//		return new InputStream() {
//
//			InputStream current;
//			int count = 0;
//
//			@Override
//			public int read() throws IOException {
//				if (current == null) {
//					current = parts.get(0);
//					if (current == null) {
//						System.out.println("no esta la sandonga");
//
//					}
//				}
//				int read = readByte();
//				if (read == -1) {
//					System.out.println("listo " + parts.indexOf(current));
//					int currentIndex = parts.indexOf(current);
//					if (parts.size() == currentIndex + 1) {
//						read = -1;
//						System.out.println("listo");
//					} else {
//						current = parts.get(currentIndex + 1);
//						read = readByte();
//					}
//				}
//				return read;
//			}
//
//			double BytesPerSec = KbitsPerSec * 1000 / 8;
//			double BytesPerMili = BytesPerSec / 1000;
//			double transferCostFactor = 2;
//
//			private int readByte() throws IOException {
//				int read = current.read();
//				count++;
//				if (count >= BytesPerMili * transferCostFactor) {
//					try {
//						Thread.sleep(1);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					count = 0;
//				}
//				return read;
//			}
//		};
//	}

//	private static int length(File fdesc) throws FileNotFoundException {
//		int length = 0;
//		String name = fdesc.getAbsolutePath();
//		File partFile = newPartFile(name, 0);
//		for (int part = 1; partFile.exists(); part++) {
//			length += partFile.length();
//			partFile = newPartFile(name, part);
//
//		}
//		return length;
//	}
//
//	private static List<FileInputStream> parts(File fdesc) throws FileNotFoundException {
//		final List<FileInputStream> parts = new LinkedList<FileInputStream>();
//		String name = fdesc.getAbsolutePath();
//		File partFile = newPartFile(name, 0);
//		for (int part = 1; partFile.exists(); part++) {
//			parts.add(new FileInputStream(partFile));
//			partFile = newPartFile(name, part);
//		}
//		return parts;
//	}
	
	
	
//	private static File newPartFile(String name, int part) {
//		File file = new File(name + ".part." + part);
//		return file;
//	}


//	private static InputStream createStream(File fdesc) throws FileNotFoundException {
//		final List<FileInputStream> parts = parts(fdesc);
//
//		System.err.println(parts.size());
//		return new InputStream() {
//
//			InputStream current;
//			int count = 0;
//
//			@Override
//			public int read() throws IOException {
//				if (current == null) {
//					current = parts.get(0);
//					if (current == null) {
//						System.out.println("no esta la sandonga");
//
//					}
//				}
//				int read = readByte();
//				if (read == -1) {
//					System.out.println("listo " + parts.indexOf(current));
//					int currentIndex = parts.indexOf(current);
//					if (parts.size() == currentIndex + 1) {
//						read = -1;
//						System.out.println("listo");
//					} else {
//						current = parts.get(currentIndex + 1);
//						read = readByte();
//					}
//				}
//				return read;
//			}
//
//			double BytesPerSec = KbitsPerSec * 1000 / 8;
//			double BytesPerMili = BytesPerSec / 1000;
//			double transferCostFactor = 2;
//
//			private int readByte() throws IOException {
//				int read = current.read();
//				count++;
//				if (count >= BytesPerMili * transferCostFactor) {
//					try {
//						Thread.sleep(1);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					count = 0;
//				}
//				return read;
//			}
//		};
//	}

//	private static int length(File fdesc) throws FileNotFoundException {
//		int length = 0;
//		String name = fdesc.getAbsolutePath();
//		File partFile = newPartFile(name, 0);
//		for (int part = 1; partFile.exists(); part++) {
//			length += partFile.length();
//			partFile = newPartFile(name, part);
//
//		}
//		return length;
//	}

//	private static List<FileInputStream> parts(File fdesc) throws FileNotFoundException {
//		final List<FileInputStream> parts = new LinkedList<FileInputStream>();
//		String name = fdesc.getAbsolutePath();
//		File partFile = newPartFile(name, 0);
//		for (int part = 1; partFile.exists(); part++) {
//			parts.add(new FileInputStream(partFile));
//			partFile = newPartFile(name, part);
//		}
//		return parts;
//	}

	// public static void main(String[] args) throws Exception{
	// Server server = new Server(Integer.valueOf(System.getenv("PORT")));
	// ServletContextHandler context = new
	// ServletContextHandler(ServletContextHandler.SESSIONS);
	// context.setContextPath("/");
	// server.setHandler(context);
	// context.addServlet(new ServletHolder(new Demo()),"/demo");
	// context.addServlet(new ServletHolder(new Upload()),"/upload");
	// server.start();
	// server.join();
	// }
}