package cn.foxtech.proxy.cloud.publisher;

import cn.foxtech.common.entity.constant.EntityPublishConstant;
import cn.foxtech.proxy.cloud.publisher.service.CloudEntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地数据库
 */
@Component
public class ValueEntityLocalDataBase {
    private final Map<String, Long> entityServiceMap = new ConcurrentHashMap<>();

    @Autowired
    private CloudEntityManageService entityManageService;

    public Set<String> getEntityTypeList() {
        // 查询各服务的发布订阅
        Set<String> entityTypeList = this.entityManageService.getPublishEntityTypeList(EntityPublishConstant.value_mode_value);
        for (String entityType : entityTypeList) {
            this.entityServiceMap.put(entityType, -1L);
        }

        return this.entityServiceMap.keySet();
    }

    public Object getSyncObject(String entityType) {
        Set<String> entityTypeList = this.entityManageService.getPublishEntityTypeList(EntityPublishConstant.value_mode_value);
        if (!entityTypeList.contains(entityType)) {
            return null;
        }

        return this.entityServiceMap.get(entityType);
    }

    public void setSyncObject(String entityType, Long syncObject) {
        this.entityServiceMap.put(entityType, syncObject);
    }
}
