package cn.foxtech.kernel.system.service.controller;


import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.constant.OperateMonitorTaskVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DevicePo;
import cn.foxtech.common.entity.entity.OperateMonitorTaskEntity;
import cn.foxtech.common.entity.service.device.DeviceEntityService;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.common.utils.string.StringUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.*;

@RestController
@RequestMapping("/device/operate/task/monitor")
public class OperateMonitorTaskManageController {
    @Autowired
    protected DeviceEntityService deviceEntityService;
    @Autowired
    private EntityManageService entityManageService;

    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(OperateMonitorTaskEntity.class);
        return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
    }

    @PostMapping("entitiesByName")
    public AjaxResult selectEntityListByNameList(@RequestBody List<String> nameList) {
        Map<String, BaseEntity> result = new HashMap<>();
        for (String name : nameList) {
            OperateMonitorTaskEntity entity = new OperateMonitorTaskEntity();
            entity.setTemplateName(name);
            entity = this.entityManageService.getEntity(entity.makeServiceKey(), OperateMonitorTaskEntity.class);
            if (entity == null) {
                continue;
            }

            result.put(entity.makeServiceKey(), entity);
        }

        return AjaxResult.success(EntityVOBuilder.buildVOList(result.values()));
    }

    @PostMapping("entities")
    public AjaxResult selectEntityList(@RequestBody Map<String, Object> body) {
        AjaxResult ajaxResult = this.selectEntityList(body, false);

        List<Map<String, Object>> list = (List<Map<String, Object>>) ajaxResult.get("data");
        this.addDeviceCount(list);

        return ajaxResult;
    }

    @PostMapping("page")
    public AjaxResult selectPageList(@RequestBody Map<String, Object> body) {
        AjaxResult ajaxResult = this.selectEntityList(body, true);
        Map<String, Object> data = (Map<String, Object>) ajaxResult.get("data");
        if (data == null) {
            return ajaxResult;
        }

        List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");
        this.addDeviceCount(list);

        return ajaxResult;
    }

    private void addDeviceCount(List<Map<String, Object>> list) {
        String field1 = StringUtils.underscoreName(DeviceVOFieldConstant.field_manufacturer);
        String field2 = StringUtils.underscoreName(DeviceVOFieldConstant.field_device_type);

        // 查询各种类型的数量
        List<DevicePo> groups = this.deviceEntityService.selectListGroupBy(field1, field2);
        Map<String, Long> type2count = new HashMap<>();
        for (DevicePo group : groups) {
            String key = group.getManufacturer() + ":" + group.getDeviceType();
            type2count.put(key, group.getId());
        }

        // 将设备数量信息,添加进去
        for (Map<String, Object> map : list) {
            String manufacturer = (String) map.get(OperateMonitorTaskVOFieldConstant.field_manufacturer);
            String deviceType = (String) map.get(OperateMonitorTaskVOFieldConstant.field_device_type);
            if (manufacturer == null || deviceType == null) {
                continue;
            }

            String key = manufacturer + ":" + deviceType;
            Long countDeviceType = type2count.get(key);

            List deviceIds = (List) map.get(OperateMonitorTaskVOFieldConstant.field_device_ids);

            map.put(OperateMonitorTaskVOFieldConstant.field_count_device_type, countDeviceType);
            map.put(OperateMonitorTaskVOFieldConstant.field_count_device_bind, deviceIds.size());
        }
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(OperateMonitorTaskEntity.class, (Object value) -> {
                OperateMonitorTaskEntity entity = (OperateMonitorTaskEntity) value;

                boolean result = true;

                if (body.containsKey(OperateMonitorTaskVOFieldConstant.field_template_name)) {
                    result = entity.getTemplateName().contains((String) body.get(OperateMonitorTaskVOFieldConstant.field_template_name));
                }
                if (body.containsKey(OperateMonitorTaskVOFieldConstant.field_device_type)) {
                    result &= entity.getDeviceType().equals(body.get(OperateMonitorTaskVOFieldConstant.field_device_type));
                }
                if (body.containsKey(OperateMonitorTaskVOFieldConstant.field_manufacturer)) {
                    result &= entity.getManufacturer().equals(body.get(OperateMonitorTaskVOFieldConstant.field_manufacturer));
                }
                if (body.containsKey(OperateMonitorTaskVOFieldConstant.field_manufacturer)) {
                    result &= entity.getManufacturer().equals(body.get(OperateMonitorTaskVOFieldConstant.field_manufacturer));
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
        OperateMonitorTaskEntity exist = this.entityManageService.getEntity(id, OperateMonitorTaskEntity.class);
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
            String templateName = (String) params.get(OperateMonitorTaskVOFieldConstant.field_template_name);
            String manufacturer = (String) params.get(OperateMonitorTaskVOFieldConstant.field_manufacturer);
            String deviceType = (String) params.get(OperateMonitorTaskVOFieldConstant.field_device_type);
            List<Map<String, Object>> templateParam = (List<Map<String, Object>>) params.get(OperateMonitorTaskVOFieldConstant.field_template_param);
            List<Object> deviceIds = (List<Object>) params.get(OperateMonitorTaskVOFieldConstant.field_device_ids);
            Map<String, Object> taskParam = (Map<String, Object>) params.get(OperateMonitorTaskVOFieldConstant.field_task_param);

            // 简单校验参数
            if (MethodUtils.hasNull(templateName, manufacturer, deviceType, deviceIds, templateParam, taskParam)) {
                return AjaxResult.error("参数不能为空: templateName, manufacturer, deviceType, deviceIds, templateParam, taskParam) ");
            }

            // 构造作为参数的实体
            OperateMonitorTaskEntity entity = new OperateMonitorTaskEntity();
            entity.setTemplateName(templateName);
            entity.setManufacturer(manufacturer);
            entity.setDeviceType(deviceType);
            entity.setTemplateParam(templateParam);
            entity.setTaskParam(taskParam);
            entity.getDeviceIds().addAll(this.makeLongList(manufacturer, deviceType, deviceIds));
            entity.setDefaultValue();

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }


            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (params.get("id") == null) {
                OperateMonitorTaskEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), OperateMonitorTaskEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                OperateMonitorTaskEntity exist = this.entityManageService.getEntity(id, OperateMonitorTaskEntity.class);
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

    private Set<Long> makeLongList(String manufacturer, String deviceType, List<Object> deviceIds) {
        if (deviceIds.isEmpty()) {
            return new HashSet<>();
        }

        List<BaseEntity> deviceList = this.deviceEntityService.selectListBatchIds(deviceIds);
        Map<Long, BaseEntity> id2entity = ContainerUtils.buildMapByKey(deviceList, BaseEntity::getId);

        Set<Long> result = new HashSet<>();
        for (Object id : deviceIds) {
            Long value = NumberUtils.makeLong(id);
            if (value == null) {
                continue;
            }

            DeviceEntity entity = (DeviceEntity) id2entity.get(value);
            if (entity == null) {
                continue;
            }

            if (!entity.getDeviceType().equals(deviceType)) {
                continue;
            }
            if (!entity.getManufacturer().equals(manufacturer)) {
                continue;
            }

            result.add(value);
        }

        return result;
    }

    @DeleteMapping("entity")
    public AjaxResult deleteEntity(@QueryParam("id") Long id) {
        OperateMonitorTaskEntity exist = this.entityManageService.getEntity(id, OperateMonitorTaskEntity.class);
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

            this.entityManageService.deleteEntity(Long.parseLong(id), OperateMonitorTaskEntity.class);
        }

        return AjaxResult.success();
    }
}
