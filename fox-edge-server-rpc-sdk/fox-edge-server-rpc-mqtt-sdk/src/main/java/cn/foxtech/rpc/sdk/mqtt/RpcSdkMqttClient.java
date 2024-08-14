/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.rpc.sdk.mqtt;

import cn.foxtech.common.domain.vo.RestfulLikeRequestVO;
import cn.foxtech.common.domain.vo.RestfulLikeRespondVO;
import cn.foxtech.common.mqtt.MqttClientHandler;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.rpc.sdk.mqtt.remote.RemoteMqttService;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Component
public class RpcSdkMqttClient {
    @Autowired
    @Getter(value = AccessLevel.PUBLIC)
    private RemoteMqttService remoteMqtt;

    public void initialize(MqttClientHandler clientHandler) {
        this.remoteMqtt.initialize(clientHandler);
    }

    public void waitConnected(long timeout) {
        this.remoteMqtt.waitConnected(timeout);
    }

    public void setMqttConfig(Map<String, Object> mqttConfig) {
        this.remoteMqtt.setMqttConfig(mqttConfig);
    }

    public Map<String, Object> buildMqttDefaultConfig() {
        try {
            ClassPathResource classPathResource = new ClassPathResource("mqttDefaultConfig.json");
            InputStream inputStream = classPathResource.getInputStream();
            String json = FileTextUtils.readTextFile(inputStream, StandardCharsets.UTF_8);
            Map<String, Object> defaultConfig = JsonUtils.buildObject(json, Map.class);

            String clientId = (String) defaultConfig.get("clientId");
            if (clientId == null || clientId.isEmpty()) {
                defaultConfig.put("clientId", UUID.randomUUID().toString());
            }

            return defaultConfig;
        } catch (Exception e) {
            throw new ServiceException("生成缺省配置异常：" + e.getMessage());
        }
    }

    public void sendRequest(RestfulLikeRequestVO requestVO) {
        try {
            if (MethodUtils.hasEmpty(requestVO.getTopic(), requestVO.getUuid(), requestVO.getResource(), requestVO.getMethod())) {
                throw new ServiceException("参数不能为空：topic, uuid, resource, method");
            }

            String json = JsonUtils.buildJson(requestVO);
            byte[] send = json.getBytes(StandardCharsets.UTF_8);
            this.remoteMqtt.getClient().publish(requestVO.getTopic(), send);
        } catch (Exception e) {
            throw new ServiceException("发送MQTT报文异常：" + e.getMessage());
        }
    }

    public RestfulLikeRespondVO waitRespond(String uuid, long timeout) {
        try {
            RestfulLikeRespondVO respondVO = (RestfulLikeRespondVO) SyncFlagObjectMap.inst().waitDynamic(uuid, timeout);
            return respondVO;
        } catch (Exception e) {
            return null;
        }
    }
}
