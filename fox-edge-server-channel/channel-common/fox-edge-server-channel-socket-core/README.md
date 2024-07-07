# fox-edge-server-channel-common

#### 介绍
channel服务的公共模块

#### 工作流程

``` 
1. channel服务启动阶段：
1.1 会在redis的fox.edge.entity.ChannelEntity中去加载自己相关的配置数据;
1.2 会在redis上订阅topic_channel_request_XXXX和topic_channel_request_manager，接收客户端请求
    其中XXXX为服务类型，比如topic_channel_request_serialport
1.3 会启动ChannelRedisScheduler线程，后续会从redis读取配置数据和主题数据
1.4 会启动RedisTopicPuberService线程，后续会向redis发送topic_channel_respond_device主题数据
    
2. channel在运行阶段
2.1 监听redis的fox.edge.entity.ChannelEntity上的配置变更，并触发配置变更动作
2.2 监听redis的topic_channel_request_XXXX的消息，处理后返回topic_channel_respond_device
2.3 监听redis的topic_channel_request_manager消息，处理后返回topic_channel_respond_manager

``` 
#### API接口

``` 
1. redis配置接口：
key: fox.edge.entity.ChannelEntity
value:
{
  "id": 15,
  "createTime": 1651843479358,
  "updateTime": 1664597030402,
  "channelName": "COM3",
  "channelType": "serialport",
  "channelParam": {
    "parity": "E",
    "databits": 8,
    "stopbits": 1,
    "baud_rate": 2440,
    "serial_name": "COM3"
  }
}

2. 报文发送请求接口：
接收Topic：
topic_channel_request_simulator
报文：
{
	"name": "channel-simulator",
    "mode": "exchange",
    "uuid": "hupengwu",
	"send": "22 72 04 03 00 00 01 00 5c f3   ",    
	"timeout": 3000
}
返回Topic：
topic_channel_respond_device
报文：
{
    "type": "simulator",
    "uuid": "hupengwu",
    "name": "channel-simulator",
    "mode": "exchange",
    "send": "22 72 04 03 00 00 01 00 5c f3   ",
    "recv": "22 72 04 03 00 00 81 0F 47 46 44 2D 32 32 37 32 30 34 30 00 00 24 24 66 CD ",
    "timeout": 3000,
    "msg": "",
    "code": 200
}

3. 管理发送请求接口：
接收Topic：
topic_channel_request_manager
报文：
返回Topic：
topic_channel_respond_manager
报文：

4. redis进程状态通告接口：
key: fox.edge.status
value: 
{
  "modelName": "fox-edge-server-channel-udpsocket-service",
  "activeTime": 1664764338973,
  "modelType": "channel"
}

``` 
#### 使用说明


#### 参与贡献


#### 特技
