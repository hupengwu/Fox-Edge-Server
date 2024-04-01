package cn.foxtech.kernel.system.service.mqtt;

import cn.foxtech.common.mqtt.MqttClientService;
import cn.foxtech.kernel.system.service.controller.RestfulLikeController;
import cn.foxtech.kernel.system.service.vo.RestfulLikeRequestVO;
import cn.foxtech.kernel.system.service.vo.RestfulLikeRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MqttProxyController {
    @Autowired
    private RestfulLikeController controller;


    @Autowired
    private MqttClientService clientService;


    public RestfulLikeRespondVO execute(RestfulLikeRequestVO requestVO) {
        try {
            Object value = this.controller.execute(requestVO.getResource(), requestVO.getMethod(), requestVO.getBody());

            RestfulLikeRespondVO respondVO = new RestfulLikeRespondVO();
            respondVO.bindVO(requestVO);
            respondVO.setBody(value);

            return respondVO;
        } catch (Exception e) {
            RestfulLikeRespondVO respondVO = RestfulLikeRespondVO.error(e.getMessage());
            if (requestVO != null) {
                respondVO.bindVO(requestVO);
            }

            return respondVO;
        }
    }


    public void publish(String pubTopic, String rspContext) {
        this.clientService.getMqttClient().publish(pubTopic, rspContext.getBytes(StandardCharsets.UTF_8));

    }
}
