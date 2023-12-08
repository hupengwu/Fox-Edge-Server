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

1. 打开串口
``` 
http://192.168.241.128:9001/serialport/reload
GET
返回：
{
    "msg": "操作成功",
    "code": 200
}
``` 
2. 发送数据
``` 
http://192.168.241.128:9001/serialport/send
POST
LINUX发送
{
	"name": "ttyS1",
	"format": "hex",
	"send": "b0 01 00 fe fe",
	"timeout": 5000
}
返回：
{
    "msg": "操作成功",
    "code": 200,
    "data": {
        "name": "ttyS1",
        "format": "hex",
        "send": "b0 01 00 fe fe",
        "recv": "b08103131613fefe",
        "timeout": 5000
    }
}
WINDOWS发送
{
	"name": "COM1",
    "format": "hex",
	"send": "b0 01 00 fe fe",
	"timeout": 2000
}
返回：
{
    "msg": "操作成功",
    "code": 200,
    "data": {
        "name": "COM3",
        "format": "hex",
        "send": "b0 01 00 fe fe",
        "recv": " b0 810 3 13 16 13 fe fe",
        "timeout": 3000
    }
}
``` 

#### 参与贡献


#### 特技
