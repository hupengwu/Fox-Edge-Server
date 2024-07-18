# fox-edge-server-persist-service

#### 介绍
``` 
持久化服务
它的作用是将控制器与设备服务之间的交互数据，保存到redis和数据库，简化控制器的逻辑，让控制器专注于控制
``` 

#### 软件架构
软件架构说明


#### 安装教程

1.  该服务职能在LINUX环境运行，所以需要远程调试
2.  远程调试的配置，不要参考简单网上的配置，因为linux的默认端口是环回端口，无法远程连接
3.  linux侧的启动命令，必须强制指明IP地址，例如:
java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=192.168.241.128:5005 fox-device-adapter-service-0.0.1.jar

#### 使用说明


#### 参与贡献



#### 特技
