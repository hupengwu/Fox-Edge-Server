package cn.foxtech.proxy.cloud.common.service.proxy;

import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.uuid.UuidUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.proxy.cloud.common.mqtt.MqttClientService;
import cn.foxtech.proxy.cloud.common.mqtt.MqttMessageE2C;
import cn.foxtech.proxy.cloud.common.vo.RestfulLikeRequestVO;
import cn.foxtech.proxy.cloud.common.vo.RestfulLikeRespondVO;
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

            return (Map<String, Object>) respondVO.getBody();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
