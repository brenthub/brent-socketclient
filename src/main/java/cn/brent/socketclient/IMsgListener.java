package cn.brent.socketclient;

/**
 * 返回消息侦听
 */
public interface IMsgListener {

	/**
	 * 收到消息时的回调
	 * @param msg 字符，当配置有接收字符编码时有此值
	 * @param bmsg 字节
	 * @throws InterruptedException
	 */
	public void recieveMessages(String msg, byte[] bmsg);
	
}
