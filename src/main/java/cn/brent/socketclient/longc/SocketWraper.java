package cn.brent.socketclient.longc;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.socketclient.SendException;
import cn.brent.socketclient.config.SocketConfigs.SocketLongConfig;

public class SocketWraper {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/** socket */
	protected Socket socket;

	/** 消息发送助手 */
	protected SendHelper sendHelper;

	/** 上一次心跳时间 */
	protected long lastActiveTime = System.currentTimeMillis();

	/** 心跳间隔 */
	protected Integer heartbeatInterval;

	/** 心跳报文 */
	protected byte[] heartMsg;

	/** context */
	protected SocketLongContext context;

	public SocketWraper(SocketLongContext context) {
		this.socket = new Socket();
		this.sendHelper = (SendHelper) context.getSendHelper();
		this.context = context;
		SocketLongConfig config = (SocketLongConfig) context.getConfig();
		if (config.getHeartbeat() != null && config.getHeartbeat().getMsg() != null) {
			this.heartbeatInterval = config.getHeartbeat().getInterval();
			this.heartMsg = config.getHeartbeat().getMsg().getBytes();
		}

		if (heartMsg != null && heartbeatInterval != null) {
			startHeartbeat();
		}
		
		// 开启接收消息任务
		if(config.getMsgListener().isOpen()){
			startRevMsg();
		}else{
			logger.info("Msg Listener is not open, skip startRevMsg..");
		}
	}

	/**
	 * 发送心跳
	 */
	protected void heartbeat() {
		if (!socket.isConnected()) {
			sendHelper.connect(socket);
		}
		if (System.currentTimeMillis() - lastActiveTime > heartbeatInterval) {
			lastActiveTime = System.currentTimeMillis();
			try {
				logger.debug("send heartbeat begin...");
				sendHelper.sendByte(socket, heartMsg);
				logger.debug("send heartbeat success...");
			} catch (IOException e) {
				logger.error("send heartbeat error", e);
			}
		}
	}

	/**
	 * 接收消息
	 * 
	 * @return
	 */
	public synchronized byte[] receiveMsg() {
		if (!socket.isConnected()) {
			sendHelper.connect(socket);
		}
		try {
			return sendHelper.readByte(socket);
		} catch (IOException e) {
			logger.error("receiveMessage error", e);
			return null;
		}
	}

	/**
	 * 接收消息任务
	 */
	private void receiveMsgTask() {
		logger.debug("receiveMsg begin");
		byte[] msg = receiveMsg();
		logger.debug("receiveMsg success...");
		try {
			context.getReceiveQueue().put(msg);
		} catch (InterruptedException e) {
			logger.error("receiveMsgTask error", e);
		}
	}

	/**
	 * 发送消息
	 */
	public void sendMsg(byte[] msg) {
		if (msg == null || msg.length == 0) {
			return;
		}
		if (!socket.isConnected()) {
			sendHelper.connect(socket);
		}
		try {
			sendHelper.sendByte(this.socket, msg);
		} catch (IOException e) {
			throw new SendException("sendError", e);
		}
	}

	/**
	 * 开启心跳
	 */
	protected void startHeartbeat() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!context.isRunflag()) {// 当停止时线程休眠5分钟
						try {
							Thread.sleep(SocketLongContext.CHECK_RUN_INTERVAL);
						} catch (InterruptedException e) {
						}
					}
					heartbeat();
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		logger.info("start Heartbeat success...");
	}

	/**
	 * 开启消息接收任务
	 */
	protected void startRevMsg() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!context.isRunflag()) {// 当停止时线程休眠5分钟
						try {
							Thread.sleep(SocketLongContext.CHECK_RUN_INTERVAL);
						} catch (InterruptedException e) {
						}
					}
					receiveMsgTask();
				}
			}
		}).start();
		logger.info("start RevMsg task success...");
	}

	public Socket getSocket() {
		return socket;
	}

}
