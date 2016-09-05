package servlet;

import webServer.Request;
import webServer.Response;
import webServer.Servlet;

public class BServlet extends Servlet {
	public void business(Request req, Response res) throws Exception {
		System.out.println("this is B-Servlet, params="+req.get_params().get("hello"));
		res.sendData("I am B-servlet");
	}
}
