# link-tcp2tcp

## 介绍

``` 
链路服务名：link-tcp2tcp
工程名称：fox-edge-server-link
说明：link-tcp2tcp链路服务器，用于支持上位机与现场设备，需要fox-edge充当透传链路的场景。

需求：
在有些物联网项目之中，南向除了智能设备在Fox-Edge处进行现场解析之外，还存在一些视频监控的透传到云端，
部分存量设备在云端侧报文解析之类的场景。

这就形成了一种既有部分设备在Fox-Edge处进行现场解析，又有部分设备云端处理的混合组网方案。
两边的通信方式，往往采用全双工的交互方式，也就是上行和下行是各发各的，在通信层面并没有对应关系。
它们的逻辑对应关系，发生在上层的应用层之中。

方案：
Fox-Edge通过Link-Tcp2Tcp 服务，为北向的上位机/云服务与南向的智能设备之间，建立一个双向透传链路。
这样北向的上层应用/云端服务和南向的智能设备，进行直接会话了。

``` 

## 组网

<img src="http://docs.fox-tech.cn/_images/link-tcp2tcp.jpg" style="width:100%;height:auto;">

## 源码

- fox-edge-server-channel-tcp-server

[源码](https://gitee.com/fierce_wolf/fox-edge-server/tree/master/fox-edge-server-channel/fox-edge-server-link-tcp2tcp)

## 使用方法

#### 1、安装服务

<img src="http://docs.fox-tech.cn/_images/link-tcp2tcp-app.png" style="width:100%;height:auto;">


#### 2、配置参数

<img src="http://docs.fox-tech.cn/_images/link-tcp2tcp-config.png" style="width:100%;height:auto;">


``` example

{
     "remote": {
          "host": "192.168.1.7",
          "port": 502
     },
     "serverPort": 802
}

serverPort：北向服务端口
remote：待连接的南向设备IP地址和端口

``` 

#### 3、测试效果

使用第三方测试工具，模拟设备从上下两个方向，各自发送报文，可以从截图中看到，两边可以各自透传报文。

<img src="http://docs.fox-tech.cn/_images/link-tcp2tcp-test1.png" style="width:100%;height:auto;">
<img src="http://docs.fox-tech.cn/_images/link-tcp2tcp-test2.png" style="width:100%;height:auto;">
