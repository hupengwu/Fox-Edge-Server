package cn.foxtech.common.entity.service.device;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DevicePo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DevicePo是数据库格式的对象，DeviceEntity是内存格式的对象，两者需要进行转换
 */
public class DeviceEntityMaker {
    /**
     * PO转Entity
     *
     * @param poList po列表
     * @return 对象列表
     */
    public static List<BaseEntity> makePoList2EntityList(List poList) {
        List<BaseEntity> resultList = new ArrayList<>();
        for (Object entity : poList) {
            DevicePo po = (DevicePo) entity;


            DeviceEntity result = DeviceEntityMaker.makePo2Entity(po);
            resultList.add(result);
        }

        return resultList;
    }

    public static DevicePo makeEntity2Po(DeviceEntity entity) {
        DevicePo result = new DevicePo();
        result.bind(entity);

        result.setDeviceParam(JsonUtils.buildJsonWithoutException(entity.getDeviceParam()));
        result.setExtendParam(JsonUtils.buildJsonWithoutException(entity.getExtendParam()));
        return result;
    }

    public static DeviceEntity makePo2Entity(DevicePo entity) {
        DeviceEntity result = new DeviceEntity();
        result.bind(entity);

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getDeviceParam(), Map.class);
            if (params != null) {
                result.setDeviceParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getDeviceParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getDeviceParam());
            e.printStackTrace();
        }

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getExtendParam(), Map.class);
            if (params != null) {
                result.setExtendParam(params);
            } else {
                System.out.println("设备扩展参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getExtendParam());
            }
        } catch (Exception e) {
            System.out.println("设备扩展参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getExtendParam());
            e.printStackTrace();
        }

        return result;
    }
}
