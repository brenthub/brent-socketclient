package cn.brent.socketclient.longc;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.pool2.impl.GenericObjectPool;

import cn.brent.socketclient.AbsSocketContext;
import cn.brent.socketclient.ISendHelper;
import cn.brent.socketclient.config.SocketConfigs.Pool;
import cn.brent.socketclient.config.SocketConfigs.SocketLongConfig;

public class SocketLongContext extends AbsSocketContext {

	/** 连接池 */
	protected GenericObjectPool<SocketWraper> socketPool;

	/** 发送助手 */
	protected final SendHelper sendHelper;

	/** 接收队列 */
	protected LinkedBlockingQueue<byte[]> receiveQueue = new LinkedBlockingQueue<byte[]>();

	/** 发送队列 */
	protected LinkedBlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<byte[]>();

	public static final int CHECK_RUN_INTERVAL = 5 * 60 * 1000;

	public SocketLongContext(SocketLongConfig config) {
		super(config);
		Pool pool = config.getPool();
		pool.setMaxIdle(pool.getMaxTotal());
		sendHelper = new SendHelper(this);
		socketPool = new GenericObjectPool<SocketWraper>(new SokectFactory(this), pool);
	}

	@Override
	public ISendHelper getSendHelper() {
		return sendHelper;
	}

	public GenericObjectPool<SocketWraper> getSocketPool() {
		return socketPool;
	}

	@Override
	public void startHandler() {
		// 开启发送任务
		startSendTask();

		if (config.getMsgListener().isOpen()) {
			// 开启消息回调任务
			startMsgProcessTask();
		} else {
			logger.info("Msg Listener is not open,skip startMsgProcessTask");
		}

		logger.info("socketLongContext start sucess");
	}

	@Override
	public void stopHandler() {
		logger.info("socketLongContext stop sucess");
	}

	/**
	 * 开启发送任务
	 */
	protected void startSendTask() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!runflag) {// 当停止时线程休眠5分钟
						try {
							Thread.sleep(CHECK_RUN_INTERVAL);
						} catch (InterruptedException e) {
						}
					}
					processSend();
				}
			}
		}).start();
		logger.info("SendTask start sucess...");
	}

	/**
	 * 处理发送队列
	 */
	protected void processSend() {
		SocketWraper socketWraper = null;
		try {
			byte[] msg = sendQueue.take();
			if (msg == null || msg.length == 0) {
				return;
			}
			logger.debug("send msg from Queue begin...");
			socketWraper = socketPool.borrowObject();
			try {
				socketWraper.sendMsg(msg);
				logger.debug("send msg from Queue sucess...");
			} catch (Exception e) {// 发送失败时，消息重新入队
				logger.error("sendMsg error", e);
				sendQueue.put(msg);
			}
		} catch (Exception e) {
			logger.error("processSend error", e);
		} finally {
			if (socketWraper != null) {
				socketPool.returnObject(socketWraper);
			}
		}
	}

	/**
	 * 开启异步消息处理任务
	 */
	protected void startMsgProcessTask() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!runflag) {// 当停止时线程休眠5分钟
						try {
							Thread.sleep(CHECK_RUN_INTERVAL);
						} catch (InterruptedException e) {
						}
					}
					processMsg();
				}
			}
		}).start();
		logger.info("MsgProcessTask start sucess...");
	}

	/**
	 * 异步消息处理
	 */
	protected void processMsg() {
		try {
			byte[] msg = receiveQueue.take();
			if (msg == null || msg.length == 0) {
				return;
			}
			logger.debug("process Msg from receiveQueue begin...");
			sendHelper.msgAsynProcess(msg);
			logger.debug("process Msg from receiveQueue success...");
		} catch (Exception e) {// 处理失败也算已经消费
			logger.error("processSend error", e);
		}
	}

	public LinkedBlockingQueue<byte[]> getSendQueue() {
		return sendQueue;
	}

	public LinkedBlockingQueue<byte[]> getReceiveQueue() {
		return receiveQueue;
	}

	public boolean isRunflag() {
		return runflag;
	}

}
