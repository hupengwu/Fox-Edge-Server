package cn.foxtech.kernel.system.service.controller;


import cn.foxtech.common.entity.constant.DeviceValueExVOFieldConstant;
import cn.foxtech.common.entity.entity.DeviceObjectValue;
import cn.foxtech.common.entity.entity.DeviceValueExEntity;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/device/value/ex")
public class DeviceValueExManageController {

    @Autowired
    private EntityManageService entityManageService;


    @PostMapping("page")
    public AjaxResult selectEntityListByPage(@RequestBody Map<String, Object> body) {
        try {
            // 提取业务参数
            Integer pageNum = (Integer) body.get(DeviceValueExVOFieldConstant.field_page_num);
            Integer pageSize = (Integer) body.get(DeviceValueExVOFieldConstant.field_page_size);
            String objectName = (String) body.get(DeviceValueExVOFieldConstant.field_object_name);


            // 简单校验参数
            if (MethodUtils.hasNull(pageNum, pageSize)) {
                return AjaxResult.error("参数不能为空:pageNum, pageSize");
            }

            // 从redis读取缓存数据
            RedisReader redisReader = this.entityManageService.getRedisReader(DeviceValueExEntity.class);
            List<Map<String, Object>> mapList = redisReader.readHashMapList();

            // 筛选数据
            mapList = this.selectEntityList(mapList, body);

            // 分页处理
            Map<String, Object> data = this.getPage(mapList, objectName, pageNum, pageSize);
            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    private List<Map<String, Object>> selectEntityList(List<Map<String, Object>> mapList, Map<String, Object> body) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            boolean result = true;
            if (body.containsKey(DeviceValueExVOFieldConstant.field_manufacturer)) {
                result &= ((String) map.get(DeviceValueExVOFieldConstant.field_manufacturer)).contains((String) body.get(DeviceValueExVOFieldConstant.field_manufacturer));
            }
            if (body.containsKey(DeviceValueExVOFieldConstant.field_device_type)) {
                result &= ((String) map.get(DeviceValueExVOFieldConstant.field_device_type)).contains((String) body.get(DeviceValueExVOFieldConstant.field_device_type));
            }
            if (body.containsKey(DeviceValueExVOFieldConstant.field_device_name)) {
                result &= ((String) map.get(DeviceValueExVOFieldConstant.field_device_name)).contains((String) body.get(DeviceValueExVOFieldConstant.field_device_name));
            }

            if (result) {
                resultList.add(map);
            }
        }

        return resultList;
    }

    private Map<String, Object> getPage(List<Map<String, Object>> mapList, String objectName, Integer pageNmu, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();


        int total = 0;
        for (Map<String, Object> map : mapList) {
            Map<String, DeviceObjectValue> params = (Map<String, DeviceObjectValue>) map.get(DeviceValueExVOFieldConstant.field_params);
            if (params == null) {
                continue;
            }

            // 检查：是否需要进行对象级别名称的过滤
            if (!MethodUtils.hasEmpty(objectName)) {
                if (params.containsKey(objectName)) {
                    total += 1;
                } else {
                    total += params.size();
                }

                continue;
            }


            total += params.size();
        }

        result.put("total", total);


        List<Map<String, Object>> list = new ArrayList<>();
        result.put("list", list);

        int index = 0;
        for (Map<String, Object> map : mapList) {
            Map<String, DeviceObjectValue> params = (Map<String, DeviceObjectValue>) map.get(DeviceValueExVOFieldConstant.field_params);
            if (params == null) {
                continue;
            }

            if ((index + params.size()) < (pageNmu - 1) * pageSize) {
                index += params.size();
                continue;
            }

            for (String key : params.keySet()) {
                if (list.size() >= pageSize) {
                    return result;
                }

                Map<String, Object> value = (Map<String, Object>) params.get(key);
                if (value == null) {
                    continue;
                }

                // 检查：是否需要进行对象级别名称的过滤
                if (!MethodUtils.hasEmpty(objectName) && !key.equals(objectName)) {
                    continue;
                }

                Map<String, Object> data = new HashMap<>();
                data.put(DeviceValueExVOFieldConstant.field_device_name, map.get(DeviceValueExVOFieldConstant.field_device_name));
                data.put(DeviceValueExVOFieldConstant.field_device_type, map.get(DeviceValueExVOFieldConstant.field_device_type));
                data.put(DeviceValueExVOFieldConstant.field_manufacturer, map.get(DeviceValueExVOFieldConstant.field_manufacturer));
                data.put(DeviceValueExVOFieldConstant.field_update_time, map.get(DeviceValueExVOFieldConstant.field_update_time));
                data.put(DeviceValueExVOFieldConstant.field_id, map.get(DeviceValueExVOFieldConstant.field_id));

                data.put(DeviceValueExVOFieldConstant.field_object_name, key);
                data.put(DeviceValueExVOFieldConstant.field_object_time, value.get("time"));
                data.put(DeviceValueExVOFieldConstant.field_object_value, value.get("value"));

                list.add(data);
            }


            index += params.size();
        }

        return result;
    }
}
