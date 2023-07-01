package cn.foxtech.proxy.cloud.forwarder.config;


import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ApplicationConfig {
    /**
     * 边缘节点的ID
     */
    private String edgeId = OSInfoUtils.getCPUID();

    /**
     * MQTT订阅位置
     */
    private String subscribe = "/fox/edge/proxy/" + edgeId + "/request";

    /**
     * MQTT发布位置
     */
    private String publish = "/fox/edge/proxy/" + edgeId + "/respond";

    /**
     * 用户名
     */
    @Value("${spring.fox-service.gateway.username}")
    private String loginUserName;

    /**
     * 密码
     */
    @Value("${spring.fox-service.gateway.password}")
    private String loginPassword;

    /**
     * MQTT Server的位置
     */
    @Value("${mqtt.client.ip}")
    private String ip = "127.0.0.1";

    /**
     * 客户端的ID
     */
    private String clientId = "fox-edge-" + edgeId;
}
