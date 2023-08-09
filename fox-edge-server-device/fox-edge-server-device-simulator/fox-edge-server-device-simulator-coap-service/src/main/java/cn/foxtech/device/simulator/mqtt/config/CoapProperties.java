package cn.foxtech.device.simulator.mqtt.config;

import cn.foxtech.device.simulator.mqtt.entity.CoapConfigEntity;
import cn.foxtech.device.simulator.mqtt.entity.CoapConfigRes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "coap.server")
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class CoapProperties {
    /**
     * 默认的端口
     */
    private int coapPort = 5684;

    /**
     * 格式:byte[],string,hex
     */
    private String returnFormat = "string";

    /**
     * 默认访问超时
     */
    private int timeout = 2000;

    /**
     * 最大客户端并发数量
     */
    private int maxActivePeers = 1000;

    /**
     * topic
     */
    private Map<String, Object> resources = new HashMap<>();


    /**
     * 生成的资源配置
     * @param resources
     * @return
     */
    private List<CoapConfigRes> buildResList(Map<String, Object> resources) {
        List<CoapConfigRes> result = new ArrayList<>();

        for (Map.Entry<String, Object> entry : resources.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            Map<String, Object> map = (Map<String, Object>) value;
            String resource = (String) map.get("resource");
            Object mediaType = map.get("media-type");

            CoapConfigRes res = new CoapConfigRes();
            if (resource == null) {
                continue;
            }
            res.setResource(resource);

            if (mediaType != null) {
                res.setMediaType((Integer) mediaType);
            }

            result.add(res);
        }

        return result;
    }

    /**
     * 生成配置信息
     *
     * @return 配置实体
     */
    public CoapConfigEntity buildConfigEntity() {
        CoapConfigEntity deviceTemplateEntity = new CoapConfigEntity();
        deviceTemplateEntity.setCoapPort(this.coapPort);
        deviceTemplateEntity.setMaxActivePeers(this.maxActivePeers);
        deviceTemplateEntity.setReturnFormat(this.returnFormat);
        deviceTemplateEntity.setTimeout(this.timeout);
        deviceTemplateEntity.setResources(this.buildResList(this.resources));

        return deviceTemplateEntity;
    }
}
