package cn.foxtech.common.entity.service.devicehistory;

import cn.foxtech.common.entity.entity.DeviceHistoryEntity;
import cn.foxtech.common.entity.entity.DeviceHistoryPo;

import java.util.ArrayList;
import java.util.List;

/**
 * DeviceConfigPo是数据库格式的对象，DeviceConfigEntity是内存格式的对象，两者需要进行转换
 */
public class DeviceHistoryEntityMaker {
    /**
     * PO转Entity
     *
     * @param poList po列表
     * @return 实体列表
     */
    public static List<DeviceHistoryEntity> makePoList2EntityList(List<DeviceHistoryPo> poList) {
        List<DeviceHistoryEntity> deviceConfigList = new ArrayList<>();
        for (DeviceHistoryPo entity : poList) {
            DeviceHistoryPo po = entity;


            DeviceHistoryEntity config = DeviceHistoryEntityMaker.makePo2Entity(po);
            deviceConfigList.add(config);
        }

        return deviceConfigList;
    }

    public static DeviceHistoryPo makeEntity2Po(DeviceHistoryEntity entity) {
        DeviceHistoryPo po = new DeviceHistoryPo();
        po.setDeviceId(entity.getDeviceId());
        po.setObjectName(entity.getObjectName());
        po.setParamType(entity.getParamValue().getClass().getSimpleName());
        po.setParamValue(entity.getParamValue().toString());


        po.setId(entity.getId());
        po.setCreateTime(entity.getCreateTime());

        return po;
    }

    public static DeviceHistoryEntity makePo2Entity(DeviceHistoryPo po) {
        DeviceHistoryEntity entity = new DeviceHistoryEntity();
        entity.setId(po.getId());

        entity.setDeviceId(po.getDeviceId());
        entity.setObjectName(po.getObjectName());
        entity.setCreateTime(po.getCreateTime());

        if (Integer.class.getSimpleName().equals(po.getParamType())) {
            entity.setParamValue(Integer.parseInt(po.getParamValue()));
            return entity;
        }
        if (Long.class.getSimpleName().equals(po.getParamType())) {
            entity.setParamValue(Long.parseLong(po.getParamValue()));
            return entity;
        }
        if (Short.class.getSimpleName().equals(po.getParamType())) {
            entity.setParamValue(Short.parseShort(po.getParamValue()));
            return entity;
        }
        if (Byte.class.getSimpleName().equals(po.getParamType())) {
            entity.setParamValue(Byte.parseByte(po.getParamValue()));
            return entity;
        }
        if (Double.class.getSimpleName().equals(po.getParamType())) {
            entity.setParamValue(Double.parseDouble(po.getParamValue()));
            return entity;
        }
        if (Float.class.getSimpleName().equals(po.getParamType())) {
            entity.setParamValue(Float.parseFloat(po.getParamValue()));
            return entity;
        }
        if (String.class.getSimpleName().equals(po.getParamType())) {
            entity.setParamValue(po.getParamValue());
            return entity;
        }

        return entity;
    }
}
