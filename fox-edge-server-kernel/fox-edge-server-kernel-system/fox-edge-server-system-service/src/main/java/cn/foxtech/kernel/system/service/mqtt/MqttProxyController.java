package cn.foxtech.kernel.system.service.mqtt;

import cn.foxtech.common.mqtt.MqttClientService;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.service.controller.RestfulLikeController;
import cn.foxtech.kernel.system.service.vo.RestfulLikeRequestVO;
import cn.foxtech.kernel.system.service.vo.RestfulLikeRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class MqttProxyController {
    @Autowired
    private RestfulLikeController controller;


    @Autowired
    private MqttClientService clientService;


    public RestfulLikeRespondVO execute(RestfulLikeRequestVO requestVO) {
        try {
            String resource = this.controller.getResource(requestVO.getResource());
            String methodName = requestVO.getMethod().toUpperCase();

            String methodKey = resource + ":" + methodName;
            Object bean = this.controller.getBean(methodKey);
            Object method = this.controller.getMethod(methodKey);
            if (method == null || bean == null) {
                throw new ServiceException("尚未支持的方法");
            }

            // 执行controller的bean函数
            Object value = null;
            if (methodName.equals("POST") || methodName.equals("PUT")) {
                value = ((Method) method).invoke(bean, requestVO.getBody());
            } else if (methodName.equals("GET") || methodName.equals("DELETE")) {
                List<Object> params = this.controller.getParams(requestVO.getResource(), (Method) method);
                if (params.size() == 0) {
                    value = ((Method) method).invoke(bean, params);
                } else if (params.size() == 1) {
                    value = ((Method) method).invoke(bean, params.get(0));
                } else if (params.size() == 2) {
                    value = ((Method) method).invoke(bean, params.get(0), params.get(1));
                } else if (params.size() == 3) {
                    value = ((Method) method).invoke(bean, params.get(0), params.get(1), params.get(2));
                } else {
                    throw new ServiceException("尚未支持的方法");
                }
            } else {
                throw new ServiceException("尚未支持的方法");
            }


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
