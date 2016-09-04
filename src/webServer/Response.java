package webServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Response {
	private static int BUFFER_SIZE = 2048;
	public OutputStream output;
	public Request request;

	public Response(OutputStream out, Request req) {
		this.output = out;
		this.request = req;
	}

	public void sendData(String data) throws IOException{
		byte[] bytes;
		bytes = data.getBytes();
		for( int i=0; i<bytes.length; ++i ){
			output.write(bytes, i, 1);
		}
	}
	
	public void sendStaticData() throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		File file = new File(HttpServer.fileRoot, request.get_uri());
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			int ch = fis.read(bytes, 0, BUFFER_SIZE);
			while( ch != -1 ){
				output.write(bytes, 0, ch);
				ch = fis.read(bytes, 0, BUFFER_SIZE);
			}
			
		} catch (Exception e) {
			try{
				output.write("error request".getBytes());
				output.flush();
			} catch( Exception e1 ){
				e1.printStackTrace();
			}
		} finally{
			if(fis !=null){
                fis.close();
            }
		}
	}

}
