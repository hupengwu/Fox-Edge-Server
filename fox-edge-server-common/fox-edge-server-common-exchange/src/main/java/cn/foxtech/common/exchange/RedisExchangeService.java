package cn.foxtech.common.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RedisExchangeService {
    private final String keyData = "fox-edge:service:exchange:data";
    /**
     * redis
     */
    @Autowired
    private RedisTemplate redisTemplate;

    public void saveConfigValue(String serviceType, String serviceName, String configName, Map<String, Object> configValue) {
        String serviceKey = this.encodeServiceKey(serviceType, serviceName, configName);

        Map<String, Object> value = new HashMap<>();
        value.put("updateTime", System.currentTimeMillis());
        value.put("configValue", configValue);

        this.redisTemplate.opsForHash().put(this.keyData, serviceKey, value);
    }

    public Map<String, Object> readConfigValue(String serviceType, String serviceName, String configName) {
        String serviceKey = this.encodeServiceKey(serviceType, serviceName, configName);

        Object value = this.redisTemplate.opsForHash().get(this.keyData, serviceKey);

        return (Map<String, Object>) value;
    }

    public List<Map<String, Object>> readConfigValues() {
        Map<String, Object> values = (Map<String, Object>) this.redisTemplate.opsForHash().entries(this.keyData);

        List<Map<String, Object>> list = new ArrayList<>();
        for (String key : values.keySet()) {
            Map<String, Object> value = (Map<String, Object>) values.get(key);

            String[] items = this.decodeServiceKey(key);

            Map<String, Object> data = new HashMap<>();
            data.put("serviceType", items[0]);
            data.put("serviceName", items[1]);
            data.put("configName", items[2]);
            data.put("updateTime", value.getOrDefault("updateTime", 0));
            data.put("configValue", value.getOrDefault("configValue", 0));

            list.add(data);
        }


        return list;
    }

    public void removeConfigValue(String serviceType, String serviceName, String configName) {
        String serviceKey = this.encodeServiceKey(serviceType, serviceName, configName);

        this.redisTemplate.opsForHash().delete(this.keyData, serviceKey);
    }


    private String encodeServiceKey(String serviceType, String serviceName, String configName) {
        return serviceType + ":" + serviceName + ":" + configName;
    }

    private String[] decodeServiceKey(String serviceKey) {
        if (serviceKey.length() < 4) {
            throw new RuntimeException("serviceKey格式非法");
        }

        String[] items = serviceKey.split(":");
        if (items.length != 3) {
            throw new RuntimeException("serviceKey格式非法");
        }

        return items;
    }
}
