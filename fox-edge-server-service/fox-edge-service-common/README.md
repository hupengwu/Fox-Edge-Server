# fox-edge-service-common

#### 介绍
``` 
fox-edge的服务分为kernel、system、service三级服务，kernel、system是fox-edge开发维护的项目，service
是各个用户自行开发和维护的项目。
考虑到用户自行开发各自的service的时候，会使用到fox-edge的公共特性，所以将一些需要用到的fox-edge公共特性抽取
出来，避免重复性的开发
``` 

#### 目录结构
``` 
├─bin           ---------fox-edge各服务的jar格式可执行文件
│  ├─kernel     ---------内核服务:gateway和manange服务，它们是fox-edge的最简安装
│  └─system     ---------系统服务:fox-edge开发团队开发的通用性服务，构成了fox-edge平台的基本框架
│  ├─service    ---------业务服务:第三方开发者团队开发的一下自定义服务，二次开发的服务部署在这个位置
├─conf          ---------服务的配置目录，有需要进行本地文件配置的服务，可以将conf格式保存在这边
├─dist          ---------前端页面的脚本，nginx会代理这些网页脚本
├─jar           ---------解码器
│  ├─decoder    ---------设备协议解码器目录，比如用户开发的DLT645、MODBUS等协议解码器部署在该目录
│  └─trigger    ---------触发器解码器目录，比如用户开发的均值化处理，就部署在这个目录
├─repository    ---------本地仓库目录，从fox-cloud下载的服务和解码器，会先保存到这个本地目录
├─shell         ---------服务启动脚本，实际上每一个服务的bin程序都是通过该目录下的各自shell脚本启动
│  ├─kernel
│  ├─service
│  ├─system
└─template
``` 
#### 业务流程
1. 用户在用户界面的仓库下载服务，那么manage服务会从服务器上下载安装包到本地仓库
2. 用户在用户界面的仓库安装服务，那么manage服务会将本地的安装包解压安装到bin、conf、shell目录
3. 用户在用户界面的服务配置为启动，那么manage服务会通过shell目录的脚本启动该服务
4. manage在启动服务的时候，会为服务传递redis、mysyl参数给服务，也会从shell提取开发者预制在shell脚本中的信息
5. manage在通过shell脚本启动服务进程后，会给服务分配一个动态的服务端口，然后到gateway去注册服务路由
6. gateway会将客户端发过来的请求，根据路由注册信息，转发给对应的服务

#### 注意事项
1. 在shell脚本中，必须填写app_shell和app_name参数，并且shell文件与app_shell指明的文件同名
2. 在xxx-service.sh中，系统默认分配的路由为/service/xxx/**
```
例如：
period-service.sh分配的路由是 /service/period/**
proxy-redis-topic.sh分配的路由是 /service/proxy-redis-topic/**"
也就是说,xxx-service.sh格式的服务，默认去掉-service这个后缀，其他的采用/service/xxx/**的原始路径
这样做的目的是为了更服务一般开发者的习惯
``` 


