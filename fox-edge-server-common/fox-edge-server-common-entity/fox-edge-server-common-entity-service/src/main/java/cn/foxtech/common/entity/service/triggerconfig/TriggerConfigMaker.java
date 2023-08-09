package cn.foxtech.common.entity.service.triggerconfig;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.TriggerConfigEntity;
import cn.foxtech.common.entity.entity.TriggerConfigPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DeviceConfigPo是数据库格式的对象，DeviceConfigEntity是内存格式的对象，两者需要进行转换
 */
public class TriggerConfigMaker {
    /**
     * PO转Entity
     *
     * @param poList PO列表
     * @return 实体列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> poList) {
        List<BaseEntity> deviceConfigList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            TriggerConfigPo po = (TriggerConfigPo) entity;


            TriggerConfigEntity result = TriggerConfigMaker.makePo2Entity(po);
            deviceConfigList.add(result);
        }

        return deviceConfigList;
    }

    public static TriggerConfigPo makeEntity2Po(TriggerConfigEntity entity) {
        TriggerConfigPo result = new TriggerConfigPo();
        result.setObjectRange(entity.getObjectRange());
        result.setDeviceName(entity.getDeviceName());
        result.setDeviceType(entity.getDeviceType());
        result.setQueueDeep(entity.getQueueDeep());
        result.setTriggerConfigName(entity.getTriggerConfigName());
        result.setTriggerMethodName(entity.getTriggerMethodName());
        result.setTriggerModelName(entity.getTriggerModelName());

        result.setId(entity.getId());
        result.setCreateTime(entity.getCreateTime());
        result.setUpdateTime(entity.getUpdateTime());

        result.setObjectsName(JsonUtils.buildJsonWithoutException(entity.getObjectList()));
        result.setTriggerParam(JsonUtils.buildJsonWithoutException(entity.getParams()));
        return result;
    }

    public static TriggerConfigEntity makePo2Entity(TriggerConfigPo entity) {
        TriggerConfigEntity result = new TriggerConfigEntity();
        result.setObjectRange(entity.getObjectRange());
        result.setDeviceName(entity.getDeviceName());
        result.setDeviceType(entity.getDeviceType());
        result.setQueueDeep(entity.getQueueDeep());
        result.setTriggerConfigName(entity.getTriggerConfigName());
        result.setTriggerMethodName(entity.getTriggerMethodName());
        result.setTriggerModelName(entity.getTriggerModelName());


        result.setId(entity.getId());
        result.setCreateTime(entity.getCreateTime());
        result.setUpdateTime(entity.getUpdateTime());

        try {
            List<String> objectList = JsonUtils.buildObject(entity.getObjectsName(), List.class);
            if (objectList != null) {
                result.getObjectList().clear();
                result.getObjectList().addAll(objectList);
            } else {
                System.out.println("触发器配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getTriggerParam());
            }

            Map<String, Object> params = JsonUtils.buildObject(entity.getTriggerParam(), Map.class);
            if (params != null) {
                result.setParams(params);
            } else {
                System.out.println("触发器配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getTriggerParam());
            }
        } catch (Exception e) {
            System.out.println("触发器配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getTriggerParam());
            e.printStackTrace();
        }

        return result;
    }
}
