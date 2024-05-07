# 介绍

> **Fox-Edge** 和 **Fox-Cloud** 是[灵狐技术](http://www.fox-tech.cn/)
为物联网企业与开发者们提供的边缘计算方案以及配套产品


## 背景
当前物联网的现场设备，普遍有数据上企业数据中心/数据云的集中管理需求。
但是，不管是现在的物联网公有云方案，还是传统的智能网关方案，在解决这个问题上，都有解决方案本身带来的重大缺陷。
所以，Fox-Edge提供另一种解决方案，来供中小企业解决数据如何上云的集中管理需求。


![image](https://gitee.com/fierce_wolf/fox-edge-server/raw/master/img/networking.jpg)

Fox-Edge 边缘计算，致力于提供一种在线下管理智能设备的物联网解决方案，帮助各类中小型设备制造企业、系统集成商将现场的智能设备接入到边缘计算设备上后，一站式输送到客户的企业数据中心数据库或者是用户在云端的数据库，方便客户的IT业务团队专注数据的分析和处理。

## 运行环境
在现实中，Fox-Edge安装在通用的 **x86系列** 或者 **ARM系列** 的 **小型工控机** 或者是 **软路由** 或者是 **小型工业网关** 这类小型物理设备上，使之成为线下的一种专用网关设备，然后它可以跟那些智能设备一起被安装在现场。
1.  **x86系列** 或者 **ARM系列** 的**小型工控机** 或者是 **软路由** 或者是 **小型工业网关** 这类小型物理设备
2. 最低配置：4G物理内存（推荐8G内存），64G存储空间。

<img src="https://gitee.com/fierce_wolf/fox-edge-server/raw/master/img/NanoPiR5C.jpg" style="width:50%;height:auto;">
<img src="https://gitee.com/fierce_wolf/fox-edge-server/raw/master/img/device-router.jpg" alt="软路由" style="width:50%;height:auto;">
<img src="https://gitee.com/fierce_wolf/fox-edge-server/raw/master/img/device.jpg" style="width:50%;height:auto;">



## 产品特点
fox-edge 边缘计算 采用积木式的全开放式的架构，方便用户自选各种组件和从仓库中选择解码器。目前正在增强解码器仓库功能，方便各用户互相分享自己开发的解码器。

- 积木式架构，用户可以根据自己的需要，可以从中央仓库中自行挑选组件和服务，搭建成适合自己项目的边缘计算系统
- 开放的架构，如果仓库中找不到适合自己项目的组件和服务，用户也可以自行开发或者委托第三方开发者开发组件和服务
- 共享的资源，中央仓库可以为用户们互相分享自己开发的解码器组件和各类应用服务
- 通用的平台，硬件环境运行在通用的ARM和x86硬件环境，不用担心专用设备对企业的绑定
- 方便的ODM，让企业可以将Fox-Edge部署在通用工控机上后，以企业自己的产品形式对外销售
- 免费的产品，用户可以从官网免费下载产品，用于非商业用途。

## 软件架构
![image](https://gitee.com/fierce_wolf/fox-edge-server/raw/master/img/system.jpg)

- 可以通过自行开发通道服务部件和通信协议解码器插件，对接下行现场网络的智能设备
- 可以自行调整或者替换系统自带控制器、触发器部件，实现项目需要的业务功能
- 可以自行开发或者替换系统调整上行接口，实现上行对接用户数据中心的接口。

## 服务清单
```txt 
fox-edge
├─fox-edge-server-channel
│  ├─fox-edge-server-channel-bacnet-client
│  ├─fox-edge-server-channel-coap-client
│  ├─fox-edge-server-channel-gdana-digester
│  ├─fox-edge-server-channel-http-client
│  ├─fox-edge-server-channel-iec104
│  ├─fox-edge-server-channel-mqtt-client
│  ├─fox-edge-server-channel-opc-ua-service
│  ├─fox-edge-server-channel-proxy-service
│  ├─fox-edge-server-channel-serialport-service
│  ├─fox-edge-server-channel-simulator-service
│  ├─fox-edge-server-channel-snmp-service
│  ├─fox-edge-server-channel-tcpsocket-service
│  └─fox-edge-server-channel-udpsocket-service
├─fox-edge-server-controller
├─fox-edge-server-device
├─fox-edge-server-persist
├─fox-edge-server-protocol
│  ├─fox-edge-server-protocol-bass260zj
│  ├─fox-edge-server-protocol-cetups
│  ├─fox-edge-server-protocol-cjt188
│  ├─fox-edge-server-protocol-cjt188-core
│  ├─fox-edge-server-protocol-core
│  ├─fox-edge-server-protocol-dlt645-1997
│  ├─fox-edge-server-protocol-dlt645-core
│  ├─fox-edge-server-protocol-gdana-digester
│  ├─fox-edge-server-protocol-iec104-core
│  ├─fox-edge-server-protocol-iec104-slaver
│  ├─fox-edge-server-protocol-lrw
│  ├─fox-edge-server-protocol-mitsubishi-plc-fx
│  ├─fox-edge-server-protocol-mitsubishi-plc-fx-core
│  ├─fox-edge-server-protocol-modbus
│  ├─fox-edge-server-protocol-modbus-core
│  ├─fox-edge-server-protocol-omron-fins
│  ├─fox-edge-server-protocol-omron-fins-core
│  ├─fox-edge-server-protocol-shmeter
│  ├─fox-edge-server-protocol-snmp
│  ├─fox-edge-server-protocol-telecom-core
│  ├─fox-edge-server-protocol-zs-sht30-1t-1h
│  └─fox-edge-server-protocol-zxdu58
├─fox-edge-server-proxy
│  ├─fox-edge-server-proxy-cloud
│  └─fox-edge-server-proxy-redis-topic-service
├─fox-edge-server-service
├─fox-edge-server-trigger

``` 

## 在线体验
在安装之前，在线上有个[预览版本](http://fox-edge-demo.fox-tech.cn)，你现在就可以直接体验

## 联系方式
EMAIL： 5389408@QQ.COM


## 微信技术交流群
<img src="https://gitee.com/fierce_wolf/fox-edge-server/raw/master/img/weixinqun-01.jpg" style="width:25%;height:auto;">

<img src="https://gitee.com/fierce_wolf/fox-edge-server/raw/master/img/weixin.png" style="width:25%;height:auto;">




## 通信协议解码器列表
| 解码器类型		|				设备厂商			|	设备类型							|
|-------------------|-----------------------------------|---------------------------------------|
|动态解码器			|	武汉中科图灵科技有限公司		|	尼特烟雾传感器-4G-YG				|
|动态解码器			|	武汉中科图灵科技有限公司		|	水浸传感器							|
|动态解码器			|	中国环境保护设备				|	HJ212-2017							|
|动态解码器			|	海湾安全技术有限公司			|	JB-QB-GST9000						|
|动态解码器			|	武汉中科图灵科技有限公司		|	消防用水五合一（LoRaWan）			|
|动态解码器			|	海湾安全技术有限公司			|	JB-QB-GST500						|
|动态解码器			|	海康威视						|	消防主机							|
|动态解码器			|	海湾安全技术有限公司			|	JB-QB-GST200						|
|动态解码器			|	海湾安全技术有限公司			|	JB-QB-GST1500H						|
|动态解码器			|	北京利达华信电子有限公司		|	JB-QG-LD128E(Q)I					|
|动态解码器			|	北京利达华信电子有限公司		|	JB-QT-LD188EL						|
|动态解码器			|	北京利达华信电子有限公司		|	JB-QG-LD128E(Q)II					|
|动态解码器			|	泰和安消防科技有限公司			|	JB-QB-TX3001DY						|
|动态解码器			|	泰和安消防科技有限公司			|	JB-TGL-TX3006C						|
|动态解码器			|	上海松江飞繁电子有限公司		|	JB-3208G							|
|动态解码器			|	上海松江飞繁电子有限公司		|	JB-3101G							|
|动态解码器			|	青鸟消防股份有限公司			|	JB-TG-JBF-11S						|
|动态解码器			|	青鸟消防股份有限公司			|	JB-QB-JBF-5010						|
|动态解码器			|	青鸟消防股份有限公司			|	JB-QB-JBF-5009						|
|动态解码器			|	德国西门子股份有限公司			|	JB-TBZL-FC720W						|
|动态解码器			|	深圳市赋安安全系统有限公司		|	JB-LTZ2-FS5116						|
|动态解码器			|	青岛鼎信通讯消防安全有限公司	|	JB-QT-TS3200						|
|动态解码器			|	北京防威威盛机电设备有限责任公司|	JB-QGZ2L-FW19000G					|
|动态解码器			|	福建闽安安全技术有限责任公司	|	JB-TB-MASYS5000						|
|动态解码器			|	蚌埠依爱消防电子有限责任公司	|	JB-TBL-EI8000S						|
|动态解码器			|	蚌埠依爱消防电子有限责任公司	|	JB-QBL-EI6000M						|
|动态解码器			|	蚌埠依爱消防电子有限责任公司	|	JB-QB-EI8002L						|
|动态解码器			|	深圳市泛海三江电子有限公司		|	JB-QGL-9116							|
|动态解码器			|	深圳市泛海三江电子有限公司		|	JB-QGL-A116							|
|动态解码器			|	爱德华消防器材有限公司			|	EST3								|
|动态解码器			|	营口赛福德电子技术有限公司		|	JB-LG-YS4800B						|
|动态解码器			|	营口天成消防设备有限公司		|	JB-TB-TC5100						|
|动态解码器			|	营口天成消防设备有限公司		|	JB-QB-TC5160						|
|动态解码器			|	营口天成消防设备有限公司		|	JB-TB-TC3000						|
|动态解码器			|	浙江爱德智能科技股份有限公司	|	AD8000B							|
|静态解码器			|	Fox Edge						|	OPC-UA								|		
|静态解码器			|	欧姆龙							|	omron-fins							|
|静态解码器			|	中国电信集团					|	基站空调(QZTT-2219-2020)			|
|静态解码器			|	广东海悟科技有限公司			|	基站空调(V2.00)						|
|静态解码器			|	美的集团股份有限公司			|	基站空调(V2.00)						|
|静态解码器			|	广东海悟科技有限公司			|	基站空调(V1.0D)						|
|静态解码器			|	广东海悟科技有限公司			|	基站空调(V1.00)						|
|静态解码器			|	TCL科技集团股份有限公司			|	柜式空调(KPRd)						|
|静态解码器			|	海尔集团公司					|	海尔空调-YCJ-A000					|
|静态解码器			|	武汉中科图灵科技有限公司		|	电器火灾监控设备					|
|静态解码器			|	武汉中科图灵科技有限公司		|	4G控制器							|
|静态解码器			|	武汉中科图灵科技有限公司		|	五合一空气监测传感器				|
|静态解码器			|	武汉中科图灵科技有限公司		|	六合一空气监测传感器				|
|静态解码器			|	成都兢志成电子科技有限公司		|	风机(JPF4826)						|
|静态解码器			|	Siemens							|	S7 PLC								|
|静态解码器			|	中兴通讯						|	ZXDU58								|
|静态解码器			|	Fox Edge						|	ModBus Device						|
|静态解码器			|	广东高新兴						|	BASS260ZJ							|
|静态解码器			|	中盛科技						|	温湿度采集模块(ZS-SHT30-1T-1H-485)	|
|静态解码器			|	Fox Edge						|	DLT645 v1997						|
|静态解码器			|	Fox Edge						|	DLT645 v2007						|
|静态解码器			|	Fox Edge						|	CJT188								|
|静态解码器			|	广州格丹纳仪器有限公司			|	全自动消解控制器					|
|静态解码器			|	Fox Edge						|	SNMP Device							|
|静态解码器			|	欧姆龙							|	omron-fins							|
|静态解码器			|	上海电表厂						|	ShangHai Electricity Meter			|
|静态解码器			|	Fox Edge						|	IEC104 Device						|
|静态解码器			|	mitsubishi						|	mitsubishi-plc-fx					|
|静态解码器			|	深圳安圣电气有限公司			|	CE+T UPS							|
|动态模板			|	正泰集团股份有限公司			|	三相电表(DT-SU666)					|
