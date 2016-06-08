package server.socketlong;

import org.junit.Ignore;
import org.junit.Test;

import cn.brent.socketclient.ISokectContext;
import cn.brent.socketclient.SocketContextFactory;

@Ignore
public class LongTest {

	ISokectContext context=SocketContextFactory.getMQContext("test2");
	
	@Test
	public void testSend() throws InterruptedException{
		
		context.start();
		
		Thread.sleep(2000);
		
//		String rev=context.getSendHelper().send("hello server");
//		System.out.println(rev);
//		
//		String rev1=context.getSendHelper().send("brent");
//		System.out.println(rev1);
		
		context.getSendHelper().sendAsyn("asyn");
		
		System.out.println("wait...");
		synchronized(LongTest.class){
			LongTest.class.wait();
		}
		


	}
}
