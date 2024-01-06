package cn.foxtech.kernel.system.service.mqtt;

import cn.foxtech.common.mqtt.MqttClientHandler;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.service.vo.RestfulLikeRequestVO;
import cn.foxtech.kernel.system.service.vo.RestfulLikeRespondVO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import org.tio.core.ChannelContext;
import org.tio.utils.buffer.ByteBufferUtil;

import java.nio.ByteBuffer;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class MqttProxyHandler extends MqttClientHandler {
    private MqttProxyController controller;
    private String request = "#";

    private String respond = "#";

    public String getTopic() {
        return this.request;
    }

    @Override
    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, ByteBuffer payload) {
        String messageTxt = ByteBufferUtil.toString(payload);

        // 执行请求
        RestfulLikeRespondVO respondVO = this.execute(messageTxt);

        // 返回数据
        this.respond(respondVO);
    }

    private RestfulLikeRespondVO execute(String messageTxt) {
        RestfulLikeRequestVO requestVO = null;
        RestfulLikeRespondVO respondVO = null;
        try {
            requestVO = JsonUtils.buildObject(messageTxt, RestfulLikeRequestVO.class);
            requestVO.setTopic(request);
            if (MethodUtils.hasEmpty(requestVO.getUuid(), requestVO.getResource(), requestVO.getMethod())) {
                throw new ServiceException("参数缺失：uuid, resource, method");
            }

            // 执行请求
            respondVO = this.controller.execute(requestVO);

        } catch (Exception e) {
            respondVO = RestfulLikeRespondVO.error(e.getMessage());
            if (requestVO != null) {
                respondVO.bindVO(requestVO);
            }
        }

        return respondVO;
    }

    private void respond(RestfulLikeRespondVO respondVO) {
        try {
            this.controller.publish(this.respond, JsonUtils.buildJson(respondVO));
        } catch (Exception e) {
            e.getMessage();
        }
    }
}