package cn.foxtech.trigger.service.service;

import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.trigger.service.trigger.TriggerValueUpdater;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 实体更新操作：将收集到的设备数据更新到redis和mysql
 */
@Component
public class EntityUpdateService {
    private static final Logger logger = Logger.getLogger(EntityUpdateService.class);

    @Autowired
    private TriggerValueUpdater triggerValueUpdater;


    public RestFulRespondVO deleteValueEntity(RestFulRequestVO requestVO) {
        try {
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) requestVO.getData();

            Map<String, Map<String, Set<String>>> deviceValues = new HashMap<>();
            for (Map<String, Object> map : mapList) {
                String deviceName = (String) map.get("deviceName");
                String triggerConfigName = (String) map.get("triggerConfigName");
                String objectName = (String) map.get("objectName");

                if (!deviceValues.containsKey(deviceName)) {
                    deviceValues.put(deviceName, new HashMap<>());
                }
                Map<String, Set<String>> config2ObjectNames = deviceValues.get(deviceName);

                if (!config2ObjectNames.containsKey(triggerConfigName)) {
                    config2ObjectNames.put(deviceName, new HashSet<>());
                }
                Set<String> objectNames = config2ObjectNames.get(deviceName);

                objectNames.add(objectName);
            }

            for (String deviceName : deviceValues.keySet()) {
                Map<String, Set<String>> config2ObjectNames = deviceValues.get(deviceName);
                for (String triggerConfigName : config2ObjectNames.keySet()) {
                    this.triggerValueUpdater.deleteValueEntity(deviceName, triggerConfigName, config2ObjectNames.get(deviceName));
                }
            }

            return RestFulRespondVO.success(requestVO);

        } catch (Exception e) {
            return RestFulRespondVO.error(requestVO, e.getMessage());
        }
    }
}
