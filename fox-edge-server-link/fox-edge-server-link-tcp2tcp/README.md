# channel-tcp-server

## 介绍

``` 
通道服务名：tcp-server
工程名称：fox-edge-server-channel-tcp-server
说明：tcp-server通道服务器，用于支持现场设备作为tcp-client主动上报的场景。
在现实项目中，有很多设备是作为TCP客户端，主动向服务器发起连接，然后上报数据，并接收服务器的操作请求。
在这种通信模式下，Fox-Edge是作为TCP的服务端，设备是作为TCP的客户端。
两边的通信方式，往往采用全双工的交互方式，也就是上行和下行是各发各的，在通信层面并没有对应关系。
它们的逻辑对应关系，发生在上层的应用层之中。

针对这个业务场景，灵狐开发了tcp-server这种通道服务，来支持该场景。
进一步考虑到用户的现场设备可能有很多种类型，所以tcp-server支持同时开启多服务端口，各自接收一种设备类型的数据。

``` 

## 组网

<img src="http://docs.fox-tech.cn/_images/channel-tcp-server-01.jpg" style="width:100%;height:auto;">

## 源码

- fox-edge-server-channel-tcp-server

[源码](https://gitee.com/fierce_wolf/fox-edge-server/tree/master/fox-edge-server-channel/fox-edge-server-channel-tcp-server)

#### 1、操作方法

该服务的操作方法，只有单工的**publish**和**report**两种模式，没有主从半双工的execute模式。<br>
这样设计的目的，是因为execute只适合一问一答式的传统自动化设备。<br>
但是，对于很多会采用tcp的设备来说，它们的设计更多的是采用全双工的方式。<br>
换句话说，在传输层面，上行和下行是彼此独立的，上行和下行，可以认为是两条各自独立的单向通道。<br>
所以，tcp-server的操作方法，也跟着用**publish**和**report**来分别对应上行和下行数据的传输。<br>

#### 2、报文协议

tcp是面向流的传输协议，想要在上面传输会话动作，必然会出现所谓的**粘包**问题。<br>
所以，设备制造厂商，它们的设备协议上，会定义有**报头**和**报长**两种信息，来帮助分包。<br>
如果它们的设备没有这种**报头**和**报长**两这两个关键信息，别白费力气了，赶紧找设备厂商解决，或者直接更换设备厂商。<br>

``` example

中科图灵的安防设备，它的通信报文格式为
报头：4 字符， ‘2’ ’4’ ’2’ ’4’；
通信类型：1 字符
设备类型：1 字符
长度：2 字符
数据区：N字符
包尾：2 字符，’A’’ A’

报文范例：
2424 08 43 867572058700527 89861121245014174191 0058009b00b3006400b6019e110200 aa

2424是固定的报头，28是报长，这个适合就可以通过 这两个信息，对粘合成一长串的报文，进行准确的断句。
``` 

#### 3、拆包接口

tcp-server提供了自动拆包的接口：cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler<br>
开发者应该写一个解码器，并派生一个子类，为tcp-server指出**报头**和**报长**的特征，那么tcp-server在接收到数据流之后，<br>
会使用SplitMessageHandler的派生类自动进行拆包操作。

#### 4、身份识别

线下设备主动连接tcp-server的适合，会通过报文告知上层应用，自己的**身份特征**信息。<br>
如何从报文中获得**身份特征**信息，同样需要解码器从中提取。<br>

``` example

中科图灵的安防设备，它的通信报文格式为
报头：4 字符， ‘2’ ’4’ ’2’ ’4’；
通信类型：1 字符
设备类型：1 字符
长度：2 字符
IMEI，15 字符
ICCID，20 字符
数据区：N字符
包尾：2 字符，’A’’ A’

报文范例：
2424 08 43 867572058700527 89861121245014174191 0058009b00b3006400b6019e110200 aa

867572058700527是它的IMEI，89861121245014174191是它的ICCID，这两个标识信息合并成一个字符串"867572058700527:89861121245014174191"，
就可以辨别出下面一堆同型号的现场设备是具体哪个设备。
``` 

#### 5、身份接口

tcp-server提供了自动识别的接口：cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler<br>
开发者应该写一个解码器，并派生一个子类，为tcp-server指出**身份特征**特征，那么tcp-server在接收到数据流之后，<br>
会使用ServiceKeyHandler的派生类自动进行身份的自动提取，然后自动维护某个TCP连接和业务通道的关联关系。<br>

#### 6、通道管理

用户在通道界面创建一个tcp-server类型的通道之后，在通道上配置好**身份特征**特征，那么Fox-Edge就会遵循下列操作流程<br>
【现场设备】《==TCP==》【通道服务】（tcp连接-身份特征-通道名）《==Channel==》【设备服务】<br>
这样，上层应用就能够以channel的方式，进行跟远程的现场设备，基于tcp的方式进行交互操作了。

#### 7、服务配置

为channel-tcp-server创建一个serverConfig的配置<br>
jarFile：解码器所在的文件位置<br>
splitHandler：拆包类的完整名称<br>
keyHandler：提取设备身份信息的完整名称<br>

<img src="http://docs.fox-tech.cn/_images/channel-tcp-server-02.png" style="width:100%;height:auto;">

``` json
{
  "decoderList": [{
    "decoder": [{
      "jarFile": "\\jar\\decoder\\fox-edge-server-protocol-zktl-air6in1-1.0.2.jar"
    }],
    "keyHandler": "cn.foxtech.device.protocol.v1.zktl.air6in1.handler.ZktlServiceKeyHandler",
    "serverPort": 9301,
    "splitHandler": "cn.foxtech.device.protocol.v1.zktl.air6in1.handler.ZktlSplitMessageHandler"
  }, {
    "decoder": [{
      "jarFile": "\\jar\\decoder\\fox-edge-server-protocol-zktl-air5in1-1.0.2.jar"
    }],
    "keyHandler": "cn.foxtech.device.protocol.v1.zktl.air5in1.handler.ZktlServiceKeyHandler",
    "serverPort": 9302,
    "splitHandler": "cn.foxtech.device.protocol.v1.zktl.air5in1.handler.ZktlSplitMessageHandler"
  }]
}
```