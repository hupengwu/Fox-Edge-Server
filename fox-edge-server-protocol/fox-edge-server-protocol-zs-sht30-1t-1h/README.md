# fox-edge-server-protocol-zs-sht30-1t-1h

## 介绍
> 制造商：中盛科技

> 产品名称：温湿度采集模块

> 产品型号：ZS-SHT30-1T-1H-485

> 官网连接：http://www.zskjdg.com/Product/productChild?childId=2&ProductType=humiture

## 设备报文参数

### 设备通讯报文
#### 请求报文
```txt
01 04 00 00 00 06 70 08
```
#### 响应报文
```txt
01 04 0c 01 1f 01 c8 41 e5 b7 a4 42 36 c3 91 14 e6
```


### 设备操作请求
#### 请求参数
```json
  {
    "param": {
      "devAddr": 1,
      "modbusMode": "RTU",
      "operateName": "读取温湿度"
    },
    "timeout": 2000,
    "deviceName": "Win32 COM3",
    "deviceType": "温湿度采集模块(ZS-SHT30-1T-1H-485)",
    "operateMode": "exchange",
    "operateName": "读取温湿度"
  }
```

#### 响应参数
```json
  {
    "uuid": "afbfb1c6-ad59-451f-ae17-147b5390ba4b",
    "operateMode": "exchange",
    "deviceName": "Win32 COM3",
    "deviceType": "温湿度采集模块(ZS-SHT30-1T-1H-485)",
    "operateName": "读取温湿度",
    "param": {
      "devAddr": 1,
      "modbusMode": "RTU",
      "operateName": "读取温湿度"
    },
    "timeout": 2000,
    "record": true,
    "data": {
      "commStatus": {
        "commSuccessTime": 1689173276267,
        "commFailedCount": 0,
        "commFailedTime": 0
      },
      "value": {
        "status": {
          "温度": 28.6,
          "湿度": 45.300000000000004
        }
      }
    },
    "code": 200,
    "msg": ""
  }
```
