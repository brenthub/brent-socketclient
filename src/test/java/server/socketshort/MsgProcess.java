package server.socketshort;

import cn.brent.socketclient.IMsgListener;

public class MsgProcess implements IMsgListener {

	@Override
	public void recieveMessages(String msg, byte[] bmsg) {
		System.out.println("异步处理："+msg+" bmsg lenth-->"+bmsg.length);
	}

}
