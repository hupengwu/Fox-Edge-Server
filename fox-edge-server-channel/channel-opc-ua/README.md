# fox-edge-server-channel-opc-ua-service

#### 介绍
opc-ua的通道服务

#### 软件架构
软件架构说明


#### 使用说明

- 创建一个opc-ua的通道后，在通道中配置连接参数

  ```json
	{
     "appUri": "urn:fox-edge:UnifiedAutomation:UaExpert@fox-edge",
     "appName": "fox-edge-OpcUaClient",
     "certificate": {
          "file": "fox-edge-client.pfx",
          "path": "",
          "alias": "fox-edge-ai",
          "dnsName": "fox-edge",
          "ipAddress": "127.0.0.1",
          "stateName": "GuangDong",
          "commonName": "UaClient@fox-edge",
          "countryCode": "CN",
          "localityName": "fox-edge",
          "organization": "fox-edge",
          "keystorePassword": "123456",
          "organizationUnit": "per"
     },
     "endpointUrl": "opc.tcp://LAPTOP-9JI6D0AU:53530/OPCUA/SimulationServer",
     "idpPassword": "",
     "idpUsername": "fox-edge"
    }
	
  ```

- 创建一个通道操作任务browseTree，然后测试返回

  ```json  
	{
     "operate": "browseTree"
    }
	
  ```
- 创建一个通道操作任务browseChildValue，然后测试返回

  ```json  
  
	{
     "nodeId": {
          "namespace": 3,
          "identifier": "85/0:Simulation"
     },
     "operate": "browseChildValue"
    }
	
  ```
- 创建一个通道操作任务browseChild，然后测试返回

  ```json  
  
	{
     "nodeId": {
          "namespace": 3,
          "identifier": "85/0:Simulation"
     },
     "operate": "browseChild"
    }
	
  ```