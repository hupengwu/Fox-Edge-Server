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
1、查询system 通道查询API 接口
发送：/fox/edge/proxy/BFEBFBFF000406E3/request
{
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/manager/system/channel/entities",
	"method": "get"
}
返回：/fox/edge/proxy/BFEBFBFF000406E3/respond
{
  "uuid" : "1b1df78266b9449c9d5705f821a2b4c1",
  "resource" : "/manager/system/channel/entities",
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
    }, {
      "createTime" : 1651843479555,
      "channelName" : "COM5",
      "channelType" : "forward",
      "updateTime" : 1651843479555,
      "id" : 18
    }, {
      "createTime" : 1651843479427,
      "channelName" : "channel-simulator",
      "channelType" : "simulator",
      "updateTime" : 1651843479427,
      "id" : 16
    }, {
      "createTime" : 1651843479358,
      "channelName" : "COM3",
      "channelType" : "serialport",
      "updateTime" : 1651843479358,
      "id" : 15
    }, {
      "createTime" : 1651843479595,
      "channelName" : "COM1",
      "channelType" : "forward",
      "updateTime" : 1651843479595,
      "id" : 19
    }, {
      "createTime" : 1651843479520,
      "channelName" : "COM9",
      "channelType" : "forward",
      "updateTime" : 1651843479520,
      "id" : 17
    } ]
  },
  "msg" : "",
  "code" : 200
}

2、查询system 通道查询API 接口
发送：/fox/edge/proxy/BFEBFBFF000906A3/request
{
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/manager/system/device/value/page",
	"method": "post",
	"body": {
		"pageNum": 1,
		"pageSize": 10
	}
}

返回：/fox/edge/proxy/BFEBFBFF000906A3/respond
{
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/manager/system/device/value/page",
	"method": "post",
	"body": {
		"msg": "操作成功",
		"code": 200,
		"data": {
			"total": 95606,
			"list": [{
				"deviceType": "ZXDU58",
				"createTime": 1670924863349,
				"objectName": "模块02输出电流",
				"updateTime": 1676632838084,
				"objectValue": 3.9799995,
				"objectTime": 2329,
				"id": 532131,
				"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备"
			}, {
				"deviceType": "ZXDU58",
				"createTime": 1670924864924,
				"objectName": "模块07输出电流",
				"updateTime": 1676632838084,
				"objectValue": 3.8999999,
				"objectTime": 2329,
				"id": 532132,
				"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备"
			}, {
				"deviceType": "ZXDU58",
				"createTime": 1670924865007,
				"objectName": "模块04输出电流",
				"updateTime": 1676632838084,
				"objectValue": 3.9799995,
				"objectTime": 2329,
				"id": 532133,
				"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备"
			}, {
				"deviceType": "ZXDU58",
				"createTime": 1670924865071,
				"objectName": "模块12输出电流",
				"updateTime": 1676632838084,
				"objectValue": 0.0,
				"objectTime": 2329,
				"id": 532134,
				"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备"
			}, {
				"deviceType": "ZXDU58",
				"createTime": 1670924865113,
				"objectName": "模块06输出电流",
				"updateTime": 1676632838084,
				"objectValue": 4.0,
				"objectTime": 2329,
				"id": 532135,
				"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备"
			}, {
				"deviceType": "ZXDU58",
				"createTime": 1670924865161,
				"objectName": "模块10输出电流",
				"updateTime": 1676632838084,
				"objectValue": 0.0,
				"objectTime": 2329,
				"id": 532136,
				"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备"
			}, {
				"deviceType": "ZXDU58",
				"createTime": 1670924865202,
				"objectName": "模块08输出电流",
				"updateTime": 1676632838084,
				"objectValue": 0.0,
				"objectTime": 2329,
				"id": 532137,
				"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备"
			}, {
				"deviceType": "ZXDU58",
				"createTime": 1670924865232,
				"objectName": "整流模块数量",
				"updateTime": 1676632838084,
				"objectValue": 12,
				"objectTime": 2329,
				"id": 532138,
				"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备"
			}, {
				"deviceType": "ZXDU58",
				"createTime": 1670924865308,
				"objectName": "模块03输出电流",
				"updateTime": 1676632838084,
				"objectValue": 3.8199997,
				"objectTime": 2329,
				"id": 532139,
				"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备"
			}, {
				"deviceType": "ZXDU58",
				"createTime": 1670924865405,
				"objectName": "模块01输出电流",
				"updateTime": 1676632838084,
				"objectValue": 3.76,
				"objectTime": 2329,
				"id": 532140,
				"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备"
			}]
		}
	},
	"msg": "",
	"code": 200
}

``` 

#### 参与贡献



#### 特技
