package webServer;

public abstract class Servlet {
	public void business(Request req, Response res) {
	}// 需要重载

	public void reply(Request req, Response res) {
		business(req, res);
	}
}
