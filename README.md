#brent-socketclient

socket 客户端组件

##说明
* socket 客户端组件
* 支持长连接，短连接，同步，异步发送消息
* 当长连接配置有消息侦听时，不允许发送同步消息


##配置文件示例
```xml
<?xml version="1.0" encoding="UTF-8"?>
<socket-configs>
	<!-- 
	配置说明：
	【socket-short|socket-long】连接配置
	id ： 唯一标识
	address ：socket服务端地址
	port ： sokcet服务端端口
	timeout ：连接超时时间
	retry ：连接失败时重试的次数
	retryInterval ：两次重试之间的间隔
	
	【send】发送配置
	charset ： 发送时的编码
	bufferSize ： 发送缓存设置
	
	【receive】接收配置
	charset ： 接收编码
	bufferSize ： 接收缓存设置
	processor ： 接收时完整消息的识别 （用于从字节流分隔消息）
	可选项：
		cn.brent.socketclient.processor.CloseStreamProcessor 对方关闭连接（用于短连接）
		cn.brent.socketclient.processor.processor.ReadLineProcessor 以换行符作为消息分隔符
	可选项中没有的，自行定义消息格式
	
	【msglistener】异步消息侦听
	open ： 异步开关
	listener ： 异步消息侦听处理器
	maxRunner : 异步线程池最大个数
	
	
	【heartbeat】长连接心跳
	interval ： 心跳间隔
	msg ： 心跳消息内容
	
	【pool】 连接池
	maxTotal ： socket client最大连接个数
	其它和普通连接池配置一个，不一一说明
	
	 -->
	<socket-short id="test" address="127.0.0.1" port="9008" timeout="30000" retry="1" retryInterval="1000">
		<send charset="UTF-8"/>
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
		<pool minIdle="1" maxTotal="1"/>
		<send bufferSize="204800" charset="UTF-8" />
		<receive bufferSize="204800" charset="UTF-8" processor="server.head.HeadProcessor" />
		<msglistener open="true" listener="server.socketshort.MsgProcess" maxRunner="100"/>
	</socket-long>
	
</socket-configs>
```
