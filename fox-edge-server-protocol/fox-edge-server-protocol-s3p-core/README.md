# fox-edge-server-protocol-eaton-s3p-core

## 介绍

伊顿公司，成立于1911年，作为全球智能动力管理公司，主要业务覆盖中低压配电、关键电能质量、工业控制、电力线路保护、
恶劣危险环境解决方案、结构解决方案及配线等能源管理领域。

该公司生产的电源产品，被很多生产电源的公司贴牌生产，比如中恒、艾默生。

所以，它们的通信协议采用的是同一种通信框架s3p

## 框架

S3P协议，分为物理层/数据链路层/应用层，三层协议

上位机跟设备之间进行通信，需要先发起连接，然后再进行后续的应用层操作。

### 链路层协议格式
起始标记|从机地址|控制字节|数据长度|数据区|校验码

1字节     2字节     1字节  1字节  N字节 2字节   

