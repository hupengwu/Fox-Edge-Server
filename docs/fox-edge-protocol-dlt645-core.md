# DLT645 通讯协议解码器

## 介绍
``` 
Fox-Edge的DLT645的通用解码器，它只需要配置配置DLT645的模板文件，就可以进行DLT645报文协议的数据解析

DLT645的协议框架模块，电力系统的各家设备厂商，会基于DLT645的协议框架，各自实现各自的DLT645协议。
这些设备厂商遵守DLT645的同样协议框架，但又各自实现了自己独有的特性，或者对DLT645协议框架进行了各自的调整
所以，这边将公共的DLT645协议框架特性抽取出来，作为DLT645设备族的公共模块，来解决各家设备通用但又差异化的场景
``` 

## 资料
[DLT645-1997协议](http://www.fox-tech.cn/download/docs/DLT645_1997.pdf)

[DLT645-2007协议](http://www.fox-tech.cn/download/docs/DLT645_2007.pdf)

## 工具

[DLT645电表模拟器](http://www.fox-tech.cn/download/tools/DLT645MeterV2.7.1.rar)

## 源码

- fox-edge-server-protocol-dlt645-core

[源码](https://gitee.com/fierce_wolf/fox-edge-server-protocol/tree/master/fox-edge-server-protocol/fox-edge-server-protocol-dlt645-core)

- fox-edge-server-protocol-dlt645-1997

[源码](https://gitee.com/fierce_wolf/fox-edge-server-protocol/tree/master/fox-edge-server-protocol/fox-edge-server-protocol-dlt645-1997)

## Maven
```pom.xml
	
	<dependencies>
		<!-- fox-tech协议解码器的core包-->
        <dependency>
            <groupId>cn.fox-tech</groupId>
            <artifactId>fox-edge-server-protocol-core</artifactId>
            <version>1.0.0</version>
        </dependency>
		
		<!-- dlt645解码器的core包-->
        <dependency>
            <groupId>cn.fox-tech</groupId>
            <artifactId>fox-edge-server-protocol-dlt645-core</artifactId>
            <version>1.0.0</version>
        </dependency>
		
		<!-- dlt645解码器-->
        <dependency>
            <groupId>cn.fox-tech</groupId>
            <artifactId>fox-edge-server-protocol-dlt645-1997</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
``` 

## 模板

### 模板文件
可以参考下列文件的格式，自己配置配置一个模板文件，那么可以让解码器装载这些模板文件，就可以自动解析数据

[模板文件](https://gitee.com/fierce_wolf/fox-edge-server-protocol/tree/master/template/dlt645/1.0.0)


```txt 
fox-edge
├─template
│  ├─dlt645
│  │  └─1.0.0
│  │    └─DLT645-1997.csv
│  │    └─DLT645-2007.csv

``` 

#### 格式说明
- DLT645-2007.csv

|di1h			|di1l			|di0h		 |di0l			 |format		 |	length		|unit	    |read	     |write	   |name		| 
|---------------|---------------|------------|---------------|---------------|------------	| --------	| -----      |  -----  |  -----     |
|di1高位        |di1低位        |di0高位     |di0低位        |数据格式       |长度      	| 单位	    | 是否可读   | 是否可写|对象名称    |


```txt
说明：DLT645解码器会根据这张表，对设备进行读取数据后，进行解析成方便用户理解的数据对象

1、di1h/di1l/di0h/di0l
DLT645协议中，将一个个对象用固定的数字ID标识，在报文中进行传输，可以参考DLT645协议的文档

2、format/length/unit/read/write
DLT645协议中，将一个个对象在传输的时候，会根据DI指明该数据对象的解析格式，单位是什么，是否可读可写，可以参考DLT645协议的文档

3、name
DI值只是方便对象在报文传输，对象具体含有还是要给它取个对象名，方便解析之后，被后面的业务应用进行使用



说明：
具体内容，可以参考DLT645-1997.csv和DLT645-2007.csv文件内容
然后，开发者修改模板文件的内容，就能适配各个厂家的DLT645设备了


``` 


### 报文配置

1. 读取DLT645的电表数据
```json
[
     {
          "param": {
               "tableName": "DLT645-v1997/1.0.0/DLT645-1997.csv",
               "objectName": "日期及周次",
               "deviceAddress": "351253111111"
          },
          "timeout": 2000,
          "operateMode": "exchange",
          "operateName": "读数据"
     },
     {
          "param": {
               "tableName": "DLT645-v1997/1.0.0/DLT645-1997.csv",
               "objectName": "时间",
               "deviceAddress": "351253111111"
          },
          "timeout": 2000,
          "operateMode": "exchange",
          "operateName": "读数据"
     },
     {
          "param": {
               "tableName": "DLT645-v1997/1.0.0/DLT645-1997.csv",
               "objectName": "电表运行状态字",
               "deviceAddress": "351253111111"
          },
          "timeout": 2000,
          "operateMode": "exchange",
          "operateName": "读数据"
     },
     {
          "param": {
               "tableName": "DLT645-v1997/1.0.0/DLT645-1997.csv",
               "objectName": "电网状态字",
               "deviceAddress": "351253111111"
          },
          "timeout": 2000,
          "operateMode": "exchange",
          "operateName": "读数据"
     },
     {
          "param": {
               "tableName": "DLT645-v1997/1.0.0/DLT645-1997.csv",
               "objectName": "周休日状态字",
               "deviceAddress": "351253111111"
          },
          "timeout": 2000,
          "operateMode": "exchange",
          "operateName": "读数据"
     },
     {
          "param": {
               "tableName": "DLT645-v1997/1.0.0/DLT645-1997.csv",
               "objectName": "表号",
               "deviceAddress": "351253111111"
          },
          "timeout": 2000,
          "operateMode": "exchange",
          "operateName": "读数据"
     },
     {
          "param": {
               "tableName": "DLT645-v1997/1.0.0/DLT645-1997.csv",
               "objectName": "用户号",
               "deviceAddress": "351253111111"
          },
          "timeout": 2000,
          "operateMode": "exchange",
          "operateName": "读数据"
     },
     {
          "param": {
               "tableName": "DLT645-v1997/1.0.0/DLT645-1997.csv",
               "objectName": "自动抄表日期",
               "deviceAddress": "351253111111"
          },
          "timeout": 2000,
          "operateMode": "exchange",
          "operateName": "读数据"
     }
]
``` 

## DLT645电表接入演示

### 软件下载

```
	1、虚拟串口

	virtualserialportdriver8.rar

	2、串口监听工具

	CEIWEI_CommMonitor_v12.0.1.exe

	3、DLT645模拟器软件下载

	DLT645%20simulator 20v2.7.1.rar


	上述软件，可以去百度下载后安装到本地

```

### 串口配置
- 1、新增COM1和COM2两个虚拟串口，这两个串口默认是环回的，也就是给COM1发数据，COM2能收到数据

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/serial2.jpg)

- 2、打开串口精灵，开始监听COM1和COM2

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/serial1.jpg)

- 3、打开DLT645模拟器，打开COM2，让它在COM2接收智能网关待会请求报文

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/serial3.jpg)

- 4、打开VMWARE，打开COM1，并映射陈串口2，智能网关从这个串口发送数据

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/serial4.jpg)


### 智能网关和DLT645电表的通信测试
- 1、登录智能网关后，切换到通道页面，建立一个串口通道。注意在智能网关中，串口2的名称未ttyS1

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/dlt645%204.jpg)

- 2、切换到设备页面，建立一个DLT645设备，并指明使用刚才建立的串口通道

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/dlt645%205.jpg)


- 3、建立一个通道操作任务，向电表发送一个报文看看，此时可以看到电表的返回数据，说明可以跟电表通信正常

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/dlt645%202.jpg)

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/serial1.jpg)

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/dlt645.jpg)

- 4、建立一个设备操作任务，向电表发送一个请求，看看解码器对报文的数据解码，可以看到数据被解析出来了

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/dlt645%203.jpg)

- 5、建立一个监控任务，让智能网关不断收集DLT645电表的数据

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/dlt645%206.jpg)

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/dlt645.jpg)

- 6、进入设备数值页面，可以看到智能网关不断的向DLT645电表获取数据

![image](http://120.79.69.201:9002/fox-edge-server/doc/demo/dlt645/dlt645%207.jpg)

