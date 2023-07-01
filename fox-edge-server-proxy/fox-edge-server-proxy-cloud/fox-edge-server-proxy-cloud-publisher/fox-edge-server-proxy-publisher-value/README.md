# fox-edge-server-publisher-service

#### 介绍
``` 
云端发布器
它的作用是将边缘节点的数据，进行发布到云端的redis/mongodb
它在将边缘节点的数据，统一汇聚到云端的redis后，方便云端数据进行统一查询处理
它在实体同步线程EntityManageScheduler中，对本地Redis/Cache/云端Redis，进行串行操作，
因为是串行操作，所以它不需要考虑多线程的并发带来的各种复杂性的业务顺序和安全问题
``` 

#### 软件架构

``` 
四种种同步模式

模式1：增量Record类型的同步MySQL的数据到云端
该模式是通过周期比对云端和本地最新的数据的记录ID，以此判定是否需要对云端进行增量同步
实现者是RecordEntitySynchronizer
对应的是DeviceRecordEntity和OperateRecordEntity
背景：该表的数据是不断递增的，数据量大，所以采用分页的增量同步，而且这个数据比较重要，所以它重新同步的时候，云端会进行备份旧数据

模式2：增量Logger类型的同步MySQL的数据到云端
该模式是通过周期比对云端和本地最新的数据的记录ID，以此判定是否需要对云端进行增量同步
实现者是LoggerEntitySynchronizer
对应的是DeviceHistoryEntity
背景：该表的数据是不断递增的，数据量大，所以采用分页的增量同步，该数据的安全性第，不会形成备份数据

模式3：全年同步MySQL的数据到云端
该模式是通过周期比对云端和本地最新的时间戳，以此判定是否需要对云端进行全量同步
实现者是ObjectEntitySynchronizer
对应的是DeviceObjectEntity和TriggerObjectEntity
背景：该表的数据在变更的时候会发生增删改，表的数据比较大，但是基本不会变化，所以采用变更就全量同步的方案

模式4：增量同步Redis的Entity数据的数据到云端
该模式是通过订阅本地EntityManager的Redis数据变更，对云端数据进行增删改
实现者是StatusEntityPublisherNotify
对应的是其他在redis的Entity
背景：这些实体的数据量很小，但是每个数据会进行来回刷新，所以采用捕获变更，并将这些更新发布到云端的方案

``` 

#### 使用说明

``` 
1、查询时间戳
http://127.0.0.1:9501/cloud/receiver/timestamp
POST
发送：
{
    "edgeId": "BFEBFBFF000406E3",
    "entityTypeList": ["DeviceEntity"]
}
返回：
{
    "msg": "操作成功",
    "code": 200,
    "data": {
        "DeviceEntity": "1662203638314"
    }
}

2、查询reset标记
http://127.0.0.1:9501/cloud/receiver/reset
POST
发送：
{
    "edgeId": "BFEBFBFF000406E3",
    "operate": "get",
    "entityTypeList": ["DeviceEntity","DeviceValueEntity"]
}
返回：
{
    "msg": "操作成功",
    "code": 200,
    "data": {
        "DeviceEntity": false,
        "DeviceValueEntity": false
    }
}

3、设备reset标记
http://127.0.0.1:9501/cloud/receiver/reset
POST
发送：
{
    "edgeId": "BFEBFBFF000406E3",
    "operate": "set",
    "entityTypeList": ["DeviceEntity","DeviceValueEntity"]
}
返回：
{
    "msg": "操作成功",
    "code": 200,
    "data": {}
}

3、发送同步数据-初始化
http://127.0.0.1:9501/cloud/receiver/entity
POST
发送：
{
    "edgeId": "边缘服务器-01号",
    "entityType": "DeviceEntity",
    "timeStamp": "1b1df78266b9449c9d5705f821a2b4c2",
    "data": {
        "reset": [
            {
                "deviceType": "刘日威解码器",
                "createTime": 1652275102255,
                "commTime": 1652275102255,
                "channelType": "simulator",
                "updateTime": 1652275102255,
                "channelName": "channel-simulator",
                "id": 1,
                "deviceName": "阿威的单板"
            }
        ]
    }
}
返回：
{
    "msg": "操作成功",
    "code": 200
}


3、发送同步数据-初始化
http://127.0.0.1:9501/cloud/receiver/entity
POST
发送：
{
    "edgeId": "边缘服务器-01号",
    "entityType": "DeviceEntity",
    "timeStamp": "1b1df78266b9449c9d5705f821a2b4c2",
    "data": {
        "insert": [
            {
                "deviceType": "刘日威解码器",
                "createTime": 1652275102255,
                "commTime": 1652275102255,
                "channelType": "simulator",
                "updateTime": 1652275102255,
                "channelName": "channel-simulator",
                "id": 1,
                "deviceName": "阿威的单板"
            }
        ],
        "update": [
            {
                "deviceType": "BASS260ZJ",
                "createTime": 1652275102255,
                "commTime": 1652275102174,
                "channelType": "simulator",
                "updateTime": 1652275102174,
                "channelName": "channel-simulator",
                "id": 2,
                "deviceName": "浙江移动门禁设备"
            }
        ],
        "delete": ["浙江移动-丽水移动-丹霞山5号基站-4号电源设备:748","浙江移动-丽水移动-丹霞山5号基站-4号电源设备:742"
        ]
    }
}
返回：
{
    "msg": "操作成功",
    "code": 200
}

``` 

