# iot-thingspanel

## 介绍

服务名：iot-thingspanel

工程名称：fox-edge-server-iot-thingspanel

说明：

ThingsPanel 是国内一家著名的开源物联网平台，用于数据收集、处理、可视化展示以及设备管理。
ThingsPanel 使用行业标准物联网协议（MQTT，TCP 和 HTTP）实现设备连接，并支持云和本地部署。

灵狐为了演示Fox-Edge的北向接入能力，同时也考虑到 ThingsPanel 在国内开源物联网云平台的影响力，
所以，提供了iot-thingspanel，将对接 ThingsPanel ，作为北向对接第三方物联网云平台的一个范例。


<img src="http://docs.fox-tech.cn/_images/thingspanel-01.png" style="width:100%;height:auto;">

## 网站

[ThingsPanel 官 网](https://www.thingspanel.cn/)


[ThingsPanel试用版](http://dev.thingspanel.cn/)

## 方案

### 1、ThingsPanel

**ThingsPanel** 是一个开源的物联网，它对外提供了HTTP接口来接受现场设备和网关的数据推送。


ThingsPanel 的HTTP Restful接口格式为:

resource: http://dev.thingspanel.cn:9988/api/device/{deviceToken}/attributes

method: POST

body:{k1:v1,k2:v2,.....}


deviceToken是ThingsPanel手动注册设备后，ThingsPanel分配的一个标识为设备身份标识的静态身份Token，
可以在ThingsPanel的云平台管理界面上，该设备的属性页面上查询到。

```sh	
#范例：
curl -v -X POST http://dev.thingspanel.cn:9988/api/device/baf45ab9-0f5a-1e31-0f0c-55f910fea7d9/attributes --header Content-Type:application/json --data '{"temp":22.2,"hum":22}'

```
注意：Windows下的curl对这个命令支持的不是很好，请用Linux下的curl发送这个条命令，灵狐在验证阶段踩了windows curl版本这个坑



### 2、Fox-Edge

**Fox-Edge** 是一个2B/2G类的物联网**边缘计算平台**。**Fox-Edge**将现场大量的设备数据接入和管理之后，再将数据推送给云平台。

**Fox-Edge** 是一个高度可扩展的 **边缘计算平台** ，它的架构允许它通过添加各种北向接入服务，实现跟各家云平台对接。

**iot-thingspanel** 是面向 **ThingsPanel** 的物联网云平台的北向对接服务。

### 3、对接过程

1、在ThingsPanel官网开通测试账号，申请一个独立的ThingsPanel云平台测试环境。

2、在ThingsPanel上手动注册了现场设备名称，ThingsPanel接下来可以管理这些设备了。

4、在Linux工作台环境下，用curl命令按照格式ThingsPanel要求的格式，测试一下你的本地环境是否能够把数据推送给到ThingsPanel。

5、在Fox-Edge的扩展信息页面，添加一个thingsboardHttpToken字段，此时Fox-Edge上的每一个设备都会自动具有该属性。

6、在ThingsPanel上的设备页面上，把每个设备的接入HTTP Token复制出来，粘贴到Fox-Edge的设备上的thingsboardHttpToken字段中。

7、启动iot-thingspanel服务，此时会Fox-Edge会把设备采样数据推送到ThingsPanel

8、在ThingsPanel上，可以看到Fox-Edge的设备状态数据。


**提醒** ：

大多数2C物联网云平台的数据接入能力和带宽资源非常有限（人家的带宽也是花钱买的），测试阶段不要物联网平台滥发数据。

目前iot-thingspanel建议的数据推送方式是哪个设备有数据变化，就推送该设备的数据。
而Fox-Edge管理的设备特别的多，数据变化也很剧烈，这可能意味着对ThingsPanel巨大的数据压力，在公网测试时，别测试太多的设备，
否则小心被物联网云平台运营方视为恶意攻击，被封号、拉黑。

iot-thingspanel提供了另外种定时发送数据的方式，来减少对ThingsPanel的压力。

两种方式各有优缺点，具体选择哪种方式，还是看自己具体项目的需求。
