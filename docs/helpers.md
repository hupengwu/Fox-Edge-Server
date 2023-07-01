# 常见问题


> 操作过程中，会遇到的常见问题和定位

## 问题定位

###### 设备数据没有更新
- 登录redis，检查持久化服务是否有接收到保存数据的请求，正常状况应该有大量的保存数据报文更新

  ```sh
	#在linux下输入命令
	redis-cli -h 127.0.0.1 -p 6379
	
	>auth 12345678
	>subscribe topic_persist_request_public
  ```
  ![image](http://docs.fox-tech.cn/_images/cjwt001.png)

