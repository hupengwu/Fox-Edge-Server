# fox-edge-server-protocol-telecom-core

#### 介绍
``` 
中国电信总局的协议框架。
在中国电信、中国移动的基站行业，有大量的设备供应商采用这种协议框架，进行通信协议接口的开发，作为电信行业的准入接口。
电信总局协议框架，至少一套协议框架，具体到各个厂家的设备，就有各自具体的操作命令。
所以，在写解码器大的时候，可以直接引用这个框架，然后将电信行业的设备厂商的命令，进行报文的编码/解码，简化开发
``` 

#### 范例
``` 
范例1：
fox-edge-server-protocol-zxdu58解码器
1. 在POM中引入fox-edge-server-protocol-telecom-core包
2. 为获取告警、获取数据等命令，按Fox-Edge的方式，写ZXDU58ProtocolGetACAlarm、ZXDU58ProtocolGetACData等作为解码器

范例2：推荐
fox-edge-server-protocol-bass260zj解码器
1. 在POM中引入fox-edge-server-protocol-telecom-core包
2。 为获取门禁记录、获取告警，编写BASS260ZJGetCardAlarm、BASS260ZJGetCardRecord解码器
``` 
