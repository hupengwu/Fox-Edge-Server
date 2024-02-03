# coap channel

#### 介绍
coap的channel

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
http://127.0.0.1:9036/channel/coap/client/execute
POST
LINUX发送
{
	"name": "192.168.1.3",
	"format": "map",
	"send": {
		"ip": "192.168.1.3",
		"port": 5683,
		"resource": "data",
		"method": "get",
		"param": [{
				"name": "temp",
				"value": 0
			},
			{
				"name": "water",
				"value": 0
			},
			{
				"name": "number",
				"value": 0
			},
			{
				"name": "vate",
				"value": 0
			},
			{
				"name": "elect",
				"value": 0
			}
		]
	},
	"timeout": 3000
}
返回：
{
    "msg": "",
    "code": 200,
    "data": {
        "format": null,
        "name": "192.168.1.3",
        "send": {
            "ip": "192.168.1.3",
            "port": 5683,
            "resource": "data",
            "method": "get",
            "param": [
                {
                    "name": "temp",
                    "value": 0
                },
                {
                    "name": "water",
                    "value": 0
                },
                {
                    "name": "number",
                    "value": 0
                },
                {
                    "name": "vate",
                    "value": 0
                },
                {
                    "name": "elect",
                    "value": 0
                }
            ]
        },
        "recv": [
            {
                "name": "number",
                "value": "9547"
            },
            {
                "name": "temp",
                "value": 23.0
            },
            {
                "name": "vate",
                "value": 24.0
            },
            {
                "name": "water",
                "value": 124.0
            },
            {
                "name": "elect",
                "value": 3.0
            }
        ],
        "timeout": 3000
    }
}
``` 
3. 添加全部数据
``` 
http://127.0.0.1:9036/channel/coap/client/execute
POST
LINUX发送
{
	"name": "192.168.1.3",
	"format": "map",
	"send": {
		"ip": "192.168.1.3",
		"port": 5683,
		"resource": "data",
		"method": "post",
		"param": [{
				"name": "temp",
				"value": 0
			},
			{
				"name": "water",
				"value": 0
			},
			{
				"name": "number",
				"value": 0
			},
			{
				"name": "vate",
				"value": 0
			},
			{
				"name": "elect",
				"value": 0
			}
		]
	},
	"timeout": 3000
}
返回：
{
    "msg": "",
    "code": 200,
    "data": {
        "format": null,
        "name": "192.168.1.3",
        "send": {
            "ip": "192.168.1.3",
            "port": 5683,
            "resource": "data",
            "method": "post",
            "param": [
                {
                    "name": "temp",
                    "value": 0
                },
                {
                    "name": "water",
                    "value": 0
                },
                {
                    "name": "number",
                    "value": 0
                },
                {
                    "name": "vate",
                    "value": 0
                },
                {
                    "name": "elect",
                    "value": 0
                }
            ]
        },
        "recv": [
            {
                "name": "temp",
                "value": 0
            },
            {
                "name": "water",
                "value": 0
            },
            {
                "name": "number",
                "value": 0
            },
            {
                "name": "vate",
                "value": 0
            },
            {
                "name": "elect",
                "value": 0
            }
        ],
        "timeout": 3000
    }
}
``` 
4. 设置指定数据
``` 
http://127.0.0.1:9036/channel/coap/client/execute
POST
{
	"name": "192.168.1.3",
	"format": "map",
	"send": {
		"ip": "192.168.1.3",
		"port": 5683,
		"resource": "data",
		"method": "put",
		"param": [{
				"name": "temp",
				"value": 0
			},
			{
				"name": "water",
				"value": 0
			},
			{
				"name": "number",
				"value": 0
			},
			{
				"name": "vate",
				"value": 0
			},
			{
				"name": "elect",
				"value": 0
			}
		]
	},
	"timeout": 3000
}
返回：
{
    "msg": "",
    "code": 200,
    "data": {
        "format": null,
        "name": "192.168.1.3",
        "send": {
            "ip": "192.168.1.3",
            "port": 5683,
            "resource": "data",
            "method": "put",
            "param": [
                {
                    "name": "temp",
                    "value": 0
                },
                {
                    "name": "water",
                    "value": 0
                },
                {
                    "name": "number",
                    "value": 0
                },
                {
                    "name": "vate",
                    "value": 0
                },
                {
                    "name": "elect",
                    "value": 0
                }
            ]
        },
        "recv": [
            {
                "name": "temp",
                "value": 0
            },
            {
                "name": "water",
                "value": 0
            },
            {
                "name": "number",
                "value": 0
            },
            {
                "name": "vate",
                "value": 0
            },
            {
                "name": "elect",
                "value": 0
            }
        ],
        "timeout": 3000
    }
}
``` 
5. 删除指定数据
``` 
http://127.0.0.1:9036/channel/coap/client/execute
POST
{
	"name": "192.168.1.3",
	"format": "map",
	"send": {
		"ip": "192.168.1.3",
		"port": 5683,
		"resource": "data",
		"method": "delete",
		"param": [{
				"name": "temp",
				"value": 0
			},
			{
				"name": "water",
				"value": 0
			},
			{
				"name": "number",
				"value": 0
			},
			{
				"name": "vate",
				"value": 0
			},
			{
				"name": "elect",
				"value": 0
			}
		]
	},
	"timeout": 3000
}
返回：
{
    "msg": "",
    "code": 200,
    "data": {
        "format": null,
        "name": "192.168.1.3",
        "send": {
            "ip": "192.168.1.3",
            "port": 5683,
            "resource": "data",
            "method": "put",
            "param": [
                {
                    "name": "temp",
                    "value": 0
                },
                {
                    "name": "water",
                    "value": 0
                },
                {
                    "name": "number",
                    "value": 0
                },
                {
                    "name": "vate",
                    "value": 0
                },
                {
                    "name": "elect",
                    "value": 0
                }
            ]
        },
        "recv": [
            {
                "name": "temp",
                "value": 0
            },
            {
                "name": "water",
                "value": 0
            },
            {
                "name": "number",
                "value": 0
            },
            {
                "name": "vate",
                "value": 0
            },
            {
                "name": "elect",
                "value": 0
            }
        ],
        "timeout": 3000
    }
}
``` 

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
