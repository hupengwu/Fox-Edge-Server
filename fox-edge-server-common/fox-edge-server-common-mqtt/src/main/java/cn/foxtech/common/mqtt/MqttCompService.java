/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.mqtt;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MQTT组件：它管理着众多的MQTT连接，你可以用空字符串作为key，生成一个默认的MqttClient
 */
@Component
public class MqttCompService {
    private final Map<String, MqttClientEntity> clientMap = new ConcurrentHashMap<>();

    /**
     * 创建一个MQTT客户端实体，用于连接MQTT服务器
     *
     * @param key
     * @param configs
     * @param handler
     */
    public void createClientEntity(String key, Map<String, Object> configs, MqttClientHandler handler) {
        MqttClientConfig config = new MqttClientConfig();
        config.instance(configs);

        this.createClientEntity(key, config, handler);
    }

    /**
     * 创建一个MQTT客户端实体，用于连接MQTT服务器
     *
     * @param key
     * @param config
     * @param handler
     */
    public void createClientEntity(String key, MqttClientConfig config, MqttClientHandler handler) {
        MqttClientEntity entity = this.getClientEntity(key);

        MqttClientListener listener = new MqttClientListener();
        listener.setClientHandler(handler);

        entity.create(config, listener);
    }


    /**
     * 获得一个可用的MqttClientEntity实体：防止多线程并发时候，重复分配
     *
     * @param key
     * @return
     */
    public synchronized MqttClientEntity getClientEntity(String key) {
        MqttClientEntity entity = this.clientMap.get(key);
        if (entity == null) {
            MqttClientEntity client = new MqttClientEntity();
            this.clientMap.put(key, client);
            return client;
        }

        return entity;
    }

    /**
     * 关闭不再使用的MqttClientEntity
     *
     * @param key
     */
    public void closeClientEntity(String key) {
        MqttClientEntity entity = this.clientMap.get(key);
        if (entity == null) {
            return;
        }

        entity.getClient().disconnect();
        this.clientMap.remove(key);
    }

    public Set<String> keySet() {
        return this.clientMap.keySet();
    }
}
