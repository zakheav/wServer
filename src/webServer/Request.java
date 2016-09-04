package webServer;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Request {// 用于处理客户端发送的请求
	private InputStream input;// 输入流对象

	private String method = "";// 请求方法
	private String protocol = "";// 协议版本
	private String requestURI = "";// 请求URI，在HTTP头第一行请求方法后面
	private String agent = "";// 代理，用来标识代理浏览器的信息，对应HTTP头的User-Agent
	private String host = "";// 被请求主机的信息
	private String accept = "";// 对应HTTP头的Accept
	private String language = "";// 对应HTTP头的Accept-Language
	private String charset = "";// 请求的字符编码 对应HTTP请求中的Accept-Charset
	private String encoding = "";// 请求的编码方式 对应HTTP请求中的Accept-Encoding
	private String connection = "";// HTTP请求连接状态信息 对应HTTP请求中的Connection
	private Map<String,String> params = new HashMap<String,String>();
	
	public Request(InputStream in) {
		this.input = in;
		parser();
	}

	public InputStream get_input() {
		return input;
	}

	public String get_method(){ return this.method; }
	public String get_protocal(){ return this.protocol; }
	public String get_uri() { return this.requestURI; }
	public String get_agent(){ return this.agent; }
	public String get_host(){ return this.host; }
	public String get_accept(){ return this.accept; }
	public String get_language(){ return this.language; }
	public String get_charset(){ return this.charset; }
	public String get_encoding(){ return this.encoding; }
	public String get_connection(){ return this.connection; }
	public Map<String,String> get_params(){ return this.params; }
	
	private void parser() {
		byte[] buffer = new byte[2048];
		StringBuffer request = new StringBuffer(2048);
		int requestLength = 0;
		try {
			requestLength = this.input.read(buffer);// HTTP请求的信息存储在buffer里面
		} catch (Exception e) {
			e.printStackTrace();
			requestLength = -1;
		}
		for (int i = 0; i < requestLength; ++i) {
			request.append((char)buffer[i]);
		}
		String[] requestProtocal = (request.toString()).split("CRLF");// 把请求头按照回车换行符分割
		for (int i = 0; i < requestProtocal.length; ++i) {
			requestParser(requestProtocal[i]);
		}

	}

	/*
	 * GET /index HTTP/1.1 
	 * User-Agent: Opera/9.80 (Windows NT 6.1; U; Edition IBIS; zh-cn) Presto/2.6.30 Version/10.63 
	 * Host: localhost:81 
	 * Accept: text/html, application/xml;q=0.9, application/xhtml+xml, image/gif 
	 * Accept-Language: zh-CN,zh;q=0.9,en;q=0.8 
	 * Accept-Charset: iso-8859-1, utf-8, utf-16 
	 * Accept-Encoding: deflate, gzip, x-gzip, identity, *;q=0
	 * Connection: Keep-Alive
	 */
	private void requestParser(String request) {
		if (request.startsWith("GET")) {
			this.method = "GET";
			int index = request.indexOf("HTTP");
			String uriParamsString = request.substring("GET".length() + 1, index - 1);
			String[] uriParams = uriParamsString.split("\\?");
			
			this.requestURI = uriParams[0];//URI
			if( uriParams.length==2 ){
				String[] params = uriParams[1].split("&");
				for( int i=0; i<params.length; ++i ){
					
					String[] key_value = params[i].split("=");
					System.out.println(key_value[0]+" "+key_value[1]);
					this.params.put(key_value[0],key_value[1]);
				}
			}
			
			this.protocol = request.substring(index);
		} else if (request.startsWith("POST")) {
			this.method = "POST";
			int index = request.indexOf("HTTP");
			String uriParamsString  = request.substring("POST".length() + 1, index - 1);
			String[] uriParams = uriParamsString.split("\\?");
			
			this.requestURI = uriParams[0];//URI
			if( uriParams.length==2 ){
				String[] params = uriParams[1].split("&");
				for( int i=0; i<params.length; ++i ){
					
					String[] key_value = params[i].split("=");
					System.out.println(key_value[0]+" "+key_value[1]);
					this.params.put(key_value[0],key_value[1]);
				}
			}
			this.protocol = request.substring(index);
		} else if (request.startsWith("User-Agent:")) {
			this.agent = request.substring("User-Agent:".length() + 1);
		} else if (request.startsWith("Host:")) {
			this.host = request.substring("Host:".length() + 1);
		} else if (request.startsWith("Accept:")) {
			this.accept = request.substring("Accept:".length() + 1);
		} else if (request.startsWith("Accept-Language:")) {
			this.language = request.substring("Accept-Language:".length() + 1);
		} else if (request.startsWith("Accept-Charset:")) {
			this.charset = request.substring("Accept-Charset:".length() + 1);
		} else if (request.startsWith("Accept-Encoding:")) {
			this.encoding = request.substring("Accept-Encoding:".length() + 1);
		} else if (request.startsWith("Connection:")) {
			this.connection = request.substring("Connection:".length() + 1);
		}
	}
}
