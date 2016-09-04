package webServer;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import dbpool.DBpool;
import threadPool.ThreadPoolTask;
import threadPool.ThreadPool;
import util.Reflection;
import util.XML;

public class HttpServer {
	public static final String fileRoot = System.getProperty("user.dir")
			+ File.separator + "webroot";
	private static String ip = "";
	private static int port = 0;
	private static ThreadPool tp;
	
	private static void init() {
		XML xml = new XML();
		// 启动连接池
		// DBpool.getInstance();
		// 启动线程池
		tp = ThreadPool.getInstance();
		// 生成servlet
		Map<String, String> servletList = xml.getServlets();
		for (String uri : servletList.keySet()) {
			Reflection rf = new Reflection();
			String className = servletList.get(uri);
			Servlet servlet = (Servlet) rf.getObject(className);
			ServletsMap.servlets.put(uri, servlet);
		}
		// 配置服务器ip和端口
		Map<String, String> serverConf = xml.getServerConf();
		HttpServer.ip = serverConf.get("ip");
		HttpServer.port = Integer.parseInt(serverConf.get("port"));
	}

	public static void start() {
		init();
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip));
			while (true) {
				Socket socket = null;
				socket = serverSocket.accept();// 如果有请求
				ThreadPoolTask task = new ThreadPoolTask(socket);// 把请求打包成任务
				tp.addTasks(task);// 交给线程池处理
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		HttpServer.start();
	}
}
