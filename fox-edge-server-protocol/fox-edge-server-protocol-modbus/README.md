# fox-edge-server-protocol-modbus

#### 介绍
``` 
modbus的通用解码器，它只需要配置数据格式，就可以解析数据
``` 
#### 软件架构
软件架构说明

#### API接口
1. 向Device服务发送请求Read Holding Register操作
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
2. 向Channel服务发送请求Read Holding Register操作
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
3. 向Device服务发送请求Read Coil Status操作
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
4. 向Channel服务发送请求Read Coil Status操作
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
5. 向Device服务发送请求Read Input Status操作
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

6. 向Device服务发送请求Write Single Register操作
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

7. 向Device服务发送请求 Write Single Status 操作
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

#### 模板配置

1. 上海电表-获取电表常数（有功）
``` 
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

#### 参与贡献


#### 特技
