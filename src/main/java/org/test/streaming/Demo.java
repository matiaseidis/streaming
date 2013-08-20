package org.test.streaming;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.test.streaming.monitor.Notifier;

public class Demo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	protected static final Log log = LogFactory.getLog(Demo.class);

	private static int bufferSize = 256 * 256;
	private String videoParam = "id";

	private static final long serialVersionUID = 1L;
	private final static String contentTypeMP4 = "video/mp4";
	byte[] headerMP4 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x14, (byte) 0x66, (byte) 0x74, (byte) 0x79, (byte) 0x70, (byte) 0x69, (byte) 0x73, (byte) 0x6F, (byte) 0x6D };

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		Conf conf = new Conf();
		String videoId = request.getParameter(videoParam);
		if (videoId == null) {
			videoId = conf.get("test.video.file.name");
			try {
				int fileSize = Integer.parseInt(conf.get("test.video.file.size"));
				downloadFile(response, videoId, fileSize, conf);
			} catch (Exception e) {
				log.error(e);
			}
			return;
		} else {
		}

		log.info("Empezando en el servle para video id: " + videoId);

		request.getSession().getServletContext().setAttribute(videoParam, videoId);

		try {
			downloadFilePosta(response, videoId, conf);
			// downloadFile(response, videoId,
			// Integer.parseInt(conf.get("test.video.file.size")), conf);
		} catch (Exception e) {
			log.error(e);
		}
	}

	private void downloadFilePosta(HttpServletResponse response, String videoId, Conf conf) {
		try {
			System.out.println("Demo.downloadFilePosta()");
			WatchMovieRetrievalPlan retrievalPlan = (WatchMovieRetrievalPlan) new Notifier(conf).getRetrievalPlan(videoId, conf.get("test.user.id"));

			if (retrievalPlan == null) {
				log.error("Error de comunicacion con el indice");
				return;
			}
			if (retrievalPlan.getRequests().isEmpty()) {
				log.error("Sin suficientes fuentes para crear plan");
				return;
			}

			log.info("------------------ RP --------------------------");
			log.info("retrievalPlan.getVideoId(): " + retrievalPlan.getVideoId());
			log.info("retrievalPlan.getVideoLenght(): " + retrievalPlan.getVideoLenght());
			log.info("retrievalPlan.getRequests().size(): " + retrievalPlan.getRequests().size());
			log.info("retrievalPlan.getRequests().get(0).getHost(): " + retrievalPlan.getRequests().get(0).getHost());
			log.info("retrievalPlan.getRequests().get(0).getPort(): " + retrievalPlan.getRequests().get(0).getPort());
			log.info("retrievalPlan.getRequests().get(0).getRequest().getDirection(): " + retrievalPlan.getRequests().get(0).getRequest().getDirection());
			log.info("retrievalPlan.getRequests().get(0).getRequest().getFileName(): " + retrievalPlan.getRequests().get(0).getRequest().getFileName());
			log.info("retrievalPlan.getRequests().get(0).getRequest().getFirstByteIndex(): " + retrievalPlan.getRequests().get(0).getRequest().getFirstByteIndex());
			log.info("retrievalPlan.getRequests().get(0).getRequest().getLength(): " + retrievalPlan.getRequests().get(0).getRequest().getLength());
			log.info("retrievalPlan.getRequests().get(0).getRequest().getMovieId(): " + retrievalPlan.getRequests().get(0).getRequest().getMovieId());
			log.info("retrievalPlan.getRequests().get(0).getRequest().getCacho().getFirstByteIndex(): " + retrievalPlan.getRequests().get(0).getRequest().getCacho().getFirstByteIndex());
			log.info("retrievalPlan.getRequests().get(0).getRequest().getCacho().getLastByteIndex(): " + retrievalPlan.getRequests().get(0).getRequest().getCacho().getLastByteIndex());
			log.info("retrievalPlan.getRequests().get(0).getRequest().getCacho().getLength(): " + retrievalPlan.getRequests().get(0).getRequest().getCacho().getLength());

			log.info(conf.getSharedDir().getAbsolutePath());
			log.info(conf.getTempDir().getAbsolutePath());

			response.setBufferSize(bufferSize);
			response.setContentType(contentTypeMP4);
			// TODO agregar el tamaño del video al plan
			response.setContentLength((int) retrievalPlan.getVideoLenght());
			response.addHeader("Content-disposition", "attachment;filename=" + videoId);
			response.flushBuffer();

			OutputStream os = response.getOutputStream();

			new DefaultMovieRetrievalPlanInterpreter(conf.getSharedDir(), conf.getTempDir()).interpret(retrievalPlan, os, new ProgressLogger());

			os.flush();
			os.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public void downloadFile(HttpServletResponse response, String videoId, int videoSize, Conf conf) throws Exception {
		try {
			response.setBufferSize(bufferSize);
			response.setContentType(contentTypeMP4);
			response.setContentLength(videoSize);
			response.addHeader("Content-disposition", "attachment;filename=" + videoId);

			response.flushBuffer();

			OutputStream os = response.getOutputStream();

			new DefaultMovieRetrievalPlanInterpreter(new File("/home/us/sharedCachos"), new File("/home/us/tempCachos")).interpret(new DummyMovieRetrievalPlan(videoId, conf), os, new ProgressLogger());

			os.flush();
			os.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}