package server.socketshort;

import org.junit.Ignore;
import org.junit.Test;

import cn.brent.socketclient.ISokectContext;
import cn.brent.socketclient.SocketContextFactory;

@Ignore
public class ShortTest {

	ISokectContext context=SocketContextFactory.getMQContext("test");
	
	@Test
	public void testSend() throws InterruptedException{
		String rev=context.getSendHelper().send("hello server");
		System.out.println(rev);
		
		String rev1=context.getSendHelper().send("brent");
		System.out.println(rev1);
		
		context.getSendHelper().sendAsyn("asyn");
		Thread.sleep(20000);
	}
}
