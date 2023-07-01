# fox-edge-server-common-utils-iec104-core

#### 介绍
``` 
背景：
IEC104规约是类似MODBUS的通信协议，它跟MODBUS一样，只定义了【操作】+【数据地址】的框架
各电力设备厂商，可以基于这种通信框架，定义自己的应用层通信协议

IEC104规约是层次性模型：TCP链路层/IEC104链路层/IEC104会话层/厂商应用层

IEC104规约的各级实体，各枚举、编码器、解码器的定义。
它提供了IEC104规约定义的数据结构处理能力，并不包含交互流程，目的是为了提高可复用性。
那么其他开发者可以基于core部件，自行开发主站/从站的通信交互流程
``` 
#### 软件架构
``` 
1、IEC104的通信协议层次
  Master                         Slaver
应   用   层<-------------------->应   用   层
IEC104会话层<-------------------->IEC104会话层
IEC104链路层<-------------------->IEC104链路层
TCP 链 路 层<-------------------->TCP 链 路 层

2、建议的开发部件
2.1 TCP 链 路 层
Netty，高性能TCP异步无阻塞通信开发框架

2.2 IEC104链路层
编码/解码器：fox-edge-server-common-utils-iec104-core
链路管理器：fox-edge-server-common-utils-iec104-master
这两个部件会跟Slaver建立和维护iec104链路

2.3 IEC104会话层
会话框架：fox-edge-server-channel-iec104-master
这个部件会通过将上层的会话，打包成APDU实体，提交给iec104链路层转发

``` 
