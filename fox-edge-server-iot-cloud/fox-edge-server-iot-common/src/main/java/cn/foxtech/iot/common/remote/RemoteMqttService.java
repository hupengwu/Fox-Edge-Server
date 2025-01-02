/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.iot.common.remote;

import cn.foxtech.common.mqtt.MqttClientHandler;
import cn.foxtech.common.mqtt.MqttClientEntity;
import cn.foxtech.common.mqtt.MqttCompService;
import lombok.AccessLevel;
import lombok.Getter;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter(value = AccessLevel.PUBLIC)
public class RemoteMqttService {

    @Autowired
    private MqttCompService service;

    @Autowired
    private RemoteMqttConfig config;

    public void initialize(MqttClientHandler handler) {
        this.service.createClientEntity("", this.config, handler);
    }

    public MqttClient getClient() {
        MqttClientEntity clientService = this.service.getClientEntity("");
        if (clientService == null) {
            return null;
        }

        return clientService.getClient();
    }
}
