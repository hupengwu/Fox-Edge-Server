/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.status;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.utils.number.NumberUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 应用状态
 */
@Getter(value = AccessLevel.PUBLIC)
@Component
public class ServiceStatus {
    /**
     * 缓存模式的配置key
     */
    private final String cacheModeKey = "spring.fox-service.mode.cache";
    /**
     * 消费者
     */
    @Autowired
    private ServiceStatusConsumerService consumerService;
    /**
     * 生产者
     */
    @Autowired
    private ServiceStatusProducerService producerService;

    /**
     * redis直读
     */
    @Autowired
    private ServiceStatusReaderService readerService;

    /**
     * 服务内线
     */
    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";
    /**
     * 服务名称
     */
    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    /**
     * 模型类型
     */
    @Value("${spring.fox-service.model.type}")
    private String foxModelType = "undefinedModelType";
    /**
     * 模型名称
     */
    @Value("${spring.fox-service.model.name}")
    private String foxModelName = "undefinedModelName";


    /**
     * 是否为本地缓存模式
     * cache模式：消费者的数据，是通过线程定时从redis中装载，使用者反复消费缓存中的数据
     * redis模式：消费者的数据，是消费者直接从redis中读取数据
     */
    @Setter(value = AccessLevel.PUBLIC)
    private boolean cacheMode = true;

    public Map<String, Object> getConsumerData() {
        if (this.cacheMode) {
            return this.consumerService.getStatus();
        } else {
            return this.readerService.getStatus();
        }
    }

    public Map<String, Object> getProducerData() {
        return this.producerService.getStatus();
    }

    public String getServiceKey() {
        return this.foxServiceType + ":" + this.foxServiceName;
    }

    /**
     * 获得时间
     *
     * @param modelType 模块分类
     * @param modelName 模块类型
     * @return
     */
    public Long getActiveTime(String modelType, String modelName) {
        Map<String, Object> consumerData = this.getConsumerData();

        Long maxActiveTime = null;
        for (String key : consumerData.keySet()) {
            try {
                Map<String, Object> value = (Map<String, Object>) consumerData.get(key);
                if (value == null) {
                    continue;
                }


                String type = (String) value.get(RedisStatusConstant.field_model_type);
                String name = (String) value.get(RedisStatusConstant.field_model_name);
                if (modelType.equals(type) && modelName.equals(name)) {
                    // 获得该服务再缓存中的activeTime数据
                    Long time = NumberUtils.makeLong(value.get(RedisStatusConstant.field_active_time));

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

    public Map<String, Object> getActiveService(String modelType, String modelName, int timeout) {
        List<Map<String, Object>> dataList = this.getActiveServices(modelType, modelName, timeout);
        if (dataList.isEmpty()) {
            return null;
        }

        return dataList.get(0);
    }

    public List<Map<String, Object>> getActiveServices(String modelType, String modelName, int timeout) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        List<Map<String, Object>> dataList = this.getDataList(timeout);
        for (Map<String, Object> data : dataList) {
            String type = (String) data.get(RedisStatusConstant.field_model_type);
            String name = (String) data.get(RedisStatusConstant.field_model_name);
            if (modelType.equals(type) && modelName.equals(name)) {
                resultList.add(data);
            }
        }

        return resultList;
    }

    public List<Map<String, Object>> getDataList() {
        return this.getDataList(null, null);
    }

    public List<Map<String, Object>> getDataList(int timeout) {
        long currentTime = System.currentTimeMillis();
        return this.getDataList(timeout, currentTime);
    }

    private List<Map<String, Object>> getDataList(Integer timeout, Long currentTime) {
        Map<String, Object> consumerData = this.getConsumerData();

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Object statusValue : consumerData.values()) {
            Map<String, Object> value = (Map<String, Object>) statusValue;
            if (value == null) {
                continue;
            }

            if (timeout == null) {
                resultList.add(value);
                continue;
            }

            // 剔除失效的任务：超时超到指定范围的业务
            Long activeTime = NumberUtils.makeLong(value.getOrDefault(RedisStatusConstant.field_active_time, -1L));
            if (currentTime - activeTime > timeout) {
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
