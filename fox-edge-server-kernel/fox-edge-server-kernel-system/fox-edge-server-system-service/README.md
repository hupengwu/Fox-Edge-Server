# fox-edge-server-manager-system-service

#### 介绍

``` 
管理服务，可以监听来自云端的Mqtt消息，也可以监听redis消息，并转发给各个服务
``` 

#### 背景

``` 
边缘节点是被动响应的服务器，并且它的IP不是固定IP，端口也可能因为NAT而动态变化
这就导致了云端或者另外一个域的客户端如果想主动向它发起操作请求，那么可能连它在哪里都找不到
这样就需要边缘节点去某个固定的转发服务器主动去查询来自客户端的请求

同样，边缘节点的其他服务，也能够操作fox-edge，比如node-red通过redis与fox-edge组合成更丰富的边缘计算
此时，需要fox-edge提供redis消息接口
``` 

#### 方案

``` 
在云端侧服务器或者另外一个域的服务器，架设一个MqttBroker，替双方转发彼此的通信消息。
此时，在边缘节点上启动Receiverfuw，主动监听MqttBroker的消息，处理后，再回复消息回去。
为什么会选择MqttBroker，因为MQTT是一个面向物联网的轻量级消息队列，有较好的实时性。
``` 

#### mqtt范例1

``` 
1、查询system 通道查询API 接口
发送：/fox/proxy/c2e/BFEBFBFF000906A3/forward
{
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/kernel/manager/channel/page",
	"method": "post",
	"body": {
		"pageNum": 1,
		"pageSize": 2
	}
}
返回：/fox/proxy/e2c/forward/BFEBFBFF000906A3
{
    "topic": "/fox/proxy/c2e/BFEBFBFF000906A3/forward",
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/kernel/manager/channel/page",
	"method": "post",
	"body": {
		"msg": "操作成功",
		"code": 200,
		"data": {
			"total": 24,
			"list": [{
				"channelParam": {
					"ip": "127.0.0.1",
					"port": 102,
					"rack": 0,
					"slot": 1,
					"plcType": "S1200"
				},
				"createTime": 1696820977407,
				"channelName": "西门子-S7-PLC-1",
				"channelType": "s7plc",
				"updateTime": 1696907665211,
				"id": 59
			}, {
				"channelParam": {
					"host": "192.168.2.80",
					"port": 9528
				},
				"createTime": 1695801924643,
				"channelName": "192.168.2.80:9527",
				"channelType": "tcp-client",
				"updateTime": 1695801924643,
				"id": 58
			}]
		}
	},
	"msg": "",
	"code": 200
}

``` 

#### mqtt范例2

``` 
1、删除数据
发送：/fox/proxy/c2e/BFEBFBFF000906A3/forward
{
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/kernel/manager/device/entities?ids=2235",
	"method": "delete"
}
返回：/fox/proxy/e2c/forward/BFEBFBFF000906A3
{
  "topic": "/fox/proxy/c2e/BFEBFBFF000906A3/forward",
  "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
  "resource": "/kernel/manager/device/entities?ids=2235",
  "method": "delete",
  "body": {
    "msg": "操作成功",
    "code": 200
  },
  "msg": "",
  "code": 200
}

``` 


#### redis范例1

``` 
1、查询system 通道查询API 接口
发送：topic_manager_request_public
{
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/kernel/manager/channel/page",
	"method": "post",
	"body": {
		"pageNum": 1,
		"pageSize": 2
	}
}
返回：topic_manager_respond_public
{
    "topic": "/fox/proxy/c2e/BFEBFBFF000906A3/forward",
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"resource": "/kernel/manager/channel/page",
	"method": "post",
	"body": {
		"msg": "操作成功",
		"code": 200,
		"data": {
			"total": 24,
			"list": [{
				"channelParam": {
					"ip": "127.0.0.1",
					"port": 102,
					"rack": 0,
					"slot": 1,
					"plcType": "S1200"
				},
				"createTime": 1696820977407,
				"channelName": "西门子-S7-PLC-1",
				"channelType": "s7plc",
				"updateTime": 1696907665211,
				"id": 59
			}, {
				"channelParam": {
					"host": "192.168.2.80",
					"port": 9528
				},
				"createTime": 1695801924643,
				"channelName": "192.168.2.80:9527",
				"channelType": "tcp-client",
				"updateTime": 1695801924643,
				"id": 58
			}]
		}
	},
	"msg": "",
	"code": 200
}

``` 

#### 参与贡献



#### 特技
