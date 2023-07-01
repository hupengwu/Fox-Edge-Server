# 环境部署

## 事前准备工作
###### 硬件环境

  ```
	准备一台x86工控机，最小配置（4G内存、64G存储空间）。
	如只是进行开发和测试，也可用虚拟机或者个人电脑进行替代性。

  ```

###### 操作系统

  ```
	Linux操作系统
	推荐Ubuntu Server 20.04 LTS版本，或者Ubuntu Server 22.04 LTS版本

  ```

###### 基础软件

  ```
	1、JAVA：openjdk-11-jdk
	2、MySql：8.0以上版本
	3、Redis：6.0以上版本
	4、nginx：1.18以上版本
	5、ufw：0.36.1以上版本

  ```

###### Fox-Edge系统
   请到[灵狐官网](http://www.fox-tech.cn/)的页面下载Fox-Edge最新的初始安装包

  ```	
	http://www.fox-tech.cn/repository/fox-edge-server/download/fox-edge-1.0.4.tar.gz

  ```
## 基础软件安装
下面以Ubuntu 20.04
  
- 基础软件 dos2unix

  ```sh
	# dos2unix工具：windows环境下编辑的sh文件，经常会因为文件格式不同导致无法运行，此时需要使用dos2unix进行格式转
	# 换处理，否则会出现/bin/bash^M: bad interpreter: No such file or directory的错误，导致无法执行
	
    apt install dos2unix -y

  ```
  
- 基础软件 openssh

  ```sh
	# 更新软件
	apt-get update   
	
	# 安装openssh
	apt install openssh-server -y
	
	#修改root密码
	passwd root
	
	#允许root远程登录
	vim /etc/ssh/sshd_config
	#将sshd_config文件中的PermitRootLogin prohibit-password修改为PermitRootLogin yes

	#重启ssh
	service ssh restart

  ```
  
- 基础软件 vsftpd

  ```sh
	
	# 更新软件
	apt-get update    
	
	# 安装vsftpd
	apt-get install vsftpd -y
 
	# 设置开机启动并启动ftp服务
	systemctl enable vsftpd
	systemctl start vsftpd

	#查看其运行状态
	systemctl  status vsftpd
	
	#重启服务
	systemctl  restart vsftpd

  ```
  
- 基础软件 java

  ```sh
	
	# 更新软件
	apt-get update    
	
	# W
	apt-get install openjdk-11-jdk -y
	
	#查看安装状态
	java -version
	
	#安装jmap:fox-edge运行阶段，会使用JMAP进行GC操作
	apt install openjdk-11-jdk-headless -y
	
	#检查jmap是否安装成功
	jmap
  ```
  
- 基础软件 mysql8.0

  ```sh
	
	#更新源
	apt-get update  
	
	#安装
	apt-get install mysql-server -y
	
	#验证
	systemctl status mysql

	#修改配置文件:bind-address = 127.0.0.1修改为bind-address = 0.0.0.0
	#关闭binlog日志：在末尾添加 skip-log-bin ，登录后可以用show variables like '%log_bin%%';查询log_bin变为off
	vim /etc/mysql/mysql.conf.d/mysqld.cnf 
	#

	#重启mysql
	systemctl restart mysql
	
	#验证mysql
	systemctl status mysql
  ```
  
- 基础软件 创建mysql账号

  ```sh
	
	# -u 指定用户名 -p需要输入密码  回车输入密码
	mysql -u root -p  

	#后面是进入mysql后的操作

	#查看用户权限
	mysql> 
	use mysql;
	select host, user, plugin from user;

	#创建用户:'root'@'%'
	create user 'root'@'%' identified by '12345678';
	grant all privileges on *.* to 'root'@'%';
	flush privileges;
	exit;
  ```

- 基础软件 redis

  ```sh
	
	#更新
	apt-get update

	#安装redis
	apt install redis-server -y

	#检查安装结果
	systemctl status redis-server

	#修改配置文件
	vim /etc/redis/redis.conf
	#1.注释掉 bind 127.0.0.1 ::1   位置在69行左右
	#2.修改protected-mode为no      位置在88行左右
	#3.修改requirepass为12345678   位置在507行左右

	#重启redis
	systemctl restart redis-server
  ```
  
- ubuntu的磁盘扩容

  ```sh
	#ubuntu20安装后，默认会有一半磁盘空间未使用，需要进行下面的操作，将这一半空间用上
	
	
	#显示存在的卷组，Alloc PE是已经分配的磁盘空间，Free PE是尚未分配的磁盘空间
	vgdisplay

	#显查看磁盘目录：可以看到正在使用的磁盘/dev/mapper/ubuntu--vg-ubuntu--lv
	df -h

	#全部空间都给这个盘
	lvextend -l +100%FREE /dev/mapper/ubuntu--vg-ubuntu--lv

	#重新计算磁盘大小
	resize2fs /dev/mapper/ubuntu--vg-ubuntu--lv

	#再次显查看磁盘目录，可以看到/dev/mapper/ubuntu--vg-ubuntu--lv已经把那部分磁盘空间利用上了
	df -h

  ```
  
- ubuntu的虚拟内存扩容

  ```sh
	#察看当前swap分区大小
	free -h

	#查看swap分区挂载位置，默认是/swap.img
	cat /proc/swaps

	#停止原来的交换分区
	#注意：这要等一段时间
	swapoff /swap.img

	#删除原来的分区文件
	rm /swap.img

	#重新建立分区文件swapfile：物理内存是4G，所以这里准备新建的swap分区是4G，bs x count = 1024 × 4000000 = 4G
	#注意：这要等一段时间，可能要十分钟，需要耐心等待
	dd if=/dev/zero of=/swap.img bs=1024 count=4096000

	#启用
	chmod 600 /swap.img
	mkswap -f /swap.img
	swapon /swap.img

	#检查结果
	free -h
	cat /proc/swaps

  ```
  
- nginx的安装

  ```sh
	#更新仓库
	apt update

	#默认安装nginx
	apt install nginx -y

	#检查nginx的安装是否成功
	systemctl status nginx

	#重新装载配置
	nginx -s reload

  ```
  
- ufw的安装

  ```sh
	#更新仓库
	apt update

	#默认安装防火墙
	apt install ufw

	#版本查看
	ufw version

	#查看防火墙运行状态，此时默认是未激活状态的
	#注意：此时千万别激活，否则你会发现全部都登录不上去了，因为防火墙默认的策略是"禁止所有入向，放行所有出向"
	ufw status

	#重要：首先要放开ssh端口和web端口和gateway端口，保证你在防火墙激活后，能够登录SSH和WEB
	#在开发阶段，可以自己添加放开的指定端口
	ufw allow 22/tcp
	ufw allow 80/tcp
	ufw allow 9000/tcp
	
	#允许开发者的IP访问。自己的公网IP可以通过百度浏览器上搜索IP，百度会反射回你的公网IP给你
	#ufw allow from 120.230.79.1/24

	#启动防火墙：此时，你会发现无法通过web访问工控机了，因为防火墙默认的策略是"禁止所有入向，放行所有出向"
	ufw enable

	#查看防火墙策略的编号
	ufw status numbered
	
	#删除指定的策略
	#ufw delete 2

	#禁止防火墙：开发调试阶段，可以先关闭防火墙
	#ufw disable

  ```
  
## Fox-Edge安装

- Fox-Edge安装包下载

  ```sh
	#切换目录
	cd /home
	
	#下载Fox-Edge安装包
	wget -c http://www.fox-tech.cn/repository/fox-edge-server/download/fox-edge-1.0.6.tar.gz

	#解压安装包
	tar -xzvf  fox-edge-1.0.6.tar.gz
	
	#移动目录
	mv fox-edge /opt
	
  ```
  
- 导入MySQL脚本

  ```sh
	#执行命令
	mysql -u root -p12345678;
	
	#在mysql中自行下面脚本
	> source /opt/fox-edge/sql/init.sql;
	
	> exit
	
  ```
- 导入Nginx配置

  ```sh
    #复制nginx配置到nginx
	cp /opt/fox-edge/doc/nginx/conf.d/fox-edge.com.ipadr.conf /etc/nginx/conf.d
	
	#重新加载配置
	nginx -s reload
	
  ```
  
## Fox-Edge启动

- 系统启动

  ```sh
	#进入目录
	cd /opt/fox-edge/shell
	
	#启动Fox-Edge
	./startup.sh
	
  ```
  
- 检查是否启动成功

  ```sh
	#执行命令
	ps -aux|grep fox-edge
	
	#此时将看到下列进程
	root@fox-edge-server:/opt/fox-edge/shell# ps -aux|grep fox-edge
	root     2653381  0.2  0.0 710316  1900 pts/1    Sl   14:19   0:00 /opt/fox-edge/bin/kernel/gateway-service/fox-edge-server-gateway-service-1.0.0.loader java --add-opens java.base/jdk.internal.loader=ALL-UNNAMED -jar /opt/fox-edge/bin/kernel/gateway-service/fox-edge-server-gateway-service-1.0.0.jar --app_name=gateway-service -Dspring.profiles.active=prod --server.port=9000 --spring.redis.host=localhost --spring.redis.port=6379 --spring.redis.password=12345678 --spring.datasource.username=fox-edge --spring.datasource.password=12345678 --spring.datasource.url=jdbc:mysql://localhost:3306/fox_edge
	root     2653407  0.1  0.0 710060  1916 pts/1    Sl   14:19   0:00 /opt/fox-edge/bin/kernel/manager-service/fox-edge-server-manager-system-service-1.0.0.loader java --add-opens java.base/jdk.internal.loader=ALL-UNNAMED -jar /opt/fox-edge/bin/kernel/manager-service/fox-edge-server-manager-system-service-1.0.0.jar --app_name=manager-service -Dspring.profiles.active=prod --server.port=9101 --spring.redis.host=localhost --spring.redis.port=6379 --spring.redis.password=12345678 --spring.datasource.username=fox-edge --spring.datasource.password=12345678 --spring.datasource.url=jdbc:mysql://localhost:3306/fox_edge
	root     2653412 60.7 11.2 6878636 448968 pts/1  Sl   14:19   1:00 java --add-opens java.base/jdk.internal.loader=ALL-UNNAMED -jar /opt/fox-edge/bin/kernel/manager-service/fox-edge-server-manager-system-service-1.0.0.jar --app_name=manager-service -Dspring.profiles.active=prod --server.port=9101 --spring.redis.host=localhost --spring.redis.port=6379 --spring.redis.password=12345678 --spring.datasource.username=fox-edge --spring.datasource.password=12345678 --spring.datasource.url=jdbc:mysql://localhost:3306/fox_edge
	root     2653413 34.1  7.4 5276028 295984 pts/1  Sl   14:19   0:34 java --add-opens java.base/jdk.internal.loader=ALL-UNNAMED -jar /opt/fox-edge/bin/kernel/gateway-service/fox-edge-server-gateway-service-1.0.0.jar --app_name=gateway-service -Dspring.profiles.active=prod --server.port=9000 --spring.redis.host=localhost --spring.redis.port=6379 --spring.redis.password=12345678 --spring.datasource.username=fox-edge --spring.datasource.password=12345678 --spring.datasource.url=jdbc:mysql://localhost:3306/fox_edge

  ```
  
- 登录管理界面

  ```sh
	#使用ifconfig查看计算机的IP
	root@fox-edge-server:/opt/fox-edge/shell# ifconfig
	
	#此时将看到linux下的网卡IP为192.168.3.133
	ens33: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.3.133  netmask 255.255.255.0  broadcast 192.168.3.255
        inet6 fe80::20c:29ff:fe6a:6ef2  prefixlen 64  scopeid 0x20<link>
        ether 00:0c:29:6a:6e:f2  txqueuelen 1000  (Ethernet)
        RX packets 7216222  bytes 2852722926 (2.8 GB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 10559279  bytes 13621898330 (13.6 GB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

	lo: flags=73<UP,LOOPBACK,RUNNING>  mtu 65536
        inet 127.0.0.1  netmask 255.0.0.0
        inet6 ::1  prefixlen 128  scopeid 0x10<host>
        loop  txqueuelen 1000  (Local Loopback)
        RX packets 149369368  bytes 122430812692 (122.4 GB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 149369368  bytes 122430812692 (122.4 GB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
	
  ```
  
  在浏览器中打开，http://192.168.3.133 <br>
  此时将出现登录页面，默认的初始账号/密码：admin/12345678
  ![image](http://docs.fox-tech.cn/_images/login-ui.jpg)
  
  登录成功后进入首页<br>
  ![image](http://docs.fox-tech.cn/_images/home-page.jpg)

## 组件下载安装

###### 1、初始状态
  从 仓库管理>服务模块配置，可以看到，新安装的Fox-Edge只有gateway-service和manager-service两个内核服务<br>
  ![image](http://docs.fox-tech.cn/_images/service01.png)
  
###### 2、下载服务
  从 仓库管理>服务模块仓库，可以看到在灵狐技术网站的中央仓库提供的服务列表。<br>
  ```
	#先下载下列服务
	1、channel-simulator-service
	这是一个通道服务，在测试环境下，为开发者提供一个模式设备的问答响应能力，方便开发者进行设备报文级别的测试
	在此，可以也可以充当设备模拟器的作用
	
	2、device-service
	这是设备数据解码的核心服务，它为上层应用提供面向设备的会话操作。它会将面向设备的会话操作，编码成为通信报文
	然后发送给下层的通道服务，再由通道服务转发给远端的物理设备。当远端设备通过通道服务返回数据的时候，对报文进
	行解码操作，使之成为上层应用可以理解的业务对象。
	
	3、controller-service
	这是控制器服务，会根据用户的监控认为编排，周期性的将设备操作任务，发送给device-service服务。当远端的设备响
	应后，device-service在完成报文解码后，将设备的业务数据返回给控制器服务。
	控制器服务在获得远端设备的业务数据后，会将业务数据发送给persist-service服务，进行持久化保存。
	
	4、persist-service
	这是持久化服务，控制器服务从设备收集完成数据之后，会把需要持久化保存的数据，发送给persist-service服务。此时
	persist-service会将设备的数据，保存到Redis和MySQL当中，供其他服务消费使用。
	
	5、proxy-redis-topic-service
	这是一个代理工具。开发者/工程人员经常需要对设备现场的通信测试，经常需要发送通道报文或者发送设备会话给远端的
	设备，该工具服务可以提供一个代理，将请求发送给device-service或者channel-xxxx-service
	
  ```
  ![image](http://docs.fox-tech.cn/_images/service02.png)
###### 3、安装服务
  ![image](http://docs.fox-tech.cn/_images/service03.png)
  
###### 3、检查安装
  ![image](http://docs.fox-tech.cn/_images/service04.png)
  
## 简单测试验证

###### 1、通道的测试
  从 任务管理>通道操作任务，给模拟设备发送一下测试报文，可以看到模拟设备返回了响应报文，此时说明Fox-Edge跟模拟设备通信上了<br>
  ![image](http://docs.fox-tech.cn/_images/service05.png)
  
###### 2、设备的测试
  从 任务管理>设备操作任务，给模拟设备发送一下操作请求，可以看到模拟设备返回了响应数据，此时说明Fox-Edge跟模拟设备能够正常会话上了<br>
  ![image](http://docs.fox-tech.cn/_images/service06.png)
###### 3、添加监控任务
  从 任务管理>设备监控任务，给控制器服务添加一个监控任务，让其周期性的去访问远端设备<br>
  ![image](http://docs.fox-tech.cn/_images/service07.png)
  
###### 4、检查监控数据
  从 设备管理>设备数值，可以看到添加监控任务后，不断从远端设备获得并持续更新数据，这些设备数据保存在redis和mysql当中<br>
  ![image](http://docs.fox-tech.cn/_images/service08.png)