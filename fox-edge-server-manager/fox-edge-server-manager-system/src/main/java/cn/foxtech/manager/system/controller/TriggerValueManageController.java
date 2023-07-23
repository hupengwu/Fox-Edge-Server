package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.TriggerObjectEntity;
import cn.foxtech.common.entity.entity.TriggerValueEntity;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.ExtendUtils;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.pair.Pair;
import cn.foxtech.manager.system.service.EntityManageService;
import cn.foxtech.common.entity.constant.TriggerValueVOFieldConstant;
import cn.foxtech.common.entity.entity.DeviceObjectValue;
import cn.foxtech.common.entity.service.triggerobject.TriggerObjectEntityMapper;
import cn.foxtech.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/kernel/manager/trigger/value")
public class TriggerValueManageController {
    @Autowired
    private TriggerObjectEntityMapper mapper;

    @Autowired
    private EntityManageService entityManageService;


    @PostMapping("page")
    public AjaxResult selectEntityListByPage(@RequestBody Map<String, Object> body) {
        // 提取业务参数
        String deviceName = (String) body.get(TriggerValueVOFieldConstant.field_device_name);
        String deviceType = (String) body.get(TriggerValueVOFieldConstant.field_device_type);
        String objectName = (String) body.get(TriggerValueVOFieldConstant.field_object_name);
        String triggerConfigName = (String) body.get(TriggerValueVOFieldConstant.field_trigger_config_name);
        Integer pageNum = (Integer) body.get(TriggerValueVOFieldConstant.field_page_num);
        Integer pageSize = (Integer) body.get(TriggerValueVOFieldConstant.field_page_size);

        // 简单校验参数
        if (MethodUtils.hasNull(pageNum, pageSize)) {
            return AjaxResult.error("参数不能为空:pageNum, pageSize");
        }

        StringBuilder sb = new StringBuilder();
        if (deviceName != null) {
            sb.append(" (device_name = '").append(deviceName).append("') AND");
        }
        if (deviceType != null) {
            sb.append(" (device_type = '").append(deviceType).append("') AND");
        }
        if (objectName != null) {
            sb.append(" (object_name = '").append(objectName).append("') AND");
        }
        if (triggerConfigName != null) {
            sb.append(" (trigger_config_name = '").append(triggerConfigName).append("') AND");
        }
        String filter = sb.toString();
        if (!filter.isEmpty()) {
            filter = filter.substring(0, filter.length() - "AND".length());
        }

        return this.selectEntityListPage(filter, "ASC", pageNum, pageSize);
    }

    /**
     * 分页查询数据
     *
     * @param filter
     * @param order
     * @param pageNmu
     * @param pageSize
     * @return
     */
    public AjaxResult selectEntityListPage(String filter, String order, long pageNmu, long pageSize) {
        try {
            // 从数据库的deviceObject中查询总数
            String selectCount = PageUtils.makeSelectCountSQL("tb_trigger_object", filter);
            Integer total = this.mapper.executeSelectCount(selectCount);

            // 分页查询数据
            String selectPage = PageUtils.makeSelectSQLPage("tb_trigger_object", filter, order, total, pageNmu, pageSize);
            List<TriggerObjectEntity> entityList = this.mapper.executeSelectData(selectPage);

            // 从redis中直接读取真正的设备数值
            Set<Object> deviceNames = new HashSet<>();
            RedisReader redisReader = this.entityManageService.getRedisReader(TriggerValueEntity.class);
            for (TriggerObjectEntity entity : entityList) {
                TriggerValueEntity triggerValueEntity = new TriggerValueEntity();
                triggerValueEntity.setDeviceName(entity.getDeviceName());
                triggerValueEntity.setTriggerConfigName(entity.getTriggerConfigName());
                deviceNames.add(triggerValueEntity.makeServiceKey());
            }
            Map<String, BaseEntity> deviceValueMap = redisReader.readEntityMap(deviceNames);

            // 组织扩展信息：
            Long time = System.currentTimeMillis();
            List<Map<String, Object>> extendList = new ArrayList<>();
            for (BaseEntity entity : deviceValueMap.values()) {
                TriggerValueEntity triggerValueEntity = (TriggerValueEntity) entity;
                for (String key : triggerValueEntity.getParams().keySet()) {
                    DeviceObjectValue deviceObjectValue = triggerValueEntity.getParams().get(key);

                    Map<String, Object> map = new HashMap<>();
                    // 业务key
                    map.put(TriggerValueVOFieldConstant.field_device_name, triggerValueEntity.getDeviceName());
                    map.put(TriggerValueVOFieldConstant.field_trigger_config_name, triggerValueEntity.getTriggerConfigName());
                    map.put(TriggerValueVOFieldConstant.field_object_name, key);
                    //业务数值
                    map.put(TriggerValueVOFieldConstant.field_object_time, (time - deviceObjectValue.getTime()) / 1000);
                    map.put(TriggerValueVOFieldConstant.field_object_value, deviceObjectValue.getValue());

                    map.put(TriggerValueVOFieldConstant.field_update_time, deviceObjectValue.getTime());

                    extendList.add(map);
                }

            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", EntityVOBuilder.buildVOList(entityList));
            data.put("total", total);

            // 增加扩展数据
            List<Pair<String, String>> pairs = new ArrayList<>();
            Set<String> extend = new HashSet<>();
            pairs.add(new Pair<>(TriggerValueVOFieldConstant.field_device_name, TriggerValueVOFieldConstant.field_device_name));
            pairs.add(new Pair<>(TriggerValueVOFieldConstant.field_trigger_config_name, TriggerValueVOFieldConstant.field_trigger_config_name));
            pairs.add(new Pair<>(TriggerValueVOFieldConstant.field_object_name, TriggerValueVOFieldConstant.field_object_name));
            extend.add(TriggerValueVOFieldConstant.field_object_value);
            extend.add(TriggerValueVOFieldConstant.field_object_time);
            extend.add(TriggerValueVOFieldConstant.field_update_time);
            ExtendUtils.extend((List<Map<String, Object>>) data.get("list"), extendList, pairs, extend);

            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
