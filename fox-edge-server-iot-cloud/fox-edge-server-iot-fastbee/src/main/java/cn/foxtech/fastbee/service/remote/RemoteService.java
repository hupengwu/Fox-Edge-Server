/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.fastbee.service.remote;

import cn.foxtech.common.entity.entity.DeviceObjectValue;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.mqtt.MqttClientConfig;
import cn.foxtech.common.mqtt.MqttClientEntity;
import cn.foxtech.common.mqtt.MqttClientListener;
import cn.foxtech.common.mqtt.MqttCompService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.iot.common.remote.RemoteMqttConfig;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RemoteService {
    private final Map<String, String> subscribed = new ConcurrentHashMap<>();
    @Autowired
    private MqttCompService mqttComp;

    @Autowired
    private RemoteMqttConfig mqttConfig;

    @Autowired
    private MqttHandler mqttHandler;

    @Autowired
    private RedisConsoleService consoleService;


    public void subscribeTopic(String deviceName, Object productId, Object deviceNum) {
        MqttClientEntity client = this.getMqttClient(deviceName, productId, deviceNum);

        // 检查：是否已经建立连接
        if (!client.getClient().isConnected()) {
            return;
        }

        // 订阅平台指令：/{productId}/{deviceNum}/function/get
        String topic = "/" + productId + "/" + deviceNum + "/function/get";
        if (!this.subscribed.containsKey(deviceName)) {
            MqttClient mqttClient = client.subscribe(topic, MqttQoS.QOS0);
            if (mqttClient != null) {
                this.subscribed.put(deviceName, topic);

                this.consoleService.info("MQTT订阅成功：" + deviceName + " ->" + topic);
            }
        }
    }


    public boolean isConnected(String deviceName, Object productId, Object deviceNum) {
        MqttClientEntity client = this.getMqttClient(deviceName, productId, deviceNum);
        return client.getClient().isConnected();
    }

    public boolean publishDeviceValue(String deviceName, Object productId, Object deviceNum, Map<String, DeviceObjectValue> values, long timestamp) {
        MqttClientEntity client = this.getMqttClient(deviceName, productId, deviceNum);
        if (!client.getClient().isConnected()) {
            return false;
        }

        if (MethodUtils.hasEmpty(values)) {
            return false;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (String key : values.keySet()) {
            DeviceObjectValue value = values.get(key);

            Map<String, Object> data = new HashMap<>();
            data.put("id", key);
            data.put("value", value.getValue());
            data.put("ts", timestamp);

            list.add(data);
        }

        // 发布数据 (实时显示，属性/功能和监测数据，可定时上报监测数据)：/{productId}/{deviceNum}/property/post
        String topic = "/" + productId + "/" + deviceNum + "/property/post";

        String json = JsonUtils.buildJsonWithoutException(list);
        return client.getClient().publish(topic, json.getBytes(StandardCharsets.UTF_8));
    }

    private synchronized MqttClientEntity getMqttClient(String deviceName, Object productId, Object deviceNum) {
        MqttClientEntity client = this.mqttComp.getClientEntity(deviceName);

        // 检查：是否实例化
        if (client.getListener() != null) {
            return client;
        }

        // 复制一个配置副本
        MqttClientConfig clone = this.mqttConfig.clone();
        String clientId = "S&" + deviceNum + "&" + productId + "&1";
        clone.setClientId(clientId);

        // 定义一个消息捕获
        MqttClientListener listener = new MqttClientListener();
        listener.setClientHandler(this.mqttHandler);

        // 为该设备创建独立的连接
        client.create(clone, listener);

        this.consoleService.info("MQTT建立连接：" + deviceName);

        return client;
    }

    public void closeNotExist(Set<String> existKeys) {
        // 注销相关的MQTT订阅
        Set<String> unsubKeys = new HashSet<>();
        for (String key : this.subscribed.keySet()) {
            if (existKeys.contains(key)) {
                continue;
            }

            unsubKeys.add(key);
        }
        for (String key : unsubKeys) {
            MqttClientEntity entity = this.mqttComp.getClientEntity(key);
            if (entity != null && entity.getClient().isConnected()) {
                entity.getClient().unSubscribe(this.subscribed.get(key));
            }

            this.subscribed.remove(key);
        }

        // 关闭相关的MQTT连接
        Set<String> clientSet = this.mqttComp.keySet();
        for (String key : clientSet) {
            if (existKeys.contains(key)) {
                continue;
            }

            this.mqttComp.closeClientEntity(key);

            this.consoleService.info("MQTT断开连接：" + key);
        }
    }
}
