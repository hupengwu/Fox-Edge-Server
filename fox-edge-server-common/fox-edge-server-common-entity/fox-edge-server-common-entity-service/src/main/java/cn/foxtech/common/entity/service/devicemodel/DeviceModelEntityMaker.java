package cn.foxtech.common.entity.service.devicemodel;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceModelEntity;
import cn.foxtech.common.entity.entity.DeviceModelPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DeviceModelPo是数据库格式的对象，DeviceModelEntity是内存格式的对象，两者需要进行转换
 */
public class DeviceModelEntityMaker {
    /**
     * PO转Entity
     *
     * @param poList po列表
     * @return 对象列表
     */
    public static List<BaseEntity> makePoList2EntityList(List poList) {
        List<BaseEntity> resultList = new ArrayList<>();
        for (Object entity : poList) {
            DeviceModelPo po = (DeviceModelPo) entity;


            DeviceModelEntity result = DeviceModelEntityMaker.makePo2Entity(po);
            resultList.add(result);
        }

        return resultList;
    }

    public static DeviceModelPo makeEntity2Po(DeviceModelEntity entity) {
        DeviceModelPo result = new DeviceModelPo();
        result.bind(entity);

        result.setServiceParam(JsonUtils.buildJsonWithoutException(entity.getServiceParam()));
        result.setModelSchema(JsonUtils.buildJsonWithoutException(entity.getModelSchema()));
        return result;
    }

    public static DeviceModelEntity makePo2Entity(DeviceModelPo entity) {
        DeviceModelEntity result = new DeviceModelEntity();
        result.bind(entity);

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getServiceParam(), Map.class);
            if (params != null) {
                result.setServiceParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getServiceParam() + ":" + entity.getServiceParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getModelName() + ":" + entity.getServiceParam());
            e.printStackTrace();
        }

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getModelSchema(), Map.class);
            if (params != null) {
                result.setModelSchema(params);
            } else {
                System.out.println("设备扩展参数转换Json对象失败：" + entity.getModelName() + ":" + entity.getModelSchema());
            }
        } catch (Exception e) {
            System.out.println("设备扩展参数转换Json对象失败：" + entity.getModelName() + ":" + entity.getModelSchema());
            e.printStackTrace();
        }

        return result;
    }
}
