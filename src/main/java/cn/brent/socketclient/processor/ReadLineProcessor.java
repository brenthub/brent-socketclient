package cn.brent.socketclient.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.brent.socketclient.AbsProcessor;
import cn.brent.socketclient.IProcessor;

/**
 * 以换行符为结尾的读取
 */
public class ReadLineProcessor extends AbsProcessor implements IProcessor {

	@Override
	public byte[] fullMsg(InputStream is) {
		BufferedReader iBufferStream = new BufferedReader(new InputStreamReader(is));
		String inMessage;
		try {
			logger.debug("read byte from InputStream");
			inMessage = iBufferStream.readLine();
			logger.debug("read byte end,msg is:"+inMessage);
		} catch (IOException e) {
			logger.error("ReadLineProcessor fullMsg error",e);
			throw new RuntimeException(e);
		}
		return inMessage.getBytes();
	}

}
