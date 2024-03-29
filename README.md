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
1. fox-edge-server-protocol-bass260zj 广东高新兴的浙江移动版基站门禁的通信协议
2. fox-edge-server-protocol-cjt188 CJT188的通信协议
3. fox-edge-server-protocol-cetups CET的UPS通信协议
4. fox-edge-server-protocol-dlt645-1997 DLT645通信协议
5. fox-edge-server-protocol-dahua-fire-core 大华消防通信协议
6. fox-edge-server-protocol-gdana-digester 格丹纳公司的检测仪通信协议
7. fox-edge-server-protocol-hj212-2017 中国环境HJ212-2017通信协议
8. fox-edge-server-protocol-hikvision-fire-core 海康威视通信协议
9. fox-edge-server-protocol-modbus ModBus通信协议
10. fox-edge-server-protocol-mitsubishi-plc-fx 三菱 PLC协议
11. fox-edge-server-protocol-omron-fins 欧姆龙Fins PLC协议
12. fox-edge-server-protocol-zs-sht30-1t-1h zs-sht30-1t-1h温湿度传感器协议
13. fox-edge-server-protocol-zktl-air6in1 中科图灵air6in1协议
14. fox-edge-server-protocol-zktl-air5in1 中科图灵air5in1协议
15. fox-edge-server-protocol-zktl-electric 中科图灵电气协议协议
16. fox-edge-server-protocol-zktl-ctrl4g 中科图灵ctrl4g协议协议
17. fox-edge-server-protocol-telecom-core 中国电信总局的通信协议
18. fox-edge-server-protocol-snmp SNMP通信协议
19. fox-edge-server-protocol-opc-ua OPC-UA通信协议
20. fox-edge-server-protocol-iec104-core IEC104通信协议
21. fox-edge-server-protocol-zxdu58 中兴通信的DU58电源通信协议
22. fox-edge-server-protocol-shmeter 上海电表通信协议
23. fox-edge-server-protocol-s7plc 西门子S7通信协议

