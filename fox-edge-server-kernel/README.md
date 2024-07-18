# fox-edge-server-kernel

## 介绍
内核服务，它包括三个模块：gateway、auth、system

#### gateway

spring网关

#### system

管理服务

## 工作模式

Fox-Edge的部署环境是非常灵活的，比如在边缘端、比如在云端、比如在k8s环境，这就决定了这三个模块的组合方式。

### 单机工作模式

在边缘端的部署状况下，通常是单机部署，此时设备的资源比较有限，不会部署nacos、k8s这类巨无霸。
通常是部署两个gateway和system的单机版本，配置信息在application.yml文件当中。

注意：这是单机版非常重要的特点：

此时，gateway内置了auth能力和router能力，system会将自己启动的其他服务，自动向gateway注册路由信息。



部署方式：选择两个local版本的jar文件镜像部署

1、fox-edge-server-kernel-gateway-service

2、fox-edge-server-system-service

这种方式，就是仅凭借上述两个服务，fox-edge就具备自动注册路由、登录鉴权的完整的能力，非常的节省内存。

### 云服务模式1

在云端部署的情况下，云端通常会部署一个nacos，配置信息，各服务的配置放在nacos当中，
服务启动的时候，会从bootstrap.yml装载nacos的地址信息，然后进一步到nacos去读取配置信息。

此时，gateway内置了auth能力，用户需要在nacos的gateway配置中手动配置system服务的router信息

部署方式：选择gateway和system的nacos版本的jar文件镜像部署

1、fox-edge-server-kernel-gateway-nacos

2、fox-edge-server-system-nacos

### 云服务模式3

在k8s的云端部署情况下，fox-edge可能跟客户的其他服务一起部署，那么此时很可能会有一个已经存在的公共gateway

然后，用户还有一个自己公司独立的公共鉴权服务auth。

此时，就不能重复部署fox-edge的gateway了，只需要部署system进行

对于fox-edge的鉴权，用户完全使用自己公司的网关和公共鉴权系统

此时，然后在客户的公共gateway上内置，用户需要在nacos的gateway配置中手动配置system服务的router信息

- 如果用户的环境，独立部署了一套nacos

部署方式1：选择system的nacos版本的jar文件镜像部署

1、fox-edge-server-system-nacos

- 如果用户的环境，没有部署了一套nacos，而是使用k8s对配置管理

部署方式2：选择system的local版本的jar文件镜像部署

1、fox-edge-server-system-service
