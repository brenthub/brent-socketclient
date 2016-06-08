package cn.brent.socketclient.shortc;

import cn.brent.socketclient.AbsSocketContext;
import cn.brent.socketclient.ISendHelper;
import cn.brent.socketclient.config.SocketConfigs.SocketShortConfig;

public class SocketShortContext extends AbsSocketContext {
	
	protected SendHelper sendHelper;
	
	public SocketShortContext(SocketShortConfig config) {
		super(config);
		sendHelper=new SendHelper(this);
	}

	@Override
	public ISendHelper getSendHelper() {
		return sendHelper;
	}

	@Override
	public void start() {
		logger.info("socketShortContext start sucess");
	}

	@Override
	public void stop() {
		logger.info("socketShortContext stop sucess");
	}

}
