package server.head;

import org.junit.Ignore;
import org.junit.Test;

import server.socketlong.LongTest;
import cn.brent.socketclient.ISokectContext;
import cn.brent.socketclient.SocketContextFactory;

//@Ignore
public class HeadTest {
	
	ISokectContext context=SocketContextFactory.getContext("head");

	@Test
	public void testSend() throws InterruptedException{
		context.start();
		
		Thread.sleep(1000);
		
		for(int i=0;i<10;i++){
			context.getSendHelper().sendAsyn("hello server,I am brent"+i);
		}
		
		System.out.println("wait...");
		synchronized(LongTest.class){
			LongTest.class.wait();
		}
	}
	
	
}
