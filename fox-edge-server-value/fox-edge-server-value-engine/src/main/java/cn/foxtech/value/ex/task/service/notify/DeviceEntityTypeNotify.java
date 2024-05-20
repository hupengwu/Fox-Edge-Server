package cn.foxtech.value.ex.task.service.notify;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.redis.BaseConsumerTypeNotify;
import cn.foxtech.value.ex.task.service.service.DataTaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class DeviceEntityTypeNotify implements BaseConsumerTypeNotify {
    @Autowired
    private DataTaskManager dataTaskManager;

    /**
     * 通知变更
     *
     * @param addMap 增加
     * @param delSet 删除
     * @param mdyMap 修改
     */
    @Override
    public void notify(String entityType, long updateTime, Map<String, BaseEntity> addMap, Set<String> delSet, Map<String, BaseEntity> mdyMap) {
        this.dataTaskManager.setNeedReset(true);
    }

}
