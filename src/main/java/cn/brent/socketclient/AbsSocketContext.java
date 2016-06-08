package cn.brent.socketclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.socketclient.config.SocketConfigs.SocketConfig;

public abstract class AbsSocketContext implements ISokectContext{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/** 配置 */
	protected final SocketConfig config;
	
	public AbsSocketContext(SocketConfig config) {
		config.verify();
		this.config=config;
		logger.info("use config:"+config.toString());
	}

	public SocketConfig getConfig() {
		return config;
	}
	
}
