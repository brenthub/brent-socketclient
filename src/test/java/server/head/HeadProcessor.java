package server.head;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;

import cn.brent.socketclient.AbsProcessor;

/**
 * 通过设定消息头来确定消息长度
 */
public class HeadProcessor extends AbsProcessor{
	
	/** 消息头长度*/
	private static final int HEAD_LENGTH=6;
	
	/** 消息最大长度 200KB*/
	private static final int MAX_SINGLE_LENGTH=200 * 1024;
	
	/** 报文码长度*/
	private static final int CODE_LENGTH = 15;
	
	/** MAC长度*/
	private static final int MAC_LENGTH = 32;
	
	private byte[] revBytes=null;//防止粘包

	@Override
	public byte[] fullMsg(InputStream input) throws IOException {
		
		while(true){
			if (revBytes == null) {
				revBytes = new byte[0];
			}
			/**
			 * 1、读取报文头
			 */
			if (revBytes.length < HEAD_LENGTH) {//粘包数据小于报文头时，读一个报文头出来
				byte[] headBytes = new byte[HEAD_LENGTH - revBytes.length];
				int couter = input.read(headBytes);
				if (couter < 0) {
					throw new RuntimeException("read head error");
				}
				revBytes = ArrayUtils.addAll(revBytes, ArrayUtils.subarray(headBytes, 0, couter));
				if (couter < headBytes.length) {// 未满足长度位数，可能是粘包造成，继续读一个报文头
					continue;
				}
			}
			String headMsg = new String(ArrayUtils.subarray(revBytes, 0, HEAD_LENGTH));//前六位是消息体的长度
			int xmlLength=0;
			try {
				xmlLength = NumberUtils.toInt(headMsg);
			} catch (Exception e) {
			}
			if (xmlLength <= 0 || xmlLength > MAX_SINGLE_LENGTH) {
				throw new RuntimeException("Not a valid message.");
			}
			/**
			 * 2、读取报文体
			 */
			int bodyLength = CODE_LENGTH + xmlLength + MAC_LENGTH;
			if (revBytes.length < HEAD_LENGTH + bodyLength) {//未读到完整的消息
				byte[] bodyBytes = new byte[HEAD_LENGTH + bodyLength - revBytes.length];//读一个完整的消息剩下的部分
				int couter = input.read(bodyBytes);
				if (couter < 0) {
					throw new RuntimeException("read head error");
				}
				revBytes = ArrayUtils.addAll(revBytes, ArrayUtils.subarray(bodyBytes, 0, couter));
				if (couter < bodyBytes.length) {// 未满足长度位数，可能是粘包造成，保存读取到的
					continue;
				}
			}
			byte[] bodyBytes = ArrayUtils.subarray(revBytes, HEAD_LENGTH, HEAD_LENGTH + bodyLength);//从一个完整的消息中截出消息体
			revBytes = ArrayUtils.subarray(revBytes, HEAD_LENGTH + bodyLength, revBytes.length);
			return bodyBytes;
		}
	}

}
