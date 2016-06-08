package cn.brent.socketclient;

import java.io.IOException;
import java.io.InputStream;

/**
 * socket 字节读取处理器
 */
public interface IProcessor {

	/**
	 * 从输入流中读出一个完整的消息
	 * @param is
	 * @return
	 */
	public byte[] fullMsg(InputStream is) throws IOException;

}
