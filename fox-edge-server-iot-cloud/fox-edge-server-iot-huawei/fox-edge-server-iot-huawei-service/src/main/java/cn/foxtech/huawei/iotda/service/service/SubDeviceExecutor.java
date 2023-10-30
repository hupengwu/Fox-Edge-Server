package cn.foxtech.huawei.iotda.service.service;

import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.common.entity.utils.ExtendConfigUtils;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.huawei.iotda.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SubDeviceExecutor {
    @Autowired
    private EntityManageService entityManageService;

    /**
     * 通知华为云，在网关设备下，添加子设备
     */
    public List<Long> getDeviceIds() {
        List<Long> deviceIds = new ArrayList<>();
        this.getDeviceIdsAndKeys(deviceIds, null);

        return deviceIds;
    }

    public List<String> getDeviceKeys() {
        List<String> deviceKeys = new ArrayList<>();
        this.getDeviceIdsAndKeys(null, deviceKeys);

        return deviceKeys;
    }

    private void getDeviceIdsAndKeys(List<Long> deviceIds, List<String> deviceKeys) {
        // 获得全体扩展配置信息
        List<BaseEntity> extendConfigEntityList = this.entityManageService.getEntityList(ExtendConfigEntity.class);

        // 获得设备列表信息
        List<BaseEntity> deviceEntityList = this.entityManageService.getEntityList(DeviceEntity.class);

        // 将设备列表转换未Map结构
        List<Map<String, Object>> deviceMapList = BeanMapUtils.objectToMap(deviceEntityList);

        // 为设备的Map组装上扩展信息
        ExtendConfigUtils.extend(deviceMapList, extendConfigEntityList, DeviceEntity.class);

        for (Map<String, Object> deviceMap : deviceMapList) {
            Map<String, Object> extendMap = (Map<String, Object>) deviceMap.get(DeviceVOFieldConstant.field_extend_param);
            if (extendMap == null) {
                continue;
            }

            if (!Boolean.TRUE.equals(extendMap.get("huaweiIotDA"))) {
                continue;
            }


            if (deviceIds != null) {
                deviceIds.add((Long) (deviceMap.get(DeviceVOFieldConstant.field_id)));
            }
            if (deviceKeys != null) {
                deviceKeys.add((String) (deviceMap.get(DeviceVOFieldConstant.field_device_name)));
            }
        }
    }
}
