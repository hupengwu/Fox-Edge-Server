# fox-serialport

#### 介绍
linux下的串口服务器

#### 软件架构
软件架构说明


#### 安装教程

1.  该服务职能在LINUX环境运行，所以需要远程调试
2.  远程调试的配置，不要参考简单网上的配置，因为linux的默认端口是环回端口，无法远程连接
3.  linux侧的启动命令，必须强制指明IP地址，例如:
java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=192.168.241.128:5005 fox-serialport-service-0.0.1.jar

#### 使用说明

1. 发现远端设备
``` 
http://127.0.0.1:9002/channel/bacnet/discover
GET
返回：
{
    "msg": "操作成功",
    "code": 200
}
``` 

2. 查询指定数据
``` 
http://127.0.0.1:9002/channel/bacnet/execute
POST
LINUX发送
    {
    "name": "3",
    "format": "map",
    "send": {
    "resource": "value",
    "method": "get",
    "param": [{
    "object_type": "analog-input",
    "oid": 0
    }, {
    "object_type": "analog-input",
    "oid": 1
    }, {
    "object_type": "analog-input",
    "oid": 2
    }, {
    "object_type": "analog-input",
    "oid": 3
    }, {
    "object_type": "analog-input",
    "oid": 4
    }]
    },
    "timeout": 3000
    }
返回：
    {
    "msg": null,
    "code": 200,
    "data": {
    "format": "map",
    "name": "3",
    "send": {
    "resource": "value",
    "method": "get",
    "param": [
    {
    "object_type": "analog-input",
    "oid": 0
    },
    {
    "object_type": "analog-input",
    "oid": 1
    },
    {
    "object_type": "analog-input",
    "oid": 2
    },
    {
    "object_type": "analog-input",
    "oid": 3
    },
    {
    "object_type": "analog-input",
    "oid": 4
    }
    ]
    },
    "recv": {
    "resource": "value",
    "method": "get",
    "param": [
    {
    "pin": null,
    "valueType": "Real",
    "progress": 0.3333333333333333,
    "pid": "present-value",
    "oid": 0,
    "deviceId": 3,
    "oidType": 9,
    "value": "20.5"
    },
    {
    "pin": null,
    "valueType": "Real",
    "progress": 0.6666666666666666,
    "pid": "present-value",
    "oid": 1,
    "deviceId": 3,
    "oidType": 9,
    "value": "35.8"
    },
    {
    "pin": null,
    "valueType": "Real",
    "progress": 1.0,
    "pid": "present-value",
    "oid": 2,
    "deviceId": 3,
    "oidType": 9,
    "value": "12.0"
    }
    ]
    },
    "timeout": 3000
    }
    }
``` 
2. 查询全部数据
``` 
    http://127.0.0.1:9002/channel/bacnet/execute
    POST
    LINUX发送
    {
    "name": "3",
    "format": "map",
    "send": {
    "resource": "value",
    "method": "get"
    },
    "timeout": 3000
    }
    返回：
    {
    "msg": null,
    "code": 200,
    "data": {
    "format": "map",
    "name": "3",
    "send": {
    "resource": "value",
    "method": "get"
    },
    "recv": {
    "resource": "value",
    "method": "get",
    "param": [
    {
    "pin": null,
    "valueType": "ErrorClassAndCode",
    "progress": 0.07692307692307693,
    "pid": "present-value",
    "oid": 3,
    "deviceId": 3,
    "oidType": 9,
    "value": "errorClass=property, errorCode=unknown-property"
    },
    {
    "pin": null,
    "valueType": "Real",
    "progress": 0.15384615384615385,
    "pid": "present-value",
    "oid": 0,
    "deviceId": 3,
    "oidType": 9,
    "value": "20.5"
    },
    {
    "pin": null,
    "valueType": "Real",
    "progress": 0.23076923076923078,
    "pid": "present-value",
    "oid": 1,
    "deviceId": 3,
    "oidType": 9,
    "value": "39.7"
    },
    {
    "pin": null,
    "valueType": "Real",
    "progress": 0.3076923076923077,
    "pid": "present-value",
    "oid": 2,
    "deviceId": 3,
    "oidType": 9,
    "value": "12.0"
    },
    {
    "pin": null,
    "valueType": "Real",
    "progress": 0.38461538461538464,
    "pid": "present-value",
    "oid": 0,
    "deviceId": 3,
    "oidType": 9,
    "value": "21.0"
    },
    {
    "pin": null,
    "valueType": "Real",
    "progress": 0.46153846153846156,
    "pid": "present-value",
    "oid": 1,
    "deviceId": 3,
    "oidType": 9,
    "value": "21.0"
    },
    {
    "pin": null,
    "valueType": "Real",
    "progress": 0.5384615384615384,
    "pid": "present-value",
    "oid": 2,
    "deviceId": 3,
    "oidType": 9,
    "value": "19.0"
    },
    {
    "pin": null,
    "valueType": "Real",
    "progress": 0.6153846153846154,
    "pid": "present-value",
    "oid": 3,
    "deviceId": 3,
    "oidType": 9,
    "value": "17.0"
    },
    {
    "pin": null,
    "valueType": "CharacterString",
    "progress": 0.6923076923076923,
    "pid": "present-value",
    "oid": 1,
    "deviceId": 3,
    "oidType": 9,
    "value": "Comfort"
    },
    {
    "pin": null,
    "valueType": "CharacterString",
    "progress": 0.7692307692307693,
    "pid": "present-value",
    "oid": 2,
    "deviceId": 3,
    "oidType": 9,
    "value": "Eco+"
    },
    {
    "pin": null,
    "valueType": "CharacterString",
    "progress": 0.8461538461538461,
    "pid": "present-value",
    "oid": 3,
    "deviceId": 3,
    "oidType": 9,
    "value": "Vacancy"
    },
    {
    "pin": null,
    "valueType": "UnsignedInteger",
    "progress": 0.9230769230769231,
    "pid": "present-value",
    "oid": 0,
    "deviceId": 3,
    "oidType": 9,
    "value": "2"
    },
    {
    "pin": null,
    "valueType": "UnsignedInteger",
    "progress": 1.0,
    "pid": "present-value",
    "oid": 1,
    "deviceId": 3,
    "oidType": 9,
    "value": "3"
    }
    ]
    },
    "timeout": 3000
    }
    }
``` 
2. 设置指定数据
``` 
http://127.0.0.1:9002/channel/bacnet/execute
POST
LINUX发送
{
	"name": "3",
	"format": "map",
	"send": {
		"resource": "value",
		"method": "set",
		"param": [{
			"object_type": "analog-value",
			"oid": 0,
			"value_type": "Real",
			"value": 3.0
		}, {
			"object_type": "analog-value",
			"oid": 1,
			"value_type": "Real",
			"value": 4.0
		}, {
			"object_type": "analog-value",
			"oid": 2,
			"value_type": "Real",
			"value": 5.0
		}, {
			"object_type": "analog-value",
			"oid": 3,
			"value_type": "Real",
			"value": 6.0
		}]
	},
	"timeout": 3000
}
返回：
{
    "msg": "",
    "code": 200,
    "data": {
        "format": "map",
        "name": "3",
        "send": {
            "resource": "value",
            "method": "set",
            "param": [
                {
                    "object_type": "analog-value",
                    "oid": 0,
                    "value_type": "Real",
                    "value": 3.0
                },
                {
                    "object_type": "analog-value",
                    "oid": 1,
                    "value_type": "Real",
                    "value": 4.0
                },
                {
                    "object_type": "analog-value",
                    "oid": 2,
                    "value_type": "Real",
                    "value": 5.0
                },
                {
                    "object_type": "analog-value",
                    "oid": 3,
                    "value_type": "Real",
                    "value": 6.0
                }
            ]
        },
        "recv": {
            "resource": "value",
            "method": "set",
            "param": [
                {
                    "object_type": "analog-value",
                    "oid": 0,
                    "value_type": "Real",
                    "value": 3.0
                },
                {
                    "object_type": "analog-value",
                    "oid": 1,
                    "value_type": "Real",
                    "value": 4.0
                },
                {
                    "object_type": "analog-value",
                    "oid": 2,
                    "value_type": "Real",
                    "value": 5.0
                },
                {
                    "object_type": "analog-value",
                    "oid": 3,
                    "value_type": "Real",
                    "value": 6.0
                }
            ]
        },
        "timeout": 3000
    }
}
``` 
3. xxxx
4. xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
