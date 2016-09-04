package threadPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import webServer.Request;
import webServer.Response;
import webServer.Servlet;
import webServer.ServletsMap;

public class ThreadPoolTask implements Runnable {
	Socket clientSocket = null;

	public ThreadPoolTask(Socket cs) {
		clientSocket = cs;
	};

	public void run() {
		try {
			InputStream input = clientSocket.getInputStream();
			OutputStream output = clientSocket.getOutputStream();
			Request req = new Request(input);
			Response res = new Response(output, req);
			String uri = req.get_uri();
			Servlet processor = ServletsMap.servlets.get(uri);
			if (processor != null) {
				processor.reply(req, res);
			} else {
			}
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
