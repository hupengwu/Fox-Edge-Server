package cn.foxtech.rpc.sdk.demo.test;

import cn.foxtech.common.domain.vo.RestfulLikeRequestVO;
import cn.foxtech.common.domain.vo.RestfulLikeRespondVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.rpc.sdk.mqtt.RpcSdkMqttClient;
import cn.foxtech.rpc.sdk.mqtt.remote.RemoteMqttHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class RpcMqttSdkTester {
    private static final Logger logger = Logger.getLogger(RpcMqttSdkTester.class);

    @Autowired
    private RpcSdkMqttClient mqttClient;

    public void test() {
        this.initialize();
        this.testManager();
    }

    private void initialize() {
        // 生成一个缺省的mqtt配置参数，你也可以自己构造一个相同格式的MQTT参数
        Map<String, Object> mqttConfig = this.mqttClient.buildMqttDefaultConfig();
        mqttConfig.put("host", "192.168.1.21");// 这是默认的配置，建议自行管理mqttConfig


        // 生成一个接收MQTT消息的handler，指明要订阅的消息
        RemoteMqttHandler mqttHandler = new RemoteMqttHandler();// 这是默认的handler实现，建议根据需要自行实现一个mqttHandler
        mqttHandler.setTopic("/fox/manager/e2c/forward/#");

        this.mqttClient.setMqttConfig(mqttConfig);
        this.mqttClient.initialize(mqttHandler);

        // 等待连接建立
        this.mqttClient.waitConnected(60 * 1000);
    }

    private void testManager() {
        RestfulLikeRequestVO requestVO = new RestfulLikeRequestVO();
        requestVO.setTopic("/fox/manager/c2e/F9509B1CE7B0F023/forward");// 必填参数：跟fox-edge的manager约定的topic
        requestVO.setUuid(UUID.randomUUID().toString());// 必填参数：待会用来查询响应的报文消息

        // 参考管理页面的浏览器查询的restful接口
        Map<String, Object> body = new HashMap<>();
        body.put("pageNum", 1);
        body.put("pageSize", 10);
        requestVO.setResource("/device/page");
        requestVO.setMethod("post");
        requestVO.setBody(body);

        // 发送请求
        this.mqttClient.sendRequest(requestVO);
        logger.info("管理服务-发送MQTT请求：" + JsonUtils.buildJsonWithoutException(requestVO));

        // 等待响应
        RestfulLikeRespondVO respondVO = this.mqttClient.waitRespond(requestVO.getUuid(), 10 * 1000);
        logger.info("管理服务-返回MQTT响应：" + JsonUtils.buildJsonWithoutException(respondVO));
    }
}
