# fox-edge-server-iot-whzktl

## 介绍

``` 
whzktl是一个武汉中科图灵的云平台

该服务会不断的讲DeviceValue数据和DeviceRecord数据推送给给中科图灵的云端。

``` 

## 方案


``` 
获得同步需要的时间戳

fox-edge发送：Fox-Edge之前传递给云端最大的数据库记录序号是什么？
/zktl/gateway/BFEBFBFF000906A3/up/device/record/timestamp
{
	"uuid": "ddc901a8a24440c3a1b5bb5f8fc1f635",
	"data": {
		"edgeId": "BFEBFBFF000906A3",
		"entityType": "deviceRecord"
	}
}

whzktl-cloud返回：Fox-Edge之前传递给云端最大的数据库记录序号是21.
备注：如果没有，那么返回默认值0；
注意：uuid原样返回，fox-edge好判断是不是刚才自己发的数据
/zktl/gateway/BFEBFBFF000906A3/down/device/record/timestamp
{
	"uuid": "4e07580e1a364b28872411178c7ec437",
	"code": 200,
	"msg": "",
	"data": {
		"edgeId": "BFEBFBFF000906A3",
		"entityType": "deviceRecord",
		"lastId": 21
	}
}
``` 

``` 
fox-edge发送：Fox-Edge之前传递给云端最大的数据库记录序号是什么？
注意：武汉中科图灵的云平台，在时间戳查询下中，要把数据库中最大的记录ID号返回，比如下面的这个229

/zktl/gateway/BFEBFBFF000906A3/up/device/record/rows
{
	"edgeId": "BFEBFBFF000906A3",
	"data": {
		"insert": [{
			"deviceType": "BASS260ZJ",
			"createTime": 1663749852263,
			"recordName": "刷卡记录",
			"updateTime": 1663749852263,
			"id": 228,
			"deviceName": "浙江移动-丽水移动-丹霞山5号基站-4号电源设备:623",
			"recordData": "{\"event\": \"刷卡进门\", \"cardId\": \"34f2bf4b\", \"datetime\": \"2007-03-13 12:58:38\", \"recordType\": \"刷卡记录\"}",
			"manufacturer": "广东高新兴"
		}, {
			"deviceType": "BASS260ZJ",
			"createTime": 1663749878272,
			"recordName": "刷卡记录",
			"updateTime": 1663749878272,
			"id": 229,
			"deviceName": "浙江移动门禁设备",
			"recordData": "{\"event\": \"刷卡进门\", \"cardId\": \"34f2bf4b\", \"datetime\": \"2007-03-13 12:58:38\", \"recordType\": \"刷卡记录\"}",
			"manufacturer": "广东高新兴"
		}]
	},
	"entityType": "DeviceRecordEntity"
}

whzktl-cloud：不需要返回，可以通过查询序号，发送最新数据，来自动维护该发送什么，不成功也无所谓，关键序号别弄错了

``` 