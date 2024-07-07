# fox-edge-server-common-utils-iec104-master

#### 介绍
``` 
1、在fox-edge-server-common-utils-iec104-core的基础上，建立主站对从站的交互流程。
2、基于Netty作为通信框架，Netty的异步性能高，并且Netty能很好解决TCP的粘包、碎包问题
3、iec104其实是一种采用一问多答的全双工通信协议，虽然主站/从站是这么命名的，但是实际上
  它们是对等通信，也就是从站也会主动问询主站，所以不能简单采用主从通信方式去通信。
``` 
#### 软件架构
软件架构说明

