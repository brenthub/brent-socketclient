package cn.brent.socketclient.longc;

import java.net.Socket;
import java.net.SocketException;

import cn.brent.socketclient.AbsSendHelper;
import cn.brent.socketclient.SendException;

public class SendHelper extends AbsSendHelper {

	protected SocketLongContext context;

	protected SendHelper(SocketLongContext context) {
		super(context.getConfig());
		this.context=context;
	}

	@Override
	public byte[] send(byte[] msg) {
		
		if(context.getConfig().getMsgListener().isOpen()){
			throw new SendException("NotSupport", "when open listener will not support synchronization send");
		}
		
		SocketWraper sow=null;
		try {
			sow=context.getSocketPool().borrowObject();
			sow.sendMsg(msg);
			return sow.receiveMsg();
		} catch (Exception e) {
			throw new SendException("sendError", e);
		} finally{
			if(sow!=null){
				context.getSocketPool().returnObject(sow);
			}
		}
	}
	
	@Override
	public void socketParamSet(Socket socket)throws SocketException {
		super.socketParamSet(socket);
		socket.setKeepAlive(true);
		socket.setTcpNoDelay(true);
	}
	
	@Override
	public void sendAsyn(byte[] msg) {
		try {
			context.getSendQueue().put(msg);
		} catch (InterruptedException e) {
			logger.error("put send queue error",e);
		}
	}

}
