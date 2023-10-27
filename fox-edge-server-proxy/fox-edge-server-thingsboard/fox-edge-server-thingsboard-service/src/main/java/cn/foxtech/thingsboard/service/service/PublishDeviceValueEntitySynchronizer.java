package cn.foxtech.thingsboard.service.service;

import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.entity.utils.ExtendConfigUtils;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.thingsboard.common.service.CloudHttpProxyService;
import cn.foxtech.thingsboard.common.service.EntityManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PublishDeviceValueEntitySynchronizer {
    private static final Logger logger = Logger.getLogger(PublishDeviceValueEntitySynchronizer.class);
    private final Map<String, Object> localAgileMap = new HashMap<>();
    private Object localTimeStamp = 0L;
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private CloudHttpProxyService httpProxyService;


    public void syncEntity() {
        try {
            if (!this.entityManageService.isInitialized()) {
                return;
            }
            // 获得直接读取redis的部件
            RedisReader redisReader = this.entityManageService.getRedisReader(DeviceValueEntity.class.getSimpleName());
            if (redisReader == null) {
                return;
            }

            // 获取redis上的时间戳,用来判断是否本地redis发生了变化
            Object redisTimeStamp = redisReader.readSync();
            if (redisTimeStamp == null) {
                return;
            }

            // 比较时间戳
            if (redisTimeStamp.equals(this.localTimeStamp)) {
                return;
            }

            // redis的数据
            Map<String, Object> redisAgileMap = redisReader.readAgileMap();
            if (redisAgileMap == null) {
                return;
            }

            Set<String> addList = new HashSet<>();
            Set<String> delList = new HashSet<>();
            Set<String> eqlList = new HashSet<>();
            DifferUtils.differByValue(this.localAgileMap.keySet(), redisAgileMap.keySet(), addList, delList, eqlList);

            Set<String> pushKeys = new HashSet<>();

            // 新增的的数据，需要推送
            for (String key : addList) {
                Object redisTime = redisAgileMap.get(key);
                this.localAgileMap.put(key, redisTime);

                pushKeys.add(key);
            }

            // 删除的数据，不需要推送
            for (String key : delList) {
                this.localAgileMap.remove(key);
            }

            // 变更的数据，需要推送
            for (String key : eqlList) {
                Object localTime = localAgileMap.get(key);
                Object redisTime = redisAgileMap.get(key);
                if (localTime.equals(redisTime)) {
                    continue;
                }

                pushKeys.add(key);
            }

            this.localTimeStamp = redisTimeStamp;

            if (!pushKeys.isEmpty()) {
                Map<String, Object> redisHashMap = redisReader.readHashMap();
                this.publish(pushKeys, redisHashMap);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void publish(Set<String> serviceKeys, Map<String, Object> redisHashMap) {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(ExtendConfigEntity.class);
        Map<String, ExtendConfigEntity> extendMap = ExtendConfigUtils.getExtendConfigList(entityList, DeviceEntity.class);

        String resource = "/v1/{token}/telemetry";

        for (String serviceKey : serviceKeys) {
            try {
                Map<String, Object> deviceValue = (Map<String, Object>) redisHashMap.get(serviceKey);
                if (deviceValue == null) {
                    continue;
                }

                DeviceEntity deviceEntity = this.entityManageService.getEntity(serviceKey, DeviceEntity.class);
                if (deviceEntity == null) {
                    continue;
                }

                Map<String, Object> deviceMap = BeanMapUtils.objectToMap(deviceEntity);
                if (deviceMap == null) {
                    continue;
                }

                // 扩展配置
                ExtendConfigUtils.extend(deviceMap, extendMap);

                Map<String, Object> extendParam = (Map<String, Object>) deviceMap.get(DeviceVOFieldConstant.field_extend_param);
                if (extendParam == null) {
                    continue;
                }

                // 取出配置的token信息
                String token = (String) extendParam.get("thingsboardHttpToken");
                if (token == null || token.isEmpty()) {
                    continue;
                }

                Map<String, Object> params = (Map<String, Object>) deviceValue.get("params");
                if (params == null) {
                    continue;
                }


                // 推送数据到thingsboard云平台
                this.pushParams(resource, params, token);

            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    private void pushParams(String resource, Map<String, Object> params, String token) {
        Map<String, Object> values = new HashMap<>();
        for (String key : params.keySet()) {
            Map<String, Object> map = (Map<String, Object>) params.get(key);
            if (map == null) {
                continue;
            }

            values.put(key, map.get("value"));
            if (values.size() > 10) {
                this.pushValue(resource, values, token);
                values.clear();
            }
        }

        if (!values.isEmpty()) {
            this.pushValue(resource, values, token);
            values.clear();
        }
    }

    private void pushValue(String resource, Map<String, Object> values, String token) {
        try {
            // 准备body
            String body = JsonUtils.buildJsonWithoutException(values);
            if (body == null) {
                return;
            }

            // thingsboard上对应该设备的资源
            String res = resource.replace("{token}", token);

            // 发送请求
            this.httpProxyService.executeRestful(res, "post", body);
        } catch (Exception e) {
            return;
        }
    }
}
