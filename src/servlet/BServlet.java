package servlet;

import java.io.IOException;

import webServer.Request;
import webServer.Response;
import webServer.Servlet;

public class BServlet extends Servlet {
	public void business(Request req, Response res) {
		System.out.println("this is B-Servlet");

		try {
			res.sendData("I am B-servlet");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
