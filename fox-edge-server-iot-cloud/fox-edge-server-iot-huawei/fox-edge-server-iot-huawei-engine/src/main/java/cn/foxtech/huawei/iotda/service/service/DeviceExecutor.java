package cn.foxtech.huawei.iotda.service.service;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.common.entity.utils.ExtendConfigUtils;
import cn.foxtech.huawei.iotda.service.huawei.HuaweiIoTDAService;
import cn.foxtech.iot.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeviceExecutor {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private HuaweiIoTDAService huaweiIoTDAService;

    public Map<String, DeviceEntity> getKey2Entity() {
        // 获得全体扩展配置信息
        List<BaseEntity> extendConfigEntityList = this.entityManageService.getEntityList(ExtendConfigEntity.class);

        // 获得设备列表信息
        List<BaseEntity> deviceEntityList = this.entityManageService.getEntityList(DeviceEntity.class);

        // 为设备的Map组装上扩展信息
        ExtendConfigUtils.extendEntityList(deviceEntityList, extendConfigEntityList, DeviceEntity.class);

        Map<String, DeviceEntity> result = new HashMap<>();
        for (BaseEntity entity : deviceEntityList) {
            DeviceEntity deviceEntity = (DeviceEntity) entity;

            if (!Boolean.TRUE.equals(deviceEntity.getExtendParam().get(this.huaweiIoTDAService.getExtendField()))) {
                continue;
            }


            result.put(deviceEntity.makeServiceKey(), deviceEntity);
        }

        return result;
    }
}
