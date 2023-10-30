# fox-edge-server-receiver-service

#### 介绍

``` 
云端接收器
它的作用是监听来自云端的Mqtt消息，并转发给各个服务
``` 

#### 背景

``` 
边缘节点是被动响应的服务器，并且它的IP不是固定IP，端口也可能因为NAT而动态变化
这就导致了云端或者另外一个域的客户端如果想主动向它发起操作请求，那么可能连它在哪里都找不到
这样就需要边缘节点去某个固定的转发服务器主动去查询来自客户端的请求
``` 

#### 方案

``` 
在云端侧服务器或者另外一个域的服务器，架设一个MqttBroker，替双方转发彼此的通信消息。
此时，在边缘节点上启动Receiverfuw，主动监听MqttBroker的消息，处理后，再回复消息回去。
为什么会选择MqttBroker，因为MQTT是一个面向物联网的轻量级消息队列，有较好的实时性。
``` 

#### 软件架构

软件架构说明

#### 安装教程


#### 使用说明

``` 
1、向system服务 查询API 接口
说明：它是直接转发到system服务器的resetful API，所以具体API可以查阅文档system的resetful API
发送：/fox/edge/proxy/BFEBFBFF000406E3/request
{
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/manager/system/channel/entity",
	"method": "get"
}
返回：/fox/edge/proxy/BFEBFBFF000406E3/respond
{
  "uuid" : "1b1df78266b9449c9d5705f821a2b4c1",
  "resource" : "/manager/system/channel/entity",
  "method" : "get",
  "body" : {
    "msg" : "操作成功",
    "code" : 200,
    "data" : [ {
      "createTime" : 1651843479427,
      "channelName" : "slaver1",
      "channelType" : "iec104-master",
      "updateTime" : 1651843479427,
      "id" : 14
    }, {
      "createTime" : 1651843479427,
      "channelName" : "/v1/device/request/12345",
      "channelType" : "mqtt_client",
      "updateTime" : 1651843479427,
      "id" : 20
    }]
  },
  "msg" : "",
  "code" : 200
}

2、向channel服务 查询API 接口
说明：它是直接转发到channel服务器的topic API，所以具体API可以查阅文档channel的topic API
发送：/fox/edge/proxy/BFEBFBFF000406E3/request
{
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/channel/topic_channel_request_serialport",
	"method": "post",
	"body": {
		"name": "COM3",
		"mode": "exchange",
		"send": "fe 68 11 11 11 53 12 35 68 01 02 43 c3 a6 16",
		"timeout": 2000
	}
}
返回：/fox/edge/proxy/BFEBFBFF000406E3/respond
{
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/channel/topic_channel_request_serialport",
	"method": "post",
	"body": {
		"type": "serialport",
		"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
		"name": "COM3",
		"mode": "exchange",
		"send": "fe 68 11 11 11 53 12 35 68 01 02 43 c3 a6 16",
		"recv": "fe fe fe fe 68 11 11 11 53 12 35 68 81 07 43 c3 87 37 33 33 34 83 16 ",
		"timeout": 2000,
		"msg": "",
		"code": 200
	},
	"msg": "",
	"code": 200
}

3、向device服务 查询API 接口
说明：它是直接转发到device服务器的topic API，所以具体API可以查阅文档device的topic API
发送：/fox/edge/proxy/BFEBFBFF000406E3/request
{
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/device/topic_device_request_public",
	"method": "post",
	"body": {
		"operate": "exchange",
		"deviceName": "ModBus11",
		"operateName": "Read Holding Register",
		"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
		"param": {
			"template_name": "Read System Measures Table",
			"device_addr": 1,
			"reg_addr": "04 2E",
			"reg_cnt": 69,
			"modbus_mode": "RTU",
			"operate_name": "Read Holding Register",
			"table_name": "101.CETUPS_Read System Measures Table.csv"
		},
		"timeout": 2000
	}
}
返回：/fox/edge/proxy/BFEBFBFF000406E3/respond
{
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/device/topic_device_request_public",
	"method": "post",
	"body": {
		"deviceType": "ModBus Device",
		"msg": "",
		"code": 200,
		"operate": "exchange",
		"data": {
			"commStatus": {
				"commSuccessTime": 1663157016596,
				"commFailedCount": 0,
				"commFailedTime": 0
			},
			"value": {
				"status": {
					"逆变器06输出功率": 1,
					"逆变器12输出电流": 0.0,
					"系统输出功率": 121,
					"逆变器15输出功率": 0,
					"逆变器14输出功率": 0,
					"逆变器03输出电流": 1.5,
					"逆变器05输出电流": 0.1,
					"逆变器16输出功率": 0,
					"逆变器15温度": 0,
					"逆变器07输出功率": 0,
					"逆变器13温度": 0,
					"逆变器11温度": 0,
					"逆变器13输出电流": 0.0,
					"逆变器04输出电流": 0.0,
					"逆变器06输出电流": 0.0,
					"逆变器04输出功率": 0,
					"逆变器10输出电流": 0.0,
					"逆变器07输出电流": 0.0,
					"逆变器05输出功率": 0,
					"逆变器11输出电流": 0.0,
					"逆变器09温度": 0,
					"逆变器07温度": 0,
					"逆变器05温度": 0,
					"逆变器03温度": 0,
					"逆变器01温度": 0,
					"逆变器01输出功率": 48,
					"逆变器09输出电流": 0.0,
					"逆变器10输出功率": 0,
					"组2输入电压": 0.0,
					"逆变器16输出电流": 0.0,
					"逆变器11输出功率": 0,
					"逆变器03输出功率": 25,
					"逆变器02输出功率": 48,
					"逆变器16温度": 0,
					"逆变器08输出电流": 0.0,
					"逆变器14温度": 0,
					"系统输出电压": 229,
					"逆变器12温度": 0,
					"逆变器10温度": 0,
					"组1输入电压": 2.9,
					"逆变器12输出功率": 0,
					"逆变器08输出功率": 0,
					"逆变器14输出电流": 0.0,
					"系统输出电流": 5.3,
					"组4输入电压": 0.0,
					"逆变器01输出电流": 1.7,
					"组3输入电压": 0.0,
					"逆变器08温度": 0,
					"逆变器13输出功率": 0,
					"负载比": 9,
					"逆变器06温度": 0,
					"逆变器04温度": 0,
					"逆变器02温度": 59,
					"逆变器09输出功率": 0,
					"系统输出频率": 24.4,
					"逆变器15输出电流": 0.0,
					"逆变器02输出电流": 2.1000001
				}
			}
		},
		"clientName": "proxy4cloud2topic",
		"param": {
			"template_name": "Read System Measures Table",
			"device_addr": 1,
			"reg_addr": "04 2E",
			"reg_cnt": 69,
			"modbus_mode": "RTU",
			"operate_name": "Read Holding Register",
			"table_name": "101.CETUPS_Read System Measures Table.csv",
			"ADDR": 1,
			"REG_ADDR": 1070,
			"REG_CNT": 69,
			"mode": "RTU"
		},
		"operateName": "Read Holding Register",
		"deviceName": "ModBus11",
		"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
		"timeout": 2000
	},
	"msg": "",
	"code": 200
}
``` 

#### 参与贡献



#### 特技
