package cn.foxtech.iot.fox.cloud.common.service.proxy;

import cn.foxtech.common.domain.vo.RestfulLikeRequestVO;
import cn.foxtech.common.domain.vo.RestfulLikeRespondVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.uuid.UuidUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.iot.fox.cloud.common.mqtt.MqttClientService;
import cn.foxtech.iot.fox.cloud.common.mqtt.MqttMessageE2C;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Getter(value = AccessLevel.PUBLIC)
public class CloudMqttProxyService {
    @Autowired
    private MqttClientService mqttProxyService;


    public boolean isLogin() {
        if (this.mqttProxyService.getMqttClient() == null) {
            return false;
        }
        return this.mqttProxyService.getMqttClient().isConnected();
    }

    public <REQ> Map<String, Object> executeRestful(String res, String method, REQ request) {
        try {
            // 构造发送报文
            RestfulLikeRequestVO requestVO = new RestfulLikeRequestVO();
            requestVO.setUuid(UuidUtils.randomUUID());
            requestVO.setResource(res);
            requestVO.setMethod(method);
            requestVO.setBody(request);

            // 转换成json
            String context = JsonUtils.buildJson(requestVO);

            String pubTopic = this.mqttProxyService.getPublish2aggregate();

            // 进入准备状态
            MqttMessageE2C.inst().reset(requestVO.getUuid());

            // 发送数据
            this.mqttProxyService.getMqttClient().publish(pubTopic, context.getBytes(StandardCharsets.UTF_8));

            // 等待返回
            RestfulLikeRespondVO respondVO = (RestfulLikeRespondVO) MqttMessageE2C.inst().waitDynamic(requestVO.getUuid(), 60 * 1000);
            if (respondVO == null) {
                String body = "";
                if (context != null) {
                    body = context.substring(0, Math.min(context.length(), 1024));
                }

                throw new ServiceException("对远端的操作，通信超时！res=" + res + " method=" + method + " body=" + body);
            }

            return (Map<String, Object>) respondVO.getBody();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
