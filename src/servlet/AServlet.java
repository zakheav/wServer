package servlet;

import ioc.IOC;
import java.io.IOException;
import aop.AOP;
import webServer.Request;
import webServer.Response;
import webServer.Servlet;

public class AServlet extends Servlet {
	public void business(Request req, Response res) {
		IOC ioc = IOC.getInstance();
		AOP proxy = (AOP)ioc.getObject("AOP");
		((HelloInterface)(proxy.proxy)).sayHello();
		
		try {
			res.sendData("I am A-servlet");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
