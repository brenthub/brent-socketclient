package cn.brent.socketclient.config;


/**
 * 常量类
 */
public class Constant {

	/** 重试次数，默认1次 */
	public static final int DEFAULT_RETRY = 1;

	/** 重试间隔 1秒 */
	public static final long DEFAULT_RETRY_INTERVAL = 1 * 1000;

	/** 超时时间 ，默认30秒*/
	public static final int DEFAULT_TIMEOUT = 30 * 1000;

	/** 默认缓冲区大小 */
	public static final int DEFAULT_BUFFER_SIZE = 1024 * 200;
	
	/** 默认发送结果标志 */
	public static final String DEFAULT_SEND_END_FLAG = System.getProperty("line.separator","\n");
	
	/** 默认异步消息处理线程大小 */
	public static final int DEFAULT_MAX_RUNNER = 200;
	
	/** 默认不开启异步消息处理 */
	public static final boolean DEFAULT_MSG_LISTENNER_OPEN = false;

}
