package cn.foxtech.device.simulator.mqtt.entity;

import cn.foxtech.common.utils.json.JsonUtils;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CoapDataEntity {
    private final Map<String, Object> data = new ConcurrentHashMap<>();

    /**
     * 获取参数
     *
     * @param resource
     * @return 参数表
     */
    public Map<String, Object> getParams(String resource) {
        Map<String, Object> resourceMap = (Map<String, Object>) data.get(resource);

        return resourceMap;
    }

    public Map<String, Object> putParams(String resource, Map<String, Object> params) {
        Map<String, Object> resourceMap = (Map<String, Object>) data.get(resource);
        if (resourceMap == null) {
            return null;
        }


        resourceMap.putAll(params);
        return resourceMap;
    }

    /**
     * 构造数据
     *
     * @param json
     */
    public void build(String json) throws IOException {
        JResourceSimulator jsnData = JsonUtils.buildObject(json, JResourceSimulator.class);
        for (JResource resource : jsnData.resource_simulator) {
            // resource
            Map<String, Object> method = (Map<String, Object>) this.data.get(resource.resource);
            if (method == null) {
                method = new ConcurrentHashMap<>();
                this.data.put(resource.resource, method);
            }

            for (JParam item : resource.params) {
                method.put(item.name, item.value);
            }
        }
    }

    @Data
    static class JResourceSimulator implements Serializable {
        private List<JResource> resource_simulator = new ArrayList<>();
    }

    @Data
    static class JResource implements Serializable {
        private String resource;
        private List<JParam> params = new ArrayList<>();
    }

    @Data
    static class JParam implements Serializable {
        private String name;
        private Object value;
    }
}
