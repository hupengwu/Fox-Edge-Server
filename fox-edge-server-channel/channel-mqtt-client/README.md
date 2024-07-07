# fox-mqtt

#### 介绍
mqtt连接器

#### 软件架构
软件架构说明


#### 安装教程

1.  该服务职能在LINUX环境运行，所以需要远程调试
2.  远程调试的配置，不要参考简单网上的配置，因为linux的默认端口是环回端口，无法远程连接
3.  linux侧的启动命令，必须强制指明IP地址，例如:
    java -jar fox-edge-server-channel-mqtt-client-service-1.0.0.jar

#### 使用说明

1. 发现远端设备
``` 
http://127.0.0.1:9002/channel/mqtt/discover
GET
返回：
{
    "msg": "操作成功",
    "code": 200
}
``` 

2. 查询指定数据
``` 
http://127.0.0.1:9024/channel/mqtt/execute
POST
LINUX发送
{
	"name": "/v1/device/request/12345",
    "format": "hex",
	"send": "7E3230303134303431453030323030464433440D",
	"timeout": 2000
}
返回：
{
    "msg": "",
    "code": 200,
    "data": {
        "format": "hex",
        "name": "/v1/device/request/12345",
        "send": "7E3230303134303431453030323030464433440D",
        "recv": "7e 32 30 30 31 34 30 30 30 46 30 33 45 31 31 30 31 42 36 37 42 36 34 34 33 37 45 33 35 35 42 34 33 36 36 34 38 36 32 34 33 32 30 32 30 32 30 32 30 30 30 35 45 32 31 34 33 34 30 32 30 32 30 32 30 32 30 32 30 32 30 32 30 32 30 46 30 46 44 0d 0d ",
        "timeout": 2000
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
