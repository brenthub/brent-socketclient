package config;

import java.io.UnsupportedEncodingException;

import org.junit.Ignore;
import org.junit.Test;

import cn.brent.socketclient.config.Constant;
import cn.brent.socketclient.config.SocketConfigs;

@Ignore
public class ConfigTest {

	@Test
	public void testParse(){
		SocketConfigs configs=SocketConfigs.parse("/socketclient_config.xml");
		System.out.println(configs.getConfig("test"));
		System.out.println(configs.getConfig("test2"));
	}
	
	@Test
//	@Ignore
	public void testFlag() throws UnsupportedEncodingException{
		System.out.println(Constant.DEFAULT_SEND_END_FLAG.getBytes()[0]);
		System.out.println("\n".getBytes()[0]);
		System.out.println("\n".getBytes("UTF-8")[0]);
		System.out.println("\n".getBytes("GBK")[0]);
		
		System.out.println("200".getBytes().length);
	}
}
