package cn.foxtech.common.status;

import cn.foxtech.common.utils.redis.status.RedisStatusConsumerService;
import cn.foxtech.common.domain.constant.RedisStatusConstant;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用状态
 */
@Getter(value = AccessLevel.PUBLIC)
@Component
public class ServiceStatus {
    @Getter(value = AccessLevel.PUBLIC)
    private final Map<String, Object> producerData = new ConcurrentHashMap<>();


    @Getter(value = AccessLevel.PUBLIC)
    private final Map<String, Object> consumerData = new ConcurrentHashMap<>();

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    @Value("${spring.fox-service.model.type}")
    private String foxModelType = "undefinedModelType";

    @Value("${spring.fox-service.model.name}")
    private String foxModelName = "undefinedModelName";

    public String getServiceKey() {
        return this.foxServiceType + ":" + this.foxServiceName;
    }

    public Object getConsumerData(String key, String hkey) {
        RedisStatusConsumerService.Status status = (RedisStatusConsumerService.Status) consumerData.get(key);
        if (status == null) {
            return null;
        }

        Map<String, Object> map = (Map<String, Object>) status.getData();
        return map.get(hkey);
    }

    /**
     * 获得时间
     *
     * @param modelType 模块分类
     * @param modelName 模块类型
     * @return
     */
    public Long getActiveTime(String modelType, String modelName) {
        Long maxActiveTime = null;
        for (Map.Entry<String, Object> entry : this.getConsumerData().entrySet()) {
            try {
                RedisStatusConsumerService.Status status = (RedisStatusConsumerService.Status) entry.getValue();
                if (status == null) {
                    continue;
                }

                Map<String, Object> value = (Map<String, Object>) status.getData();
                if (value == null) {
                    continue;
                }


                String type = (String) value.get(RedisStatusConstant.field_model_type);
                String name = (String) value.get(RedisStatusConstant.field_model_name);
                if (modelType.equals(type) && modelName.equals(name)) {
                    // 获得该服务再缓存中的activeTime数据
                    Long time = null;
                    Object activeTime = value.get(RedisStatusConstant.field_active_time);
                    if (activeTime instanceof Long) {
                        time = (Long) activeTime;
                    }
                    if (activeTime instanceof Integer) {
                        time = Long.valueOf((Integer) activeTime);
                    }

                    // 是否存在该activeTime数据
                    if (time == null) {
                        continue;
                    }
                    // 如果存在，则用来初始化result
                    if (maxActiveTime == null) {
                        maxActiveTime = time;
                    }

                    // 比较多个activeTime的最大值
                    maxActiveTime = Math.max(maxActiveTime, time);
                }
            } catch (Exception e) {
                continue;
            }
        }

        return maxActiveTime;
    }

    public List<Map<String, Object>> getDataList() {
        return this.getDataList(null, null);
    }

    public List<Map<String, Object>> getDataList(int timeout) {
        long currentTime = System.currentTimeMillis();
        return this.getDataList(timeout, currentTime);
    }

    private List<Map<String, Object>> getDataList(Integer timeout, Long currentTime) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Object statusValue : this.consumerData.values()) {
            RedisStatusConsumerService.Status status = (RedisStatusConsumerService.Status) statusValue;
            Map<String, Object> value = (Map<String, Object>) status.getData();
            if (value == null) {
                continue;
            }

            if (timeout == null) {
                resultList.add(value);
                continue;
            }

            // 剔除失效的任务：超时超到指定范围的业务
            if (currentTime - status.getLastTime() > timeout) {
                continue;
            }

            value.put("currentTime", currentTime);

            resultList.add(value);
        }

        return resultList;
    }

    public boolean isActive(String modelCategory, String modelType, int timeout) {
        // 通过时间戳，判断通道服务是否正在运行
        Long activeTime = this.getActiveTime(modelCategory, modelType);
        if (activeTime == null) {
            return false;
        }
        return System.currentTimeMillis() - activeTime <= timeout;
    }
}
