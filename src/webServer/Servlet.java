package webServer;

import org.apache.log4j.Logger;

public abstract class Servlet {
	private static Logger log = Logger.getLogger(Servlet.class);

	public void business(Request req, Response res) throws Exception {
	}// 需要重载

	public void reply(Request req, Response res) {
		try {
			log.info("this is a log info test");
			business(req, res);
		} catch (Exception e) {
			log.error(Servlet.class, e);
		}
	}
}
