package cn.brent.socketclient.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ExtendedBaseRules;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SocketConfigs {

	protected static final Logger logger = LoggerFactory.getLogger(SocketConfigs.class);

	private Map<String, SocketConfig> configs = new HashMap<String, SocketConfig>();

	private SocketConfigs() {
	}

	public static SocketConfigs parse(String configName) {
		try {
			Digester dig = new Digester();

			dig.setValidating(true);
			dig.setNamespaceAware(true);
			dig.setProperty(SocketClientResolver.SCHEMA_LANGUAGE_ATTRIBUTE, SocketClientResolver.XSD_SCHEMA_LANGUAGE);
			dig.setEntityResolver(new SocketClientResolver());
			dig.setErrorHandler(new ErrorHandler() {

				@Override
				public void warning(SAXParseException exception) throws SAXException {
					logger.warn("SAXParse Warning:", exception.getMessage());
				}

				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					logger.warn("SAXParse FatalError:" + exception.getMessage());
					throw new RuntimeException(exception.getMessage());
				}

				@Override
				public void error(SAXParseException exception) throws SAXException {
					logger.warn("SAXParse error:" + exception.getMessage());
					throw new RuntimeException(exception.getMessage());
				}
			});
			// push 调用类
			dig.push(new SocketConfigs());

			// 设置匹配规则处理类
			dig.setRules(new ExtendedBaseRules());

			// 短连接
			processNode(dig, "socket-configs/socket-short", SocketShortConfig.class);
			// 发送者
			processNode(dig, "socket-configs/socket-short/send", Send.class);
			// 接收者
			processNode(dig, "socket-configs/socket-short/receive", Receive.class);
			// 异步消息
			processNode(dig, "socket-configs/socket-short/msglistener", MsgListener.class);

			// 长连接
			processNode(dig, "socket-configs/socket-long", SocketLongConfig.class);
			// 线程池
			processNode(dig, "socket-configs/socket-long/pool", Pool.class);
			// 长连接心跳
			processNode(dig, "socket-configs/socket-long/heartbeat", Heartbeat.class);
			// 发送者
			processNode(dig, "socket-configs/socket-long/send", Send.class);
			// 接收者
			processNode(dig, "socket-configs/socket-long/receive", Receive.class);
			// 异步消息
			processNode(dig, "socket-configs/socket-long/msglistener", MsgListener.class);

			dig.addSetNext("socket-configs/socket-short/send", "setSend");
			dig.addSetNext("socket-configs/socket-short/receive", "setReceive");
			dig.addSetNext("socket-configs/socket-short/msglistener", "setMsgListener");

			dig.addSetNext("socket-configs/socket-long/pool", "setPool");
			dig.addSetNext("socket-configs/socket-long/heartbeat", "setHeartbeat");
			dig.addSetNext("socket-configs/socket-long/send", "setSend");
			dig.addSetNext("socket-configs/socket-long/receive", "setReceive");
			dig.addSetNext("socket-configs/socket-long/msglistener", "setMsgListener");

			dig.addSetNext("socket-configs/socket-short", "addConfig");

			dig.addSetNext("socket-configs/socket-long", "addConfig");

			return (SocketConfigs) dig.parse(SocketConfigs.class.getResourceAsStream(configName));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void processNode(Digester dig, String name, Class<?> clz) {
		dig.addObjectCreate(name, clz);
		dig.addSetProperties(name);
		dig.addBeanPropertySetter(name + "/?");
	}

	public void addConfig(SocketConfig config) {
		configs.put(config.getId(), config);
	}

	/**
	 * 获取配置项
	 * 
	 * @param name
	 * @return
	 */
	public SocketConfig getConfig(String id) {
		return configs.get(id);
	}

	/**
	 * socket 配置
	 */
	public abstract static class SocketConfig {

		/** ID */
		private String id;

		/** 主机地址 */
		private String address;

		/** 端口 */
		private int port;

		/** 连接超时时间 */
		private int timeout = Constant.DEFAULT_TIMEOUT;

		/** 重试次数 */
		private int retry = Constant.DEFAULT_RETRY;

		/** 重试间隔 */
		private long retryInterval = Constant.DEFAULT_RETRY_INTERVAL;

		/** 发送配置 */
		private Send send = new Send();

		/** 接收配置 */
		private Receive receive = new Receive();

		private MsgListener msgListener = new MsgListener();

		public void verify() {
			MatcherAssert.assertThat("id is null", this.id, CoreMatchers.notNullValue());
			MatcherAssert.assertThat("address is null", this.address, CoreMatchers.notNullValue());
			MatcherAssert.assertThat("port is null", this.port, CoreMatchers.notNullValue());
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public int getTimeout() {
			return timeout;
		}

		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}

		public int getRetry() {
			return retry;
		}

		public void setRetry(int retry) {
			this.retry = retry;
		}

		public long getRetryInterval() {
			return retryInterval;
		}

		public void setRetryInterval(long retryInterval) {
			this.retryInterval = retryInterval;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Send getSend() {
			return send;
		}

		public void setSend(Send send) {
			this.send = send;
		}

		public Receive getReceive() {
			return receive;
		}

		public void setReceive(Receive receive) {
			this.receive = receive;
		}

		public MsgListener getMsgListener() {
			return msgListener;
		}

		public void setMsgListener(MsgListener msgListener) {
			this.msgListener = msgListener;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
		}
	}

	/**
	 * 发送配置
	 */
	public static class Send {

		private String charset;

		private Integer bufferSize;

		/** 发送结束标志 */
		private String endFlag = Constant.DEFAULT_SEND_END_FLAG;

		public String getEndFlag() {
			return endFlag;
		}

		public void setEndFlag(String endFlag) {
			this.endFlag = endFlag;
		}

		public String getCharset() {
			return charset;
		}

		public void setCharset(String charset) {
			this.charset = charset;
		}

		public Integer getBufferSize() {
			return bufferSize;
		}

		public void setBufferSize(Integer bufferSize) {
			this.bufferSize = bufferSize;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		}
	}

	/**
	 * 接收配置
	 */
	public static class Receive {

		/** 接收字符集 */
		private String charset;

		/** 接收缓存 */
		private Integer bufferSize;

		/** sokcet接收字节处理器 */
		private String processor;

		public String getCharset() {
			return charset;
		}

		public void setCharset(String charset) {
			this.charset = charset;
		}

		public Integer getBufferSize() {
			return bufferSize;
		}

		public void setBufferSize(Integer bufferSize) {
			this.bufferSize = bufferSize;
		}

		public String getProcessor() {
			return processor;
		}

		public void setProcessor(String processor) {
			this.processor = processor;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		}
	}

	public static class MsgListener {

		/** 异步处理开关 */
		private boolean open = Constant.DEFAULT_MSG_LISTENNER_OPEN;

		/** 异步消息处理类 */
		private String listener;

		/** 异步消息处理最大线程线 */
		private int maxRunner = Constant.DEFAULT_MAX_RUNNER;

		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean open) {
			this.open = open;
		}

		public String getListener() {
			return listener;
		}

		public void setListener(String listener) {
			this.listener = listener;
		}

		public int getMaxRunner() {
			return maxRunner;
		}

		public void setMaxRunner(int maxRunner) {
			this.maxRunner = maxRunner;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		}
	}

	/**
	 * 连接池
	 */
	public static class Pool extends GenericObjectPoolConfig {

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		}
	}

	/**
	 * 心跳配置
	 */
	public static class Heartbeat {

		/** 心跳间隔 */
		private Integer interval;

		private String msg;

		public Integer getInterval() {
			return interval;
		}

		public void setInterval(Integer interval) {
			this.interval = interval;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		}

	}

	public static class SocketLongConfig extends SocketConfig {

		/** 心跳配置 */
		private Heartbeat heartbeat;

		private Pool pool = new Pool();

		public Heartbeat getHeartbeat() {
			return heartbeat;
		}

		public void setHeartbeat(Heartbeat heartbeat) {
			this.heartbeat = heartbeat;
		}

		public Pool getPool() {
			return pool;
		}

		public void setPool(Pool pool) {
			this.pool = pool;
		}

	}

	public static class SocketShortConfig extends SocketConfig {

	}
}
