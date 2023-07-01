# fox-edge-server-channel-iec104-master

#### 介绍
``` 
背景：
IEC104规约是类似MODBUS的通信协议，它跟MODBUS一样，只定义了【操作】+【数据地址】的框架
各电力设备厂商，可以基于这种通信框架，定义自己的应用层通信协议

IEC104规约是层次性模型：TCP链路层/IEC104链路层/IEC104会话层/厂商应用层

IEC104规约的各级实体，各枚举、编码器、解码器的定义。
它提供了IEC104规约定义的数据结构处理能力，并不包含交互流程，目的是为了提高可复用性。
那么其他开发者可以基于core部件，自行开发主站/从站的通信交互流程
``` 
#### 软件架构
``` 
1、IEC104的通信协议层次
  Master                         Slaver
应   用   层<-------------------->应   用   层
IEC104会话层<-------------------->IEC104会话层
IEC104链路层<-------------------->IEC104链路层
TCP 链 路 层<-------------------->TCP 链 路 层

2、建议的开发部件
2.1 TCP 链 路 层
Netty，高性能TCP异步无阻塞通信开发框架

2.2 IEC104链路层
编码/解码器：fox-edge-server-protocol-iec104-core
链路管理器：fox-edge-server-common-utils-iec104-master
这两个部件会跟Slaver建立和维护iec104链路

2.3 IEC104会话层
会话框架：fox-edge-server-channel-iec104-master
这个部件会通过将上层的会话，打包成APDU实体，提交给iec104链路层转发

3、IEC104的会话方式
3.1 U帧/I帧/S帧的使用场景
U帧：使用在链路层的维护中，utils-iec104-master模块已经内部自动处理，应用层不用关心
S帧：使用在会话层的应答中
I帧：使用在应用层的发送/应答中

3.2 问答方式
一问一答：从应用层看，主站发送I帧，从站回复一个I帧或者S帧。（实际上框架会拦截处理）
一问多答：从应用层看，主站发送I帧，从站回复多个I帧或者S帧。

3.3 框架的要求
由于命令会有不同的回答方式，所以要求框架发送数据的时候，要指明等待从站如何回复结束报文
A、一问一答：主站I帧，从站一个S帧
这时候要求指明等待从站的S帧，那么Iec104ClientLinkerHandler.finishedRespond()在收到S帧就会通知结束处理
B、一问一答：主站I帧，从站一个I帧
这时候要求指明等待从站的I帧，那么Iec104ClientLinkerHandler.finishedRespond()在收到I帧就会通知结束处理
C、一问多答：主站I帧，从站多个I帧
这时候要求指明等待从站的I帧和结束原因，那么Iec104ClientLinkerHandler.finishedRespond()在收到I帧和指定结束原因就会通知结束处理
``` 
#### 使用说明
``` 
1、 报文范例
1.1、一问一答：主站I帧，从站一个S帧
关键就是指明："waitFrameType": "S_FORMAT"
{
	"name": "slaver1",
	"mode": "exchange",
	"uuid": "hupengwu",
	"send": {
		"control": {
			"type": "I_FORMAT",
			"cmd": null,
			"send": 4,
			"accept": 2
		},
		"asdu": {
			"typeId": 229,
			"vsq": {
				"sq": false,
				"num": 1
			},
			"cot": {
				"reason": 6,
				"reasonMsg": "激活",
				"test": false,
				"pn": true,
				"addr": 0
			},
			"commonAddress": 1,
			"data": "00 00 00 00 00 01 "
		},
        "waitFrameType": "S_FORMAT"
	},
	"timeout": 8000
}

1.2、一问一答：主站I帧，从站一个I帧
不需要指明等待信息：此时默认，"waitFrameType": "I_FORMAT"，"waitEndFlag": []
{
	"name": "slaver1",
	"mode": "exchange",
	"uuid": "hupengwu",
	"send": {
		"control": {
			"type": "I_FORMAT",
			"cmd": null,
			"send": 4,
			"accept": 2
		},
		"asdu": {
			"typeId": 100,
			"vsq": {
				"sq": false,
				"num": 0
			},
			"cot": {
				"reason": 6,
				"test": false,
				"pn": true,
				"addr": 0
			},
			"commonAddress": 1,
			"data": "00 00 00 14 "
		}
	},
	"timeout": 10000
}

1.3、一问多答：主站I帧，从站多个I帧
需要指明等待信息："waitEndFlag": [10]，此时默认"waitFrameType": "I_FORMAT"，
{
	"name": "slaver1",
	"mode": "exchange",
	"uuid": "hupengwu",
	"send": {
		"control": {
			"type": "I_FORMAT",
			"cmd": null,
			"send": 4,
			"accept": 2
		},
		"asdu": {
			"typeId": 100,
			"vsq": {
				"sq": false,
				"num": 0
			},
			"cot": {
				"reason": 6,
				"test": false,
				"pn": true,
				"addr": 0
			},
			"commonAddress": 1,
			"data": "00 00 00 14 "
		},
        "waitEndFlag": [10]
	},
	"timeout": 10000
}

``` 
