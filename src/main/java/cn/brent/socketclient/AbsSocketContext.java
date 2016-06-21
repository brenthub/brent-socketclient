package cn.brent.socketclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.socketclient.config.SocketConfigs.SocketConfig;

public abstract class AbsSocketContext implements ISokectContext{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/** 配置 */
	protected final SocketConfig config;
	
	/** 运行开关 */
	protected boolean runflag = false;
	
	public AbsSocketContext(SocketConfig config) {
		config.verify();
		this.config=config;
		logger.info("use config:"+config.toString());
	}

	public SocketConfig getConfig() {
		return config;
	}
	
	@Override
	public void start() {
		if(runflag){
			return;
		}
		startHandler();
		runflag = true;
	}
	
	protected abstract void startHandler();
	
	protected abstract void stopHandler();
	
	@Override
	public void stop() {
		if(!runflag){
			return;
		}
		stopHandler();
		runflag = false;
	}
	
}
