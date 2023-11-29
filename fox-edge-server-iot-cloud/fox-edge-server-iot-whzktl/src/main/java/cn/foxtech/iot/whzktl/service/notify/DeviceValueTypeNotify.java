package cn.foxtech.iot.whzktl.service.notify;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.redis.BaseConsumerTypeNotify;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.iot.whzktl.service.vo.DeviceValueNotifyVO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Component
public class DeviceValueTypeNotify implements BaseConsumerTypeNotify {
    /**
     * 通知变更
     *
     * @param addMap 增加
     * @param delSet 删除
     * @param mdyMap 修改
     */
    @Override
    public void notify(String entityType, long updateTime, Map<String, BaseEntity> addMap, Set<String> delSet, Map<String, BaseEntity> mdyMap) {
        for (String key : addMap.keySet()) {
            DeviceValueNotifyVO vo = new DeviceValueNotifyVO();
            vo.setMethod("insert");
            vo.setEntity(addMap.get(key));

            SyncQueueObjectMap.inst().push(DeviceValueNotifyVO.class.getSimpleName(), vo, 1024);
        }
        for (String key : mdyMap.keySet()) {
            DeviceValueNotifyVO vo = new DeviceValueNotifyVO();
            vo.setMethod("update");
            vo.setEntity(mdyMap.get(key));

            SyncQueueObjectMap.inst().push(DeviceValueNotifyVO.class.getSimpleName(), vo, 1024);
        }
        for (String key : delSet) {
            DeviceValueNotifyVO vo = new DeviceValueNotifyVO();
            vo.setMethod("delete");

            SyncQueueObjectMap.inst().push(DeviceValueNotifyVO.class.getSimpleName(), vo, 1024);
        }
    }
}
