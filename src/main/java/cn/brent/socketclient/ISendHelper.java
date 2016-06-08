package cn.brent.socketclient;

/**
 * 消息发送助手
 */
public interface ISendHelper {

	/**
	 * 异步发送消息
	 * @param msg
	 */
	public void sendAsyn(byte[] msg);
	
	/**
	 * 同步发送消息
	 * @param msg
	 */
	public byte[] send(byte[] msg);
	
	/**
	 * 异步发送消息
	 * @param msg
	 */
	public void sendAsyn(String msg);
	
	/**
	 * 同步发送消息
	 * @param msg
	 */
	public String send(String msg);
}
