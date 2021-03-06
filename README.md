#brent-socketclient

socket 客户端组件

##说明
* socket 客户端组件
* 支持长连接，短连接，同步，异步发送消息
* 当长连接配置有消息侦听时，不允许发送同步消息


##配置文件示例
```xml
<?xml version="1.0" encoding="UTF-8"?>
<socket-configs xmlns="http://www.brent.cn/schema/socketclient" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.brent.cn/schema/socketclient http://www.brent.cn/schema/socketclient-1.0.xsd">
	
	<!-- 
	使用xsd进行xml校验和提示
	1、Window->Preferences->XML->XML Catalog->User Specified Entries窗口中,选择Add 按纽  
	2、找到工程内的socketclient-1.0.xsd，将key改成http://www.brent.cn/schema/socketclient-1.0.xsd
	3、重新打开xml文件就能看到提示
	 -->
	
	<socket-short id="test" address="127.0.0.1" port="9008" timeout="30000" retry="1" retryInterval="1000">
		<send charset="UTF-8" />
		<receive charset="UTF-8" processor="cn.brent.socketclient.processor.CloseStreamProcessor"/>
		<msglistener open="false" listener="server.socketshort.MsgProcess"/>
	</socket-short>
	
	<socket-long id="test2" address="127.0.0.1" port="9009" timeout="30000" retry="3" retryInterval="100">
		<heartbeat interval="3000" msg="000000" />
		<pool maxTotal="1"/>
		<send bufferSize="204800" charset="UTF-8"/>
		<receive bufferSize="204800" charset="UTF-8" processor="cn.brent.socketclient.processor.ReadLineProcessor" />
		<msglistener open="true" listener="server.socketshort.MsgProcess" maxRunner="100"/>
	</socket-long>
	
	<socket-long id="head" address="127.0.0.1" port="9010" timeout="30000" retry="3" retryInterval="100">
		<heartbeat interval="30000" msg="00000000" />
		<pool maxTotal="1" />
		<send bufferSize="204800" charset="UTF-8" />
		<receive bufferSize="204800" charset="UTF-8" processor="server.head.HeadProcessor" />
		<msglistener open="true" listener="server.socketshort.MsgProcess" maxRunner="100"/>
	</socket-long>
	
</socket-configs>
```
