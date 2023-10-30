# fox-edge-server-thingsboard

## 介绍

``` 
thingsboard是一个国外市场占用率比较高的2C类物联网云平台。
该云平台作为一个2C类物联网云平台，它的能力比较弱，只提供了状态量的推送接口。
并不支持告警数据、事件数据、日志数据等各种数据模型。

所以，fox-edge只将DeviceValue数据推送到thingsboard之中

``` 

## 方案

thingsboard提供了下列接口，进行数据的推送


``` 
curl -v -X POST http://demo.thingsboard.io/api/v1/iHtC96shxyCwa05jnOpi/telemetry --header Content-Type:application/json --data "{"系统输出功率":889,"逆变器01输出功率":304,"逆变器03输出功率":281}"

``` 