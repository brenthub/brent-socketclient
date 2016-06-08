package cn.brent.socketclient;

/**
 * 发送 EXception
 */
public class SendException extends RuntimeException {

	/** */
	private static final long serialVersionUID = 1L;
	
	protected String errorCode;

	public SendException(String errorCode) {
		super();
		this.errorCode=errorCode;
	}
	
	public SendException(String errorCode,String message) {
		super(message);
		this.errorCode=errorCode;
	}
	
	public SendException(String errorCode,String message, Throwable cause) {
		super(message, cause);
		this.errorCode=errorCode;
	}

	public SendException(String errorCode,Throwable cause) {
		super(cause);
		this.errorCode=errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
	
}
