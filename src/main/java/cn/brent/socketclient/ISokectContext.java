package cn.brent.socketclient;

/**
 * Socket上下文（同一个项目可能有多个socket，在此统一管理）
 */
public interface ISokectContext {
	
	/**
	 * 获取发送助手
	 * @return
	 */
	public ISendHelper getSendHelper();
	
	/**
	 * 开始
	 */
	public void start();
	
	/**
	 * 停止
	 */
	public void stop();
}
