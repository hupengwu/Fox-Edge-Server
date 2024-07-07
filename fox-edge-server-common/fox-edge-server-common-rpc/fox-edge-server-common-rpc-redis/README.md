# fox-edge-server-common-rpc-redis

### 介绍

Fox-Edge的各个服务之间，通过redis进行进行彼此的通讯。

这个模块，提供了生产者/消费者的消息通讯能力，可以通过对Redis的List和Value的封装，进行各服务之间的RPC

使得Fox-Edge，能够适合配合K8S、K3S这类Docker化的云原生的服务管理方式。

这样，Fox-Edge既能够在现场侧的边缘端主机上运行，也能够在管理中心、云端的Docker环境下运行


