package cn.brent.socketclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.brent.socketclient.config.SocketConfigs.SocketConfig;

public abstract class AbsSendHelper implements ISendHelper {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected SocketConfig config;

	protected IMsgListener listener;

	protected IProcessor byteProcessor;

	/** 异步处理线程池 */
	protected static Executor executor;

	public AbsSendHelper(SocketConfig config) {
		this.config = config;

		if (StringUtils.isNotEmpty(config.getReceive().getProcessor())) {
			initProcessor();
		} else {
			throw new RuntimeException("processor is null");
		}

		if (config.getMsgListener().isOpen()&&StringUtils.isNotEmpty(config.getMsgListener().getListener())) {
			initListener();
		}

	}

	/**
	 * 初始化接收消息处理者
	 */
	protected void initProcessor() {
		try {
			this.byteProcessor = (IProcessor) Class.forName(config.getReceive().getProcessor()).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 初始化消息侦听
	 */
	protected void initListener() {
		try {
			this.listener = (IMsgListener) Class.forName(config.getMsgListener().getListener()).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		int max = config.getMsgListener().getMaxRunner();

		// 满队列后等待
		executor = new ThreadPoolExecutor(max, max, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(max * 10), new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				if (!executor.isShutdown()) {
					try {
						executor.getQueue().put(r);
					} catch (InterruptedException e) {
					}
				}
			}
		});
	}

	@Override
	public String send(String msg) {
		try {
			String sendCharset = config.getSend().getCharset();
			String revCharset = config.getSend().getCharset();
			if (StringUtils.isEmpty(sendCharset) || StringUtils.isEmpty(revCharset)) {
				throw new SendException("charsetError", "not set sendCharset or revCharset");
			}
			byte[] bs = send(msg.getBytes(sendCharset));
			return new String(bs, revCharset);
		} catch (UnsupportedEncodingException e) {
			throw new SendException("unsupportedEncoding", e);
		}
	}

	/**
	 * 异步消息处理
	 * 
	 * @param so
	 */
	public void msgAsynProcess(final byte[] bmsg) {
		if (listener == null) {
			return;
		}
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String msg = null;
					String revCharset = config.getReceive().getCharset();
					if (revCharset != null) {
						msg = new String(bmsg, revCharset);
					}
					listener.recieveMessages(msg, bmsg);
				} catch (Exception e) {
					logger.error("rev msg or run listener error", e);
				}
			}
		});
	}

	/**
	 * 读取
	 * 
	 * @param so
	 * @return
	 * @throws IOException
	 */
	public byte[] readByte(Socket so) throws IOException {
		InputStream is = so.getInputStream();
		return byteProcessor.fullMsg(is);
	}

	/**
	 * 发送
	 * 
	 * @param so
	 * @param msg
	 * @throws IOException
	 */
	public void sendByte(Socket so, byte[] msg) throws IOException {
		OutputStream os = so.getOutputStream();
		os.write(msg);
		os.write(config.getSend().getEndFlag().getBytes());
		os.flush();
	}

	/**
	 * 连接服务端
	 * 
	 * @param so
	 */
	public synchronized void connect(Socket so) {
		
		if(so.isConnected()){
			return;
		}

		SocketAddress address = new InetSocketAddress(config.getAddress(), config.getPort());

		try {
			// socket参数设置
			socketParamSet(so);

			// 连接服务器
			so.connect(address, config.getTimeout());

		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("connected fail :" + e.getMessage());
		}
		// 重试
		int i = 0;
		while (!so.isConnected() && i < config.getRetry()) {
			try {
				Thread.sleep(config.getRetryInterval());
				so.connect(address, config.getTimeout());
			} catch (Exception e) {
				logger.warn("retry connected [times:" + (i + 1) + "] fail:" + e.getMessage());
			} finally {
				i++;
			}
		}
		if (!so.isConnected()) {
			throw new SendException("timeout", "can't connected the target adress:" + address);
		}
	}

	/**
	 * socket参数设置
	 * 
	 * @param socket
	 * @throws SocketException
	 */
	protected void socketParamSet(Socket socket) throws SocketException {

		Integer sendBufferSize = config.getSend().getBufferSize();
		Integer revBufferSize = config.getReceive().getBufferSize();

		if (sendBufferSize != null) {
			socket.setSendBufferSize(sendBufferSize);
		}

		if (revBufferSize != null) {
			socket.setReceiveBufferSize(revBufferSize);
		}
	}

	@Override
	public void sendAsyn(String msg) {
		try {
			String sendCharset = config.getSend().getCharset();
			if (StringUtils.isEmpty(sendCharset)) {
				throw new SendException("charsetError", "not set sendCharset");
			}
			sendAsyn(msg.getBytes(sendCharset));
		} catch (UnsupportedEncodingException e) {
			throw new SendException("unsupportedEncoding", e);
		}
	}

}
