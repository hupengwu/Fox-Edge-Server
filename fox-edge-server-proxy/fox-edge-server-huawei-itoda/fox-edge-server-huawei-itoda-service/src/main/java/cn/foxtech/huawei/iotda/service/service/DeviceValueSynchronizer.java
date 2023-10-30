package cn.foxtech.thingsboard.service.service;

import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.common.entity.utils.ExtendConfigUtils;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.huawei.iotda.common.service.RemoteProxyService;
import cn.foxtech.huawei.iotda.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DeviceValueSynchronizer {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RemoteProxyService httpProxyService;

    public void publish(Set<String> serviceKeys, Map<String, Object> redisHashMap) {
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
                if (token == null || token.isEmpty() || token.length() < 10) {
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
