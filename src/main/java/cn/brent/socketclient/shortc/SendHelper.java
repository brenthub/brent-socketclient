package cn.brent.socketclient.shortc;

import java.io.IOException;
import java.net.Socket;

import cn.brent.socketclient.AbsSendHelper;
import cn.brent.socketclient.SendException;

public class SendHelper extends AbsSendHelper {

	public SendHelper(SocketShortContext context) {
		super(context.getConfig());
	}

	@Override
	public byte[] send(byte[] msg) {
		Socket so = null;
		try {
			so = new Socket();
			// 连接
			connect(so);
			// 发送
			sendByte(so, msg);
			// 同步读取
			return readByte(so);
		} catch (Exception e) {
			logger.error("send error", e);
			throw new SendException("error", "send error," + e.getMessage());
		} finally {
			if (so != null) {
				try {
					so.getInputStream().close();
					so.getOutputStream().close();
					so.close();
				} catch (IOException e) {
				}
			}
		}
	}
	

	@Override
	public void sendAsyn(byte[] msg) {
		Socket so = null;
		try {
			so = new Socket();
			// 连接
			connect(so);
			// 发送
			sendByte(so, msg);
			// 异步处理消息
			if (listener != null) {
				byte[] bmsg = readByte(so);
				msgAsynProcess(bmsg);
			}
		} catch (Exception e) {
			logger.error("send error", e);
			throw new SendException("error", "send error," + e.getMessage());
		} finally {
			if (so != null) {
				try {
					so.getInputStream().close();
					so.getOutputStream().close();
					so.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
