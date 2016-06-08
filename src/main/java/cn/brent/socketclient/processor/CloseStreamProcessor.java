package cn.brent.socketclient.processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.brent.socketclient.AbsProcessor;
import cn.brent.socketclient.IProcessor;

/**
 * 一直读到对方将输出流关闭
 */
public class CloseStreamProcessor extends AbsProcessor implements IProcessor {
	
	@Override
	public byte[] fullMsg(InputStream is) {
		logger.debug("read byte from InputStream");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len;
		try {
			while ((len = is.read(b)) != -1) {
				bos.write(b, 0, len);
			}
		} catch (IOException e) {
			logger.error("CloseStreamProcessor fullMsg error",e);
			throw new RuntimeException(e);
		}
		logger.debug("read byte end,msg is:"+new String(bos.toByteArray()));
		return bos.toByteArray();
	}

}
