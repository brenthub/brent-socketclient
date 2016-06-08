package server.socketshort;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class SocketShortServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(9008);
			while (true) {
				Socket socket = server.accept();
				invoke(socket);
			}
		} catch (Exception e) {
			System.out.println("can not listen to:" + e);
		}
	}

	private static void invoke(final Socket socket) {
		new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("new conncet...");
					InputStream is = socket.getInputStream();
					OutputStream os = socket.getOutputStream();
				    BufferedReader iBufferStream = new BufferedReader(new InputStreamReader(is));  
				    String inMessage = iBufferStream.readLine();  
			        System.out.println("rev data:" + inMessage);  
			        os.write("server time :".getBytes());
			        os.write(new Date().toString().getBytes());
			        os.flush();
					os.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

}
