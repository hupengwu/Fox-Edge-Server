package cn.foxtech.common.entity.service.devicerecord;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceRecordEntity;
import cn.foxtech.common.entity.entity.DeviceRecordPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DeviceRecordPo是数据库格式的对象，DeviceRecordEntity是内存格式的对象，两者需要进行转换
 */
public class DeviceRecordMaker {
    /**
     * PO转Entity
     *
     * @param poList PO列表
     * @return 实体列表
     */
    public static List<DeviceRecordEntity> makePoList2EntityList(List<DeviceRecordPo> poList) {
        List<DeviceRecordEntity> deviceRecordList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            DeviceRecordPo po = (DeviceRecordPo) entity;

            DeviceRecordEntity config = DeviceRecordMaker.makePo2Entity(po);
            deviceRecordList.add(config);
        }

        return deviceRecordList;
    }

    public static DeviceRecordPo makeEntity2Po(DeviceRecordEntity entity) {
        DeviceRecordPo result = new DeviceRecordPo();
        result.setDeviceName(entity.getDeviceName());
        result.setDeviceType(entity.getDeviceType());
        result.setManufacturer(entity.getManufacturer());
        result.setRecordName(entity.getRecordName());


        result.setId(entity.getId());
        result.setCreateTime(entity.getCreateTime());
        result.setUpdateTime(entity.getUpdateTime());

        result.setRecordData(JsonUtils.buildJsonWithoutException(entity.getRecordData()));
        return result;
    }

    public static DeviceRecordEntity makePo2Entity(DeviceRecordPo entity) {
        DeviceRecordEntity result = new DeviceRecordEntity();
        result.setDeviceName(entity.getDeviceName());
        result.setDeviceType(entity.getDeviceType());
        result.setManufacturer(entity.getManufacturer());
        result.setRecordName(entity.getRecordName());

        result.setId(entity.getId());
        result.setCreateTime(entity.getCreateTime());
        result.setUpdateTime(entity.getUpdateTime());

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getRecordData(), Map.class);
            if (params != null) {
                result.setRecordData(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getRecordData());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getRecordData());
            e.printStackTrace();
        }

        return result;
    }
}
