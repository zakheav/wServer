package servlet;

import ioc.IOC;
import aop.AOP;
import webServer.Request;
import webServer.Response;
import webServer.Servlet;

public class AServlet extends Servlet {
	public void business(Request req, Response res) throws Exception {
		IOC ioc = IOC.getInstance();
		AOP proxy = (AOP)ioc.getObject("AOP");
		((HelloInterface)(proxy.proxy)).sayHello();
		res.sendData("I am A-servlet");
	}
}
