package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.TriggerConfigEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.manager.system.service.EntityManageService;
import cn.foxtech.common.entity.constant.TriggerConfigVOFieldConstant;
import cn.foxtech.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/device/trigger/config")
public class TriggerConfigManageController {
    @Autowired
    private EntityManageService entityManageService;


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(TriggerConfigEntity.class);
        return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
    }

    @PostMapping("entities")
    public AjaxResult selectEntityList(@RequestBody Map<String, Object> body) {
        return this.selectEntityList(body, false);
    }

    @PostMapping("page")
    public AjaxResult selectPageList(@RequestBody Map<String, Object> body) {
        return this.selectEntityList(body, true);
    }

    /**
     * 查询实体数据
     *
     * @param body   查询参数
     * @param isPage 是否是分页模式。分页模式，要求有pageNum/pageSize参数，并按分页格式返回
     * @return 实体数据
     */
    private AjaxResult selectEntityList(Map<String, Object> body, boolean isPage) {
        try {
            List<BaseEntity> entityList = this.entityManageService.getEntityList(TriggerConfigEntity.class, (Object value) -> {
                TriggerConfigEntity entity = (TriggerConfigEntity) value;

                boolean result = true;

                if (body.containsKey(TriggerConfigVOFieldConstant.field_trigger_config_name)) {
                    result = entity.getTriggerConfigName().contains((String) body.get(TriggerConfigVOFieldConstant.field_trigger_config_name));
                }
                if (body.containsKey(TriggerConfigVOFieldConstant.field_object_range)) {
                    result &= entity.getObjectRange().equals(body.get(TriggerConfigVOFieldConstant.field_object_range));
                }
                if (body.containsKey(TriggerConfigVOFieldConstant.field_device_name)) {
                    result &= entity.getDeviceName().equals(body.get(TriggerConfigVOFieldConstant.field_device_name));
                }
                if (body.containsKey(TriggerConfigVOFieldConstant.field_device_type)) {
                    result &= entity.getDeviceType().equals(body.get(TriggerConfigVOFieldConstant.field_device_type));
                }
                if (body.containsKey(TriggerConfigVOFieldConstant.field_trigger_model_name)) {
                    result &= entity.getTriggerModelName().equals(body.get(TriggerConfigVOFieldConstant.field_trigger_model_name));
                }
                if (body.containsKey(TriggerConfigVOFieldConstant.field_trigger_method_name)) {
                    result &= entity.getTriggerMethodName().equals(body.get(TriggerConfigVOFieldConstant.field_trigger_method_name));
                }
                if (body.containsKey(TriggerConfigVOFieldConstant.field_queue_deep)) {
                    result &= entity.getQueueDeep().equals(body.get(TriggerConfigVOFieldConstant.field_queue_deep));
                }

                return result;
            });

            // 获得分页数据
            if (isPage) {
                return PageUtils.getPageList(entityList, body);
            } else {
                return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("entity")
    public AjaxResult queryEntity(@QueryParam("id") Long id) {
        TriggerConfigEntity exist = this.entityManageService.getEntity(id, TriggerConfigEntity.class);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        return AjaxResult.success(exist);
    }

    @PostMapping("entity")
    public AjaxResult insertEntity(@RequestBody Map<String, Object> params) {
        return this.insertOrUpdate(params);
    }

    @PutMapping("entity")
    public AjaxResult updateEntity(@RequestBody Map<String, Object> params) {
        return this.insertOrUpdate(params);
    }

    /**
     * 插入或者更新
     *
     * @param params 参数
     * @return 操作结果
     */
    private AjaxResult insertOrUpdate(Map<String, Object> params) {
        try {
            // 提取业务参数
            String triggerConfigName = (String) params.get(TriggerConfigVOFieldConstant.field_trigger_config_name);
            String objectRange = (String) params.get(TriggerConfigVOFieldConstant.field_object_range);
            String deviceName = (String) params.get(TriggerConfigVOFieldConstant.field_device_name);
            String deviceType = (String) params.get(TriggerConfigVOFieldConstant.field_device_type);
            List<String> objectList = (List<String>) params.get(TriggerConfigVOFieldConstant.field_objects_name);
            String triggerModelName = (String) params.get(TriggerConfigVOFieldConstant.field_trigger_model_name);
            String triggerMethodName = (String) params.get(TriggerConfigVOFieldConstant.field_trigger_method_name);
            Integer queueDeep = (Integer) params.get(TriggerConfigVOFieldConstant.field_queue_deep);
            Map<String, Object> triggerParam = (Map<String, Object>) params.get(TriggerConfigVOFieldConstant.field_trigger_param);
            // 简单校验参数
            if (MethodUtils.hasNull(triggerConfigName, objectRange, deviceName, deviceType, objectList, triggerModelName, triggerMethodName, queueDeep, triggerParam)) {
                return AjaxResult.error("参数不能为空:triggerConfigName, objectRange, deviceName, deviceType, objectList, triggerModelName, triggerMethodName, queueDeep, params");
            }

            // 构造作为参数的实体
            TriggerConfigEntity entity = new TriggerConfigEntity();
            entity.setTriggerConfigName(triggerConfigName);
            entity.setObjectRange(objectRange);
            entity.setDeviceName(deviceName);
            entity.setDeviceType(deviceType);
            entity.setTriggerModelName(triggerModelName);
            entity.setTriggerMethodName(triggerMethodName);
            entity.setQueueDeep(queueDeep);
            entity.setParams(triggerParam);
            entity.getObjectList().addAll(objectList);

            // 触发器配置的业务Key采用的是ID，一开始就是空的，
            // 所以不判断hasNullServiceKey，也在创建阶段不获得是否存在重复对象


            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                TriggerConfigEntity exist = this.entityManageService.getEntity(id, TriggerConfigEntity.class);
                if (exist == null) {
                    return AjaxResult.error("实体不存在");
                }

                // 修改数据
                entity.setId(id);
                this.entityManageService.updateEntity(entity);
                return AjaxResult.success();
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("entity")
    public AjaxResult deleteEntity(@QueryParam("id") Long id) {
        TriggerConfigEntity exist = this.entityManageService.getEntity(id, TriggerConfigEntity.class);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        this.entityManageService.deleteEntity(exist);
        return AjaxResult.success();
    }

    @DeleteMapping("entities")
    public AjaxResult deleteEntityList(@QueryParam("ids") String ids) {
        String[] idList = ids.split(",");

        for (String id : idList) {
            if (id == null || id.isEmpty()) {
                continue;
            }

            this.entityManageService.deleteEntity(Long.parseLong(id), TriggerConfigEntity.class);
        }

        return AjaxResult.success();
    }
}
