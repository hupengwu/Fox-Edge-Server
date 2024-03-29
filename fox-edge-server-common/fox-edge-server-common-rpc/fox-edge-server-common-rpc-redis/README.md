# fox-edge-server-common-rpc-redis

#### 介绍

``` 
管理服务，可以监听来自其他服务的redis消息，并转发给各个服务
``` 

#### redis范例1

``` 
1、查询system 通道查询API 接口
发送topic：fox.edge.list.manager.restful.message
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
