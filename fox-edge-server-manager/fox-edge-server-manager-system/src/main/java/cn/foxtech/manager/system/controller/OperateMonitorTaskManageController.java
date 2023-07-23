package cn.foxtech.manager.system.controller;


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
import cn.foxtech.manager.system.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.*;

@RestController
@RequestMapping("/kernel/manager/device/operate/task/monitor")
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
            OperateMonitorTaskEntity entity = this.entityManageService.getOperateMonitorTaskEntity(name);
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
        // 查询各种类型的数量
        List<DevicePo> groups = this.deviceEntityService.selectListGroupBy(StringUtils.underscoreName(DeviceVOFieldConstant.field_device_type));
        Map<String, Long> type2count = new HashMap<>();
        for (DevicePo group : groups) {
            type2count.put(group.getDeviceType(), group.getId());
        }

        // 将设备数量信息,添加进去
        for (Map<String, Object> map : list) {
            String deviceType = (String) map.get(OperateMonitorTaskVOFieldConstant.field_device_type);
            if (deviceType == null) {
                continue;
            }

            Long countDeviceType = type2count.get(deviceType);

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
            String deviceType = (String) params.get(OperateMonitorTaskVOFieldConstant.field_device_type);
            List<Map<String, Object>> operateParam = (List<Map<String, Object>>) params.get(OperateMonitorTaskVOFieldConstant.field_template_param);
            List<Object> deviceIds = (List<Object>) params.get(OperateMonitorTaskVOFieldConstant.field_device_ids);
            // 简单校验参数
            if (MethodUtils.hasNull(templateName, deviceType, deviceIds, operateParam)) {
                return AjaxResult.error("参数不能为空:templateName, deviceType,deviceIds, operateParam");
            }

            // 构造作为参数的实体
            OperateMonitorTaskEntity entity = new OperateMonitorTaskEntity();
            entity.setTemplateName(templateName);
            entity.setDeviceType(deviceType);
            entity.setTemplateParam(operateParam);
            entity.getDeviceIds().addAll(this.makeLongList(deviceType, deviceIds));

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }


            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
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

    private Set<Long> makeLongList(String deviceType, List<Object> deviceIds) {
        if (deviceIds.isEmpty()){
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
