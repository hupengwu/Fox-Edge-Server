# fox-edge-server-persist-common

#### 介绍
``` 
持久化服务
它为设备服务、控制器服务提供保存数据的服务，这样设备服务、控制器服务就可以在内存中高速运行，不需要关注数据的保存。
``` 
#### 软件架构
``` 
持久化服务在redis上订阅了topic_persist_request_public的topic消息，设备服务、控制器服务会将它们需要保存的数据，
发送到这个topic上，那么持久化服务数据会对来自这个接口的消息进行数据的保存。

数据结构为TaskRespondVO格式，该格式包括
1）设备的通信状态，保存在redis中
2）设备的状态数据，保存在redis中
3）设备的记录数据，保存在mysql中
4）用户的操作记录，保存在mysql中
5）设备的历史数据，保存在mysql中
``` 

#### 特性描述

``` 
Topic描述
1. 北向旁路式API：topic_persist_request_public
1.1 操作类数据
操作结果数据：record，根据该标识，会记录到mysql的tb_operate_record表中
设备记录数据：data/value/record，会记录到mysql的tb_device_record表中
设备状态数据：data/value/status，会记录到redis的fox.edge.entity.ObjectEntity和fox.edge.entity.DeviceValueEntity中
历史记录数据：data/value/status中的数字类数据，会记录到mysql的tb_history表中

``` 

#### 问题定位
``` 
#在linux下输入命令
redis-cli -h 127.0.0.1 -p 6379
>auth 12345678
>subscribe topic_persist_request_public

``` 
