package cn.foxtech.period.service.mapper.periodtask;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.period.service.entity.PeriodTaskEntity;
import cn.foxtech.period.service.entity.PeriodTaskPo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DeviceConfigPo是数据库格式的对象，DeviceConfigEntity是内存格式的对象，两者需要进行转换
 */
public class PeriodTaskMaker {
    /**
     * PO转Entity
     *
     * @param deviceList
     * @return
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> deviceList) {
        List<BaseEntity> deviceConfigList = new ArrayList<>();
        for (BaseEntity entity : deviceList) {
            PeriodTaskPo po = (PeriodTaskPo) entity;

            PeriodTaskEntity config = PeriodTaskMaker.makePo2Entity(po);
            deviceConfigList.add(config);
        }

        return deviceConfigList;
    }

    public static PeriodTaskPo makeEntity2Po(PeriodTaskEntity entity) {
        PeriodTaskPo result = new PeriodTaskPo();
        result.bind(entity);

        result.setTaskParam(JsonUtils.buildJsonWithoutException(entity.getTaskParam()));
        result.setDeviceIds(JsonUtils.buildJsonWithoutException(entity.getDeviceIds()));
        result.setObjectIds(JsonUtils.buildJsonWithoutException(entity.getObjectIds()));
        return result;
    }

    public static PeriodTaskEntity makePo2Entity(PeriodTaskPo entity) {
        PeriodTaskEntity result = new PeriodTaskEntity();
        result.bind(entity);

        try {
            if (entity.getDeviceIds().startsWith("[") && entity.getDeviceIds().endsWith("]")) {
                List<Object> params = JsonUtils.buildObject(entity.getDeviceIds(), List.class);
                if (params != null) {
                    result.setDeviceIds(params);
                } else {
                    System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceIds() + ":" + entity.getTaskParam());
                }
            }
            if (entity.getObjectIds().startsWith("[") && entity.getObjectIds().endsWith("]")) {
                List<String> params = JsonUtils.buildObject(entity.getObjectIds(), List.class);
                if (params != null) {
                    result.setObjectIds(params);
                } else {
                    System.out.println("设备配置参数转换Json对象失败：" + entity.getObjectIds() + ":" + entity.getTaskParam());
                }
            }
            if (entity.getTaskParam().startsWith("{") && entity.getTaskParam().endsWith("}")) {
                Map<String, Object> params = JsonUtils.buildObject(entity.getTaskParam(), Map.class);
                if (params != null) {
                    result.setTaskParam(params);
                } else {
                    System.out.println("设备配置参数转换Json对象失败：" + entity.getTaskParam() + ":" + entity.getTaskParam());
                }
            }


        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getTaskName() + ":" + entity.getTaskParam());
            e.printStackTrace();
        }

        return result;
    }
}
