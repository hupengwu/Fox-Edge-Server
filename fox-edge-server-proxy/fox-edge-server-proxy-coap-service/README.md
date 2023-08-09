# fox-edge-server-proxy-coap-service

## 介绍
> restful接口转coap接口，方便用户通过postman工具发送coap请求的测试工具

## 软件架构
软件架构说明


## 安装教程


## 使用说明
### 说明
> 使用restful和coap接口的风格非常相似，所以将对restful请求做了个简单的转发，即可发送coap请求
### 范例
> 准备发送coap的get请求: coap://192.168.1.3:5683/time，那么在restful输入http://127.0.0.1:9003/coap://192.168.1.3:5683/time的get请求，就可以发出该请求
### 1.  发送数据
``` 

http://127.0.0.1:9003/coap://192.168.1.3:5683/time
GET
返回：
{
"msg": "ok",
"code": 200,
"data": "2022-06-05 13:28:16"
}
```
### 2.  发送数据
``` 
http://127.0.0.1:9003/coap://192.168.1.3:5683/time
POST
{
	"name": "ttyS1",
	"send": "b0 01 00 fe fe",
	"timeout": 5000
}
返回：
{
    "msg": "操作成功",
    "code": 200,
    "data": {
        "name": "ttyS1",
        "send": "b0 01 00 fe fe",
        "recv": "b08103131613fefe",
        "timeout": 5000
    }
}
``` 
3.  xxxx
4.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
