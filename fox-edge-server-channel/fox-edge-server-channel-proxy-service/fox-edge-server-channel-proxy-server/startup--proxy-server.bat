#如果是其他配对标识，那么修改group_fox为自己约定的标识
title fox-edge-server-channel-proxy-server-1.0.0.jar
java -jar fox-edge-server-channel-proxy-server-1.0.0.jar --mqtt.client.topics.topic1.subscribe=/group_fox/v1/proxy/request/# --mqtt.client.topics.topic1.publish=/group_fox/v1/proxy/response/#
