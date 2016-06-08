package cn.brent.socketclient.longc;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class SokectFactory extends BasePooledObjectFactory<SocketWraper> {
	
	protected final SocketLongContext context;
	
	public SokectFactory(SocketLongContext context) {
		this.context=context;
	}

	@Override
	public SocketWraper create() throws Exception {
		return new SocketWraper(context);
	}

	@Override
	public PooledObject<SocketWraper> wrap(SocketWraper obj) {
		return new DefaultPooledObject<SocketWraper>(obj);
	}
	
}
