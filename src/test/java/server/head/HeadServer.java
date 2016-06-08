package server.head;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

public class HeadServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(9010);
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
				    while(true){
				    	String inMessage = iBufferStream.readLine();
				    	System.out.println("rev data:" + inMessage);  
				    	
				    	if(inMessage.equals("000000")){//
				    		continue;
				    	}else{
				    		String code=RandomStringUtils.randomNumeric(15);
				    		String mac = RandomStringUtils.randomAlphabetic(32);
				    		String body="this is a cmb demo. you data is ["+inMessage+"]";
				    		String head=StringUtils.leftPad(""+body.length(), 6, "0");
				    		
				    		os.write(head.getBytes());
				    		os.write(mac.getBytes());
				    		os.write(body.getBytes());
				    		os.write(code.getBytes());
				    		os.flush();
				    	}
				    }
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

}
