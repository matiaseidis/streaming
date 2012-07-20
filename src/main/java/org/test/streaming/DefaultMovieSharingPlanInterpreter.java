package org.test.streaming;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultMovieSharingPlanInterpreter implements MovieSharingPlanInterpreter {

	protected static final Log log = LogFactory.getLog(DefaultMovieSharingPlanInterpreter.class);

	private Conf conf;

	public DefaultMovieSharingPlanInterpreter(Conf conf) {
		this.setConf(conf);
	}

	@Override
	public void interpret(MovieRetrievalPlan sharingPlan) {
		List<CachoRetrieval> requests = sharingPlan.getRequests();
		for (CachoRetrieval cachoRetrieval : requests) {
			CachoRequest request = cachoRetrieval.getRequest();
			CachoPusher cachoPusher = new CachoPusher(cachoRetrieval.getHost(), cachoRetrieval.getPort());
			File originalMovie = new File(this.getConf().getCachosDir(), request.getFileName());
			try {
				cachoPusher.push(new FileInputStream(originalMovie), request.getFileName(), request.getFirstByteIndex(), request.getLength());
			} catch (FileNotFoundException e) {
				log.error("Couldnt open file for push", e);
			}
		}

	}

	public Conf getConf() {
		return conf;
	}

	public void setConf(Conf conf) {
		this.conf = conf;
	}
}
