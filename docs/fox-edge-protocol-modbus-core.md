# ModBus 通讯协议解码器

## 介绍
``` 
Fox-Edge的ModBus的通用解码器，它只需要配置配置ModBus的模板文件，就可以进行ModBus报文协议的数据解析
``` 

## 资料
[ModBus协议中文版](http://www.fox-tech.cn/download/docs/ModBus_CN.pdf)

## 工具

``` 
推荐工具：
ModBus设备模拟器：ModbusSlave、Modbus Poll
ModBus设备模拟器：ModSim32
``` 

## 源码

- fox-edge-server-protocol-modbus-core

[源码](https://gitee.com/fierce_wolf/fox-edge-server-protocol/tree/master/fox-edge-server-protocol/fox-edge-server-protocol-modbus-core)

- fox-edge-server-protocol-modbus

[源码](https://gitee.com/fierce_wolf/fox-edge-server-protocol/tree/master/fox-edge-server-protocol/fox-edge-server-protocol-modbus)

## Maven
```pom.xml
	
	<dependencies>
		<!-- fox-tech协议解码器的core包-->
        <dependency>
            <groupId>cn.fox-tech</groupId>
            <artifactId>fox-edge-server-protocol-core</artifactId>
            <version>1.0.0</version>
        </dependency>
		
		<!-- modbus解码器的core包-->
        <dependency>
            <groupId>cn.fox-tech</groupId>
            <artifactId>fox-edge-server-protocol-modbus-core</artifactId>
            <version>1.0.0</version>
        </dependency>
		
		<!-- modbus解码器-->
        <dependency>
            <groupId>cn.fox-tech</groupId>
            <artifactId>fox-edge-server-protocol-modbus</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
``` 

## 模板

### 模板文件
可以参考下列文件的格式，自己配置配置一个模板文件，那么可以让解码器装载这些模板文件，就可以自动解析数据

[文件](https://gitee.com/fierce_wolf/fox-edge-server-protocol/tree/master/template/modbus/1.0.0)


```txt 
fox-edge
├─template
│  ├─modbus
│  │  └─1.0.0
│  │    └─101.CETUPS_Read System Measures Table.csv
│  │    └─100.CETUPS_Read Alarms And Events Table.csv
│  │    └─103.CETUPS_Input Status Table.csv
│  │    └─102.CETUPS_Coil Status Table.csv
``` 

#### 格式说明
- 101.CETUPS_Read System Measures Table.csv

| value_name	| value_index	| bit_index	 | bit_length	 | value_type	 | magnification| determine	| remark | 
|---------------|---------------|------------|---------------|---------------|------------	| --------	| -----  | 
|对象名称       |modbus地址     |该地址bit位 |占用多少个bit  |数据类型       |放大倍数   	| 判定方式	| 备注   | 


```txt
说明：ModBus解码器会根据这张表，对设备进行读取数据后，进行解析成方便用户理解的数据对象

1、value_index
也就是modbus中的地址偏移量。对于一个modbus偏移量来说，它的大小为16位的空间

2、bit_index和bit_length
有些设备厂家为了节省空间，会将一批告警状态数据保存在设备的某个ModBus地址偏移量当中。对于这种用一个16位地址
保存一批状态数据的设备，可以采用bit_index和bit_length自动分拆成一批对象

3、value_type
modbus设备的16位数据，可以被设备厂家们用来保存bool、int、float等数据格式，解码器会对这16位数据进行相应的解析

4、magnification
有些设备厂家，为了保存非常大数值的数据，会采用定点数的方式，在16位mosbus地址空间上，约定放大了一定的倍数保存。

5、determine
对于bool类型的数值，什么时候是true，什么时候是false，厂商们通常会有特定的数值约定，可以通过判定方式判定具体的bool数值


说明：
具体内容，可以参考101.CETUPS_Read System Measures Table.csv文件内容


``` 


### 报文配置

1. 上海电表-获取电表常数（有功）
```json
{
	"operate_list": [{
		"reg_cnt": 69,
		"reg_addr": 1070,
		"table_name": "101.CETUPS_Read System Measures Table.csv",
		"device_addr": 1,
		"modbus_mode": "RTU",
		"operate_name": "Read Holding Register",
		"template_name": "Read System Measures Table"
	}, {
		"reg_cnt": 7,
		"reg_addr": 1055,
		"table_name": "100.CETUPS_Read Alarms And Events Table.csv",
		"device_addr": 1,
		"modbus_mode": "RTU",
		"operate_name": "Read Holding Register",
		"template_name": "Read Alarms And Events Table"
	}]
}
``` 

## API接口
- 1、向Device服务发送请求Read Holding Register操作

``` 
URL:http://192.168.1.10:9000/proxy/redis/topic/device/topic_device_request_public
发送：
POST
{
	"operate": "exchange",
	"deviceName": "ModBus11",
	"operateName": "Read Holding Register",
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"param": {
		"template_name": "Read System Measures Table",
		"device_addr": 1,
		"reg_addr": "04 2E",
		"reg_cnt": 69,
		"modbus_mode": "RTU",
		"operate_name": "Read Holding Register",
		"table_name": "101.CETUPS_Read System Measures Table.csv"
	},
	"timeout": 2000
}
返回：
{
    "deviceType": "ModBus Device",
    "msg": "",
    "code": 200,
    "operate": "exchange",
    "data": {
        "commStatus": {
            "commSuccessTime": 1663293234606,
            "commFailedCount": 0,
            "commFailedTime": 0
        },
        "value": {
            "status": {
                "逆变器06输出功率": 1,
                "逆变器12输出电流": 0.0,
                "系统输出功率": 121,
                "逆变器03输出电流": 1.5,
                "逆变器05输出电流": 0.1,
                "逆变器01输出功率": 48,
                "逆变器03输出功率": 25,
                "逆变器02输出功率": 48,
                "逆变器08输出电流": 0.0,
                "系统输出电压": 229,
                "组1输入电压": 2.9,
                "系统输出电流": 5.3,
                "逆变器01输出电流": 1.7,
                "负载比": 9,
                "逆变器02温度": 59,
                "系统输出频率": 24.4,
                "逆变器15输出电流": 0.0,
                "逆变器02输出电流": 2.1000001
            }
        }
    },
    "clientName": "proxy4http2topic",
    "param": {
        "template_name": "Read System Measures Table",
        "device_addr": 1,
        "reg_addr": "04 2E",
        "reg_cnt": 69,
        "modbus_mode": "RTU",
        "operate_name": "Read Holding Register",
        "table_name": "101.CETUPS_Read System Measures Table.csv",
        "ADDR": 1,
        "REG_ADDR": 1070,
        "REG_CNT": 69,
        "mode": "RTU"
    },
    "operateName": "Read Holding Register",
    "deviceName": "ModBus11",
    "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
    "timeout": 2000
}
``` 


- 2、向Channel服务发送请求Read Holding Register操作

``` 
URL:http://192.168.1.5:9000/proxy/redis/topic/channel/topic_channel_request_tcpsocket
发送：
POST
{
	"name": "127.0.0.1:502",
	"send": "00 00 00 00 00 06 01 03 00 00 00 0A",
	"timeout": 3000
}
返回：
{
    "type": "tcpsocket",
    "uuid": "3988cfbf2c1749dd9e394f64ea44b6ee",
    "name": "127.0.0.1:502",
    "mode": "exchange",
    "send": "00 00 00 00 00 06 01 03 00 00 00 0A ",
    "recv": "00 00 00 00 00 17 01 03 14 00 00 42 34 00 00 00 00 24 68 00 00 5a 7d 00 00 3c 47 00 00 ",
    "timeout": 3000,
    "msg": "",
    "code": 200
}


``` 


- 3、向Device服务发送请求Read Coil Status操作

``` 
URL:http://192.168.1.10:9000/proxy/redis/topic/device/topic_device_request_public
发送：
POST
{
	"operate": "exchange",
	"deviceName": "ModSim32",
	"operateName": "Read Coil Status",
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"param": {
		"template_name": "Read Coil Status Table",
		"device_addr": 1,
		"reg_addr": 0,
		"reg_cnt": 10,
		"modbus_mode": "TCP",
		"operate_name": "Read Coil Status",
		"table_name": "102.CETUPS_Read Coil Status Table.csv"
	},
	"timeout": 2000
}
返回：
{
    "deviceType": "ModBus Device",
    "msg": "",
    "code": 200,
    "operate": "exchange",
    "data": {
        "commStatus": {
            "commSuccessTime": 1663567502742,
            "commFailedCount": 0,
            "commFailedTime": 0
        },
        "value": {
            "status": {
                "线圈状态3": false,
                "线圈状态2": true,
                "线圈状态1": false,
                "线圈状态10": false,
                "线圈状态9": true,
                "线圈状态8": false,
                "线圈状态7": true,
                "线圈状态6": false,
                "线圈状态5": true,
                "线圈状态4": false
            }
        }
    },
    "clientName": "proxy4http2topic",
    "param": {
        "template_name": "Read Coil Status Table",
        "device_addr": 1,
        "reg_addr": 0,
        "reg_cnt": 10,
        "modbus_mode": "TCP",
        "operate_name": "Read Coil Status",
        "table_name": "102.CETUPS_Coil Status Table.csv",
        "ADDR": 1,
        "REG_ADDR": 0,
        "REG_CNT": 10,
        "mode": "TCP"
    },
    "operateName": "Read Coil Status",
    "deviceName": "ModSim32",
    "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
    "timeout": 2000
}
``` 

- 4、向Channel服务发送请求Read Coil Status操作

``` 
URL:http://192.168.1.5:9000/proxy/redis/topic/channel/topic_channel_request_tcpsocket
发送：
POST
{
	"name": "127.0.0.1:502",
	"send": "03 E5 00 00 00 06 01 01 00 00 00 0A ",
	"timeout": 3000
}
返回：
{
    "type": "tcpsocket",
    "uuid": "9389cb1f97ac40d58ca11a7d7d82c5c0",
    "name": "127.0.0.1:502",
    "mode": "exchange",
    "send": "03 E5 00 00 00 06 01 01 00 00 00 0A ",
    "recv": "03 e5 00 00 00 05 01 01 02 52 01 ",
    "timeout": 3000,
    "msg": "",
    "code": 200
}
``` 

- 5、向Device服务发送请求Read Input Status操作

``` 
URL:http://192.168.1.10:9000/proxy/redis/topic/device/topic_device_request_public
发送：
POST
{
	"operate": "exchange",
	"deviceName": "ModSim32",
	"operateName": "Read Input Status",
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"param": {
		"template_name": "Read Input Status Table",
		"device_addr": 1,
		"reg_addr": 0,
		"reg_cnt": 10,
		"modbus_mode": "TCP",
		"operate_name": "Read Input Status",
		"table_name": "103.CETUPS_Input Status Table.csv"
	},
	"timeout": 2000
}
返回：
{
    "deviceType": "ModBus Device",
    "msg": "",
    "code": 200,
    "operate": "exchange",
    "data": {
        "commStatus": {
            "commSuccessTime": 1663573663843,
            "commFailedCount": 0,
            "commFailedTime": 0
        },
        "value": {
            "status": {
                "输入状态3": false,
                "输入状态4": false,
                "输入状态1": false,
                "输入状态2": true,
                "输入状态7": true,
                "输入状态8": false,
                "输入状态10": false,
                "输入状态5": true,
                "输入状态6": false,
                "输入状态9": true
            }
        }
    },
    "clientName": "proxy4http2topic",
    "param": {
        "template_name": "Read Input Status Table",
        "device_addr": 1,
        "reg_addr": 0,
        "reg_cnt": 10,
        "modbus_mode": "TCP",
        "operate_name": "Read Input Status",
        "table_name": "103.CETUPS_Read Input Status Table.csv",
        "ADDR": 1,
        "REG_ADDR": 0,
        "REG_CNT": 10,
        "mode": "TCP"
    },
    "operateName": "Read Input Status",
    "deviceName": "ModSim32",
    "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
    "timeout": 2000
}
``` 

- 6、向Device服务发送请求Write Single Register操作

``` 
URL:http://192.168.1.10:9000/proxy/redis/topic/device/topic_device_request_public
发送：
POST
{
	"operate": "exchange",
	"deviceName": "ModSim32",
	"operateName": "Write Single Register",
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"param": {
		"template_name": "Read System Measures Table",
		"device_addr": 1,
		"reg_cnt": 69,
		"modbus_mode": "TCP",
		"operate_name": "Write Single Register",
        "object_name": "系统输出频率",
        "object_value": 1,
		"table_name": "101.CETUPS_Read System Measures Table.csv"
	},
	"timeout": 2000
}
返回：
{
    "deviceType": "ModBus Device",
    "msg": "",
    "code": 200,
    "operate": "exchange",
    "data": {
        "commStatus": {
            "commSuccessTime": 1663594882331,
            "commFailedCount": 0,
            "commFailedTime": 0
        },
        "value": {
            "status": {}
        }
    },
    "clientName": "proxy4http2topic",
    "param": {
        "template_name": "Read System Measures Table",
        "device_addr": 1,
        "reg_cnt": 69,
        "modbus_mode": "TCP",
        "operate_name": "Write Single Register",
        "object_name": "系统输出频率",
        "object_value": 1,
        "table_name": "101.CETUPS_Read System Measures Table.csv",
        "ADDR": 1,
        "REG_ADDR": 1073,
        "REG_CNT": 1,
        "mode": "TCP"
    },
    "operateName": "Write Single Register",
    "deviceName": "ModSim32",
    "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
    "timeout": 2000
}
``` 

- 7、向Device服务发送请求 Write Single Status 操作
``` 
URL:http://192.168.1.10:9000/proxy/redis/topic/device/topic_device_request_public
发送：
POST
{
	"operate": "exchange",
	"deviceName": "ModSim32",
	"operateName": "Write Single Status",
	"uuid": "1b1df78266b9449c9d5705f821a2b4c1",
	"param": {
		"template_name": "Write Single Status Table",
		"device_addr": 1,
		"reg_cnt": 69,
		"modbus_mode": "TCP",
		"operate_name": "Write Single Status",
        "object_name": "线圈状态5",
        "object_value": true,
		"table_name": "102.CETUPS_Coil Status Table.csv"
	},
	"timeout": 2000
}
返回：
{
    "deviceType": "ModBus Device",
    "msg": "",
    "code": 200,
    "operate": "exchange",
    "data": {
        "commStatus": {
            "commSuccessTime": 1663595830542,
            "commFailedCount": 0,
            "commFailedTime": 0
        },
        "value": {
            "status": {}
        }
    },
    "clientName": "proxy4http2topic",
    "param": {
        "template_name": "Write Single Status Table",
        "device_addr": 1,
        "reg_cnt": 69,
        "modbus_mode": "TCP",
        "operate_name": "Write Single Status",
        "object_name": "线圈状态5",
        "object_value": true,
        "table_name": "102.CETUPS_Coil Status Table.csv",
        "ADDR": 1,
        "REG_ADDR": 4,
        "REG_CNT": 1,
        "mode": "TCP"
    },
    "operateName": "Write Single Status",
    "deviceName": "ModSim32",
    "uuid": "1b1df78266b9449c9d5705f821a2b4c1",
    "timeout": 2000
}
``` 


