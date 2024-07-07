fox-edge-server-persist-iotdb

#### 介绍

持久化服务的**IoTDB**版，它跟fox-edge-server-persist-service的最大区别，就是它的历史数据，是保存在
**IoTDB**中的，而persist-service是保存咋**MySQL**中的。

IoTDB是由清华大学贡献给阿帕奇基金会的时序数据库，它的开发和维护团队是国内团队。
国企、政府的客户，普遍有**信创**的要求，所以有些国企集成商指明要使用这种国产数据库，作为准入措施。

#### 注意

MySQL是一种关系数据库，关系数据库是表单式的数据结构。而IoTDB是一种时序数据库，时序数据库的结构跟关系数据库的差异很大。


<img src="https://img-blog.csdnimg.cn/8b72d1ac74b347c394939b9dc1936561.png" style="width:50%;height:auto;">

[《IoTDB相关介绍》](https://blog.csdn.net/weixin_43705457/article/details/132229089)

它的数据模型：【设备】-【采样对象】-【时间:数据】

简单理解，它就是把一个个的设备，分为对应的一个个文件，然后，在文件里面存储多个传感器（比如温度）的数据，每个数据它称为时序。
在这个一个时序里，保存了连续性的【时间:数据】。

这种充分面向IoT设备-传感器的业务模型，并在每个传感器按时间保存一批连续性的数据。
这种高度明确的业务特征，使得可以进行针对性的数据压缩和高效的数据处理。

但是，这个优势是通过高度定制的代价换来的，也就是它不像MySQL一样具有通用性。

## 限制

通过上面对IoTDB的背景信息了解，persist-iotdb版本有如下限制

1、persist-iotdb只负责把数据写入IoTDB之中，并进行周期性删除过期数据。

2、fox-edge不提供界面管理功能，使用者需要自己实现符合自己需要的界面管理功能。

3、persist-iotdb只负责数据的写入，使用者对数据的查询，需要使用IoTDB提供的组件包iotdb-session的SessionPool
直接访问IoTDB，进行自己的二次开发工作。

4、IoTDB在灵狐的实际测试中发现，对内存的需求比较高（比如它至少需要占用2G内存），用户在硬件规格选型阶段，要为它提供足够的内存空间。

## 约定

### 设备ID

以每个设备作为一个独立的"设备表"，它的表名称为【root.tb_device_history.device_xxx】,其中xxx是Fox-Edge中的设备对象的Long型ID

例如：有个设备，名称为“CE+T UPS设备-8”，ID是1193，那么这个设备的数据表名称为

root.tb_device_history.device_1193

### 采样对象

设备下的每一个对象称为一个时序，也可以认为是设备表的字段，它的时序名称为【root.tb_device_history.device_xxx.oid_yyy】,
其中xxx是Fox-Edge中的设备对象的Long型ID，而yyy是采样对象的类型表tb_device_mapper中的Long型ID

例如：有个设备，名称为“CE+T UPS设备-8”，ID是1193

该设备类型下面有个采样数据，名称为“逆变器14输出电流”，在tb_device_mapper中表中的ID是348，那么该对象的名称

root.tb_device_history.device_1193.oid_348

## 常用SQL

IoTDB提供了SQL风格的查询接口，下列常用语句范例

### 查询采样对象的数据

范例：
select oid_443 from  root.tb_device_history.device_1193;

### 删除设备级别的过期数据

范例：
delete from root.tb_device_history.device_1193.* where time <= 1715394387789;
