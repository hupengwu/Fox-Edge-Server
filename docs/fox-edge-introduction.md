# 项目介绍

## 文件结构

  ```text
	─fox-edge								// 系统目录		
		├─bin								// 程序目录
		│  └─kernel							// kernel级程序
		│      ├─gateway-service			// 网关服务
		│      └─manager-service			// 管理服务
		├─conf								// 服务配置
		├─dist								// nginx脚本
		├─doc								// 文档目录
		├─jar								// jar组件目录
		│  ├─decoder						// 设备解码器
		│  └─trigger						// 触发器解码器
		├─logs								// 日志目录
		├─repository						// 本地仓库
		│  ├─decoder						// 解码器仓库
		│  └─service						// 服务仓库	
		├─shell								// 启动脚本
		│  ├─kernel							// kernel服务启动配置
		│  │  ├─gateway-service				// 网关启动配置
		│  │  └─manager-service				// 管理服务启动配置
		│  └─tools							// 工具脚本
		├─sql								// mysql初始化脚本
		└─template 							// 解码器模板目录
	
  ```

## 配置文件
  目录：`/opt/fox-edge/shell`

- 服务配置文件 `fox-edge.ini` <br>
  ```ini
	[environment]
	ip=192.168.3.133
	
	[redis]
	host=localhost
	
	port=6379
	password=12345678
	
	[mysql]
	host=localhost	
	username=fox-edge
	password=12345678
	
  ```
  
- 服务配置文件 `/opt/fox-edge/shell/[业务级别]/[业务名称]/service.conf` <br>
	例如：`/opt/fox-edge/shell/service/channel-proxy-server/service.conf`
  ```conf
    # 业务级别
	appType=service
	
	# 业务名称
	appName=channel-proxy-server
	
	# 程序文件
	jarName=fox-edge-server-channel-proxy-server-1.0.0.jar
	
	# 用户的spring启动参数，注意用双引号""包起来，因为spring参数的格式与linux的shell格式定义冲突
	springParam="--spring.fox-service.model.name=serialport --mqtt.client.ip=39.108.137.38 --mqtt.client.topic.subscribe=/group_fox/v1/proxy/request/# --mqtt.client.topic.publish=/group_fox/v1/proxy/response/#"
  ```
  
## 启动脚本
  目录：`/opt/fox-edge/shell`

- 启动脚本  `startup.sh` 
  ```sh
	# 该脚本用来启动整个Fox-Edge应用系统
	./startup.sh
	
	# 启动结果可以通过下面这条命令来检查
	ps -aux|grep fox-edge
	```
	
- 停止脚本  `shutdown.sh` 
  ```sh
	# 该脚本用来关闭整个Fox-Edge应用系统
	./shutdown.sh
	```
	
	
- 重启服务脚本  `restart.sh` 
  ```sh
	# 该脚本用来重启某个服务，例如重启kernel级的manager-service服务，服务端口为9123
	./restart.sh kernel/manager-service -p9123
	
	# 例如重启kernel级的manager-service服务，远程调试端口为192.168.3.133:5005 ，服务端口为9123
	./restart.sh kernel/manager-service -d192.168.3.133:5005 -p9123
	```
	
- 开机启动脚本  `enable-service.sh` 
  ```sh
	# 该脚本用配置Fox-Edge为开机启动方式，那么重启设备后，将开机启动
	./enable-service.sh
	```