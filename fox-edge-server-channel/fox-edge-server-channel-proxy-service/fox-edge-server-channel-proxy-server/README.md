# fox-edge-server-channel-proxy-server

#### 介绍
``` 
远程通道代理服务，它的作用是通过client/server的配合，在远程的A/B两台服务器之间建立一个管道，进行通道访问。
例如：
A服务器上没有串口，但是它想访问B服务器管理下的串口设备，那么可以在A服务器上启动client，在B服务器上启动server
那么A服务上的上层服务，就可以像访问本地串口服务下的设备一样，实际访问B服务器本地管理的串口服务下的设备。
``` 

#### 软件架构
``` 
A服务器<--------------------------------------------->B服务器
device-service
proxy-client                                       proxy-server<--->serial-service<---> device
mqtt-client<---------->阿里云BROKER<---------------->mqtt-client
``` 


#### 安装教程

``` 
1、客户端配置：代理串口服务
#系统参数
app_home=/opt/fox-edge

#redis参数
app_param_redis_host=localhost
app_param_redis_port=6379
app_param_redis_password=12345678
#mysql参数
app_param_mysql_host=localhost
app_param_mysql_username=fox-edge
app_param_mysql_password=12345678
app_param_mysql_endcode=?useUnicode=true&characterEncoding=utf8
#channel_prox参数
app_param_channel_type=serialport
app_param_channel_proxy=group_fox


#启动命令
nohup \
java -jar \
$app_home/bin/\
fox-edge-server-channel-proxy-client-1.0.0.jar \
-Dspring.profiles.active=prod \
--spring.redis.host=$app_param_redis_host --spring.redis.port=$app_param_redis_port --spring.redis.password=$app_param_redis_password \
--spring.channel_model.type=$app_param_channel_type --mqtt.client.topic.subscribe=/$app_param_channel_proxy/v1/proxy/response/# --mqtt.client.topic.publish=/$app_param_channel_proxy/v1/proxy/request/# \
>$app_home/logs/start_channel-proxy-client.out 2>&1 & \
> 

2、服务端配置：代理串口服务
#系统参数
app_home=/opt/fox-edge

#redis参数
app_param_redis_host=localhost
app_param_redis_port=6379
app_param_redis_password=12345678
#mysql参数
app_param_mysql_host=localhost
app_param_mysql_username=fox-edge
app_param_mysql_password=12345678
app_param_mysql_endcode=?useUnicode=true&characterEncoding=utf8
#channel_prox参数
app_param_channel_type=serialport
app_param_channel_proxy=group_fox


#启动命令
nohup \
java -jar \
$app_home/bin/\
fox-edge-server-channel-proxy-server-1.0.0.jar \
-Dspring.profiles.active=prod \
--spring.redis.host=$app_param_redis_host --spring.redis.port=$app_param_redis_port --spring.redis.password=$app_param_redis_password \
--mqtt.client.topic.subscribe=/$app_param_channel_proxy/v1/proxy/response/# --mqtt.client.topic.publish=/$app_param_channel_proxy/v1/proxy/request/# \
>$app_home/logs/start_channel-proxy-server.out 2>&1 & \
> 
``` 

#### 使用说明

#### 参与贡献


#### 特技
