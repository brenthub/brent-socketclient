package cn.brent.socketclient;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.socketclient.config.SocketConfigs;
import cn.brent.socketclient.config.SocketConfigs.SocketConfig;
import cn.brent.socketclient.config.SocketConfigs.SocketLongConfig;
import cn.brent.socketclient.config.SocketConfigs.SocketShortConfig;
import cn.brent.socketclient.longc.SocketLongContext;
import cn.brent.socketclient.shortc.SocketShortContext;

/**
 * Socket上下文工厂
 */
public class SocketContextFactory {
	
	public static String DEFAULT_CONFIG_NAME="/socketclient_config.xml";
	
	protected static final Logger logger = LoggerFactory.getLogger(SocketContextFactory.class);
	
	public static Map<String,ISokectContext> contextMap=new HashMap<String, ISokectContext>();
	
	public static ISokectContext getMQContext(String configName,String id){
		String key=configName+"#"+id;
		ISokectContext context=contextMap.get(key);
		if(context==null){
			try {
				SocketConfigs configs=SocketConfigs.parse(configName);
				SocketConfig config=configs.getConfig(id);
				if(config instanceof SocketShortConfig){
					SocketShortConfig cfg=(SocketShortConfig)config;
					context=new SocketShortContext(cfg);
				}else if(config instanceof SocketLongConfig){
					SocketLongConfig cfg=(SocketLongConfig)config;
					context=new SocketLongContext(cfg);
				}
			} catch (Exception e) {
				logger.error("init context error",e);
			} finally {
				contextMap.put(key, context);
			}
		}
		return context;
	}
	
	public static ISokectContext getMQContext(String id){
		return getMQContext(DEFAULT_CONFIG_NAME, id);
	}
	
}
