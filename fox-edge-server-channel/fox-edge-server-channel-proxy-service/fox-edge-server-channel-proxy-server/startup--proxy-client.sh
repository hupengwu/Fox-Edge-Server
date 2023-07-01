#!/bin/bash

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


#切换当前目录
cd $app_home


#远端代理转发服务：串口服务
#启动命令
nohup \
java -jar \
$app_home/bin/\
fox-edge-server-channel-proxy-client-1.0.0.jar \
-Dspring.profiles.active=prod \
--spring.redis.host=$app_param_redis_host --spring.redis.port=$app_param_redis_port --spring.redis.password=$app_param_redis_password \
--spring.fox-service.model.name=$app_param_channel_type --mqtt.client.topic.subscribe=/$app_param_channel_proxy/v1/proxy/response/# --mqtt.client.topic.publish=/$app_param_channel_proxy/v1/proxy/request/# \
 >$app_home/logs/start_channel-proxy-client.out 2>&1 & \



