# fox-edge-server-device-service

#### 介绍

``` 
设备服务框架
它的作用对各式各样的设备的通信协议，进行编码解码处理。
它能够通过加载这些设备通信协议的编码/解码模块，向下能够对设备的报文进行编码解码，向上能够用统一接restful接口进行通信
它是整个设备通信协议编码/解码的驱动框架
``` 

#### 软件架构

``` 
用户开发完成编码/解码器后后，在conf文件指明编码/解码器的jar文件位置
那么设备服务框架，在启动阶段会扫描配置文件里指明的编码解码器，把这些解码器转载起来
同时在数据库里生成设备操作信息，那么上层应用就可以根据这些信息，不再需要关心设备通信协议报文，对设备进行统一的操作
``` 

``` 
例如：ZJ移动的某个设备的通信协议报文
发送报文：对连接在COM3的设备发送HEX内容
{
	"name": "COM3",
    "mode": "exchange",
	"send": "fe 68 11 11 11 53 12 35 68 01 02 43 c3 a6 16",
	"timeout": 2000
}
接收报文：COM3的设备返回了HEX内容
{
    "type": "serialport",
    "uuid": "6210d8ee8806487a861919b8c24a3273",
    "name": "COM3",
    "mode": "exchange",
    "send": "fe 68 11 11 11 53 12 35 68 01 02 43 c3 a6 16",
    "recv": "fe fe fe fe 68 11 11 11 53 12 35 68 81 07 43 c3 a8 ab 3c 34 34 22 16 ",
    "timeout": 2000,
    "msg": "",
    "code": 200
}
而它的设备操作：
发送：对名为"浙江移动门禁设备"，执行"读取刷卡记录"操作
{
	"operate": "exchange",
	"deviceName": "浙江移动门禁设备",
	"operateName": "读取刷卡记录",
    "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"timeout": 2100
}
返回："浙江移动门禁设备"返回了"34f2bf4b"持卡人，在"2007-03-13 12:58:38"的"刷卡进门"
{
    "deviceType": "BASS260ZJ",
    "msg": "",
    "code": 200,
    "operate": "exchange",
    "data": {
        "commStatus": {
            "commSuccessTime": 1661670206109,
            "commFailedCount": 0,
            "commFailedTime": 0
        },
        "value": {
            "record": [
                {
                    "datetime": "2007-03-13 12:58:38",
                    "recordType": "刷卡记录",
                    "cardId": "34f2bf4b",
                    "event": "刷卡进门"
                }
            ]
        }
    },
    "clientName": "proxy4http2topic",
    "param": {},
    "operateName": "读取刷卡记录",
    "deviceName": "浙江移动门禁设备",
    "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
    "timeout": 2100
}

相对来说，通过设备编码/解码器框架，各式各样的协议报文，就统一变成了JSON
命名为：operateName
数据为：data
其中固定的JSON的Map<String,Obect>结构
``` 

#### 安装教程

``` 
WINDOWS/LINUX的命令行启动
java -jar fox-edge-server-device-service-1.0.0.jar

``` 

#### 特性描述

``` 
Topic描述
1.北向接口
1.1 北向交互式API：topic_device_request_public/topic_device_respond_xxxxxx
device在public监听请求，并根据body中的clientName=xxxx的身份信息，将报文返回给对方

1.2 北向上报式API：topic_device_respond_public
对于物理设备主动上报的报文，由于找不到客户端，所有发送到public，关注它的人自己去订阅

1.3 北向旁路式API：topic_persist_request_public
对于操作类报文，根据携带data/value/result判定为操作类报文，这数据会给旁路的persist
再发送一份，目的是用于让persist服务对操作记录服务，在旁边进行记录，方便管理员进行重要的
操作管理

2.南向接口
1. channel交互API接口：topic_channel_request_xxxxx/topic_channel_respond_device
device服务向向xxxx类型的channel服务发送数据，对应topic是topic_channel_request_xxxxx
xxxx类型的channel服务从该topic接收到数据后，发送给设备，设备返回的信息，channel服务再通过
topic_channel_respond_device返回

``` 

#### 使用说明

1. 发送数据

``` 
http://192.168.1.3:9003/proxy/redis/topic/device/topic_device_request
POST  
发送  
{
	"operate": "exchange",
	"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备",
	"operateName": "获取交流系统模拟量量化数据",
    "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"timeout": 2000
}
返回：  
{
    "deviceType": "ZXDU58",
    "msg": "",
    "code": 200,
    "operate": "exchange",
    "data": {
        "commStatus": {
            "commSuccessTime": 1661757401473,
            "commFailedCount": 0,
            "commFailedTime": 0
        },
        "value": {
            "status": {
                "交流输入相电压L3": 226.2828,
                "交流输入相电压L2": 219.20895,
                "交流屏输出电流L3": 1.3563156E-19,
                "交流输入相电压L1": 228.47934,
                "交流屏输出电流L1": 3.0489116,
                "交流屏输出电流L2": 1.3563156E-19
            }
        }
    },
    "clientName": "proxy4http2topic",
    "param": {
        "VER": 32,
        "CID2": 65,
        "CID1": 64,
        "INFO": "AA==",
        "ADR": 1
    },
    "operateName": "获取交流系统模拟量量化数据",
    "deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备",
    "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
    "timeout": 2000
}
``` 

2. 发送数据

``` 
    http://192.168.1.3:9003/proxy/redis/topic/device/topic_device_request
    POST  
    发送  
   {
   "resource": "operate",
   "device_name": "浙江移动门禁设备",
   "operate_name": "读取刷卡记录",
   "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
   "timeout": 2100,
   "data": {}
   }
    返回：  
   {
   "msg": "",
   "device_name": "浙江移动门禁设备",
   "operate_name": "读取刷卡记录",
   "code": 200,
   "data": {
   "mode": "record",
   "record": [{
   "datetime": "2007-03-13 12:58:38",
   "cardId": "34f2bf4b",
   "event": "刷卡进门"
   }]
   },
   "resource": "operate",
   "client_name": null,
   "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
   "timeout": 2100
   }
``` 

3. 发送数据

``` 
    http://192.168.1.3:9003/proxy/redis/topic/device/topic_device_request
    POST  
    发送  
   {
	"resource": "workflow",
	"workflow_type": "system",
	"device_name": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备",
    "timeout": 8000
    }
    返回：  
{
	"msg": null,
	"code": 200,
	"resource": "workflow",
	"values": {
		"交流输入主空开告警": false,
		"模块04输出电流": 3.9643745,
		"模块12输出电流": 0.0,
		"D级防雷器坏告警": false,
		"C级防雷器坏告警": false,
		"模块08输出电流": 0.0,
		"整流模块数量": 12,
		"交流输入空开2断": true,
		"模块01输出电流": 3.744314,
		"交流辅助输出开关断告警": false,
		"模块05输出电流": 1.0099695,
		"交流屏输出电流L3": 1.3563156E-19,
		"整流模块输出电压": 53.2455,
		"交流屏输出电流L1": 3.0489116,
		"模块09输出电流": 0.0,
		"输出电流L1告警": false,
		"交流屏输出电流L2": 1.3563156E-19,
		"输出电流L2告警": false,
		"输出电流L3告警": false,
		"模块02输出电流": 3.9643745,
		"模块07输出电流": 3.8843138,
		"交流辅助输出开关断": false,
		"交流停电告警": false,
		"模块06输出电流": 1.0,
		"模块10输出电流": 0.0,
		"模块03输出电流": 3.8199387,
		"交流输入电压L3告警": false,
		"交流输入空开1断": false,
		"交流输入电压L1告警": false,
		"交流输入相电压L3": 226.2828,
		"交流输入电压L2告警": false,
		"交流输入相电压L2": 219.20895,
		"交流输入相电压L1": 228.47934,
		"模块11输出电流": 0.0
	},
	"device_type": "ZXDU58",
	"workflow_type": "system",
	"uuid": "f338b2f469864e00823f5d1b55977c82",
	"timeout": 8000,
	"config_param": {},
	"device_name": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备",
	"operate_name": "获取交流系统告警状态",
	"operate_params": {},
	"template_params": {},
	"client_name": null,
	"status": {
		"comm_failed_count": 0,
		"comm_failed_time": 0,
		"comm_sucess_time": 1657378989179
	}
}
``` 

#### 参与贡献

#### 特技
