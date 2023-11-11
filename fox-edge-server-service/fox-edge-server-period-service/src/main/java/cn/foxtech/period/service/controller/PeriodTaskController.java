package cn.foxtech.period.service.controller;


import cn.foxtech.common.entity.constant.PeriodTaskVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.period.service.entity.PeriodTaskEntity;
import cn.foxtech.period.service.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.*;

@RestController
@RequestMapping("/service/period/task")
public class PeriodTaskController {
    @Autowired
    private EntityManageService entityManageService;

    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(PeriodTaskEntity.class);
        return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
    }

    @PostMapping("objectNames")
    public AjaxResult selectEntityListByNameList(@RequestBody Map<String, Object> body) {
        Set<String> objectNames = new HashSet<>();
        this.entityManageService.getEntityList(PeriodTaskEntity.class, (Object value) -> {
            PeriodTaskEntity entity = (PeriodTaskEntity) value;

            boolean result = true;

            // 三个参数简单判定该记录是否包含需要的objectName
            if (body.containsKey(PeriodTaskVOFieldConstant.field_device_type)) {
                result &= entity.getDeviceType().equals(body.get(PeriodTaskVOFieldConstant.field_device_type));
            }
            if (body.containsKey(PeriodTaskVOFieldConstant.field_manufacturer)) {
                result &= entity.getManufacturer().equals(body.get(PeriodTaskVOFieldConstant.field_manufacturer));
            }
            if (body.containsKey(PeriodTaskVOFieldConstant.field_task_name)) {
                result &= entity.getTaskName().equals(body.get(PeriodTaskVOFieldConstant.field_task_name));
            }
            if (!result) {
                return false;
            }

            objectNames.addAll(entity.getObjectIds());

            return true;
        });


        return AjaxResult.success(objectNames);
    }

    @PostMapping("entities")
    public AjaxResult selectEntityList(@RequestBody Map<String, Object> body) {
        AjaxResult ajaxResult = this.selectEntityList(body, false);

        List<Map<String, Object>> list = (List<Map<String, Object>>) ajaxResult.get("data");
        this.extendDeviceCount(list);

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
        this.extendDeviceCount(list);
        this.extendObjectCount(list);

        return ajaxResult;
    }

    private void extendDeviceCount(List<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            String manufacturer = (String) map.get(PeriodTaskVOFieldConstant.field_manufacturer);
            if (manufacturer == null) {
                continue;
            }

            String deviceType = (String) map.get(PeriodTaskVOFieldConstant.field_device_type);
            if (deviceType == null) {
                continue;
            }

            int countDeviceType = this.entityManageService.getEntityCount(DeviceEntity.class, (Object value) -> {
                DeviceEntity entity = (DeviceEntity) value;
                return manufacturer.equals(entity.getManufacturer()) && deviceType.equals(entity.getDeviceType());
            });

            List deviceIds = (List) map.get(PeriodTaskVOFieldConstant.field_device_ids);

            // 如果是指定选中设备，那么计算的是选择的数量，否则绑定的是全体数量
            Boolean selectDevice = (Boolean) map.get(PeriodTaskVOFieldConstant.field_select_device);
            if (Boolean.TRUE.equals(selectDevice)) {
                map.put(PeriodTaskVOFieldConstant.field_count_device_type, countDeviceType);
                map.put(PeriodTaskVOFieldConstant.field_count_device_bind, deviceIds.size());
            } else {
                map.put(PeriodTaskVOFieldConstant.field_count_device_type, countDeviceType);
                map.put(PeriodTaskVOFieldConstant.field_count_device_bind, countDeviceType);
            }

        }
    }

    private void extendObjectCount(List<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            List<String> objectIds = (List<String>) map.get(PeriodTaskVOFieldConstant.field_object_ids);
            if (objectIds == null) {
                map.put(PeriodTaskVOFieldConstant.field_count_object_bind, 0);
            } else {
                map.put(PeriodTaskVOFieldConstant.field_count_object_bind, objectIds.size());
            }
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(PeriodTaskEntity.class, (Object value) -> {
                PeriodTaskEntity entity = (PeriodTaskEntity) value;

                boolean result = true;

                if (body.containsKey(PeriodTaskVOFieldConstant.field_task_name)) {
                    result = entity.getTaskName().contains((String) body.get(PeriodTaskVOFieldConstant.field_task_name));
                }
                if (body.containsKey(PeriodTaskVOFieldConstant.field_device_type)) {
                    result &= entity.getDeviceType().equals(body.get(PeriodTaskVOFieldConstant.field_device_type));
                }
                if (body.containsKey(PeriodTaskVOFieldConstant.field_manufacturer)) {
                    result &= entity.getManufacturer().equals(body.get(PeriodTaskVOFieldConstant.field_manufacturer));
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
    public AjaxResult queryEntity(@QueryParam("name") String name) {
        PeriodTaskEntity entity = new PeriodTaskEntity();
        entity.setTaskName(name);

        PeriodTaskEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), PeriodTaskEntity.class);
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
            String taskName = (String) params.get(PeriodTaskVOFieldConstant.field_task_name);
            String deviceType = (String) params.get(PeriodTaskVOFieldConstant.field_device_type);
            String manufacturer = (String) params.get(PeriodTaskVOFieldConstant.field_manufacturer);
            Boolean selectDevice = (Boolean) params.get(PeriodTaskVOFieldConstant.field_select_device);
            Map<String, Object> taskParam = (Map<String, Object>) params.get(PeriodTaskVOFieldConstant.field_task_param);
            List<Object> deviceIds = (List<Object>) params.get(PeriodTaskVOFieldConstant.field_device_ids);
            List<String> objectIds = (List<String>) params.get(PeriodTaskVOFieldConstant.field_object_ids);

            // 简单校验参数
            if (MethodUtils.hasNull(taskName, manufacturer, deviceType, taskParam, selectDevice)) {
                return AjaxResult.error("参数不能为空:taskName, manufacturer, deviceType, taskParam, selectDevice");
            }

            // 检查：deviceIds, objectName至少填写一个
            if (MethodUtils.hasNull(deviceIds) && MethodUtils.hasNull(objectIds)) {
                return AjaxResult.error("参数不能为空:deviceIds, objectIds，至少填写一个");
            }

            // 构造作为参数的实体
            PeriodTaskEntity entity = new PeriodTaskEntity();
            entity.setTaskName(taskName);
            entity.setDeviceType(deviceType);
            entity.setManufacturer(manufacturer);
            entity.setSelectDevice(selectDevice);
            entity.setTaskParam(taskParam);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }


            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
                PeriodTaskEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), PeriodTaskEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                // 新增场景：如果有用户输入，那么就用用户输入
                List<Object> deviceList = new ArrayList<>();
                if (deviceIds != null) {
                    for (Object devId : deviceIds) {
                        deviceList.add(Long.parseLong(devId.toString()));
                    }
                }
                List<String> objectList = new ArrayList<>();
                if (objectIds != null) {
                    objectList.addAll(objectIds);
                }
                entity.setDeviceIds(deviceList);
                entity.setObjectIds(objectList);

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                PeriodTaskEntity exist = this.entityManageService.getEntity(id, PeriodTaskEntity.class);
                if (exist == null) {
                    return AjaxResult.error("实体不存在");
                }

                // 修改数据
                entity.setId(id);

                // 修改场景：如果有用户输入，那么就用用户输入，否则就用原来的数据
                List<Object> deviceList = new ArrayList<>();
                if (deviceIds != null) {
                    for (Object devId : deviceIds) {
                        deviceList.add(Long.parseLong(devId.toString()));
                    }
                } else {
                    deviceList.addAll(exist.getDeviceIds());
                }
                List<String> objectList = new ArrayList<>();
                if (objectIds != null) {
                    objectList.addAll(objectIds);
                } else {
                    objectList.addAll(exist.getObjectIds());
                }
                entity.setDeviceIds(deviceList);
                entity.setObjectIds(objectList);


                this.entityManageService.updateEntity(entity);
                return AjaxResult.success();
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("entity")
    public AjaxResult deleteEntity(@QueryParam("id") Long id) {
        PeriodTaskEntity exist = this.entityManageService.getEntity(id, PeriodTaskEntity.class);
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

            this.entityManageService.deleteEntity(Long.parseLong(id), PeriodTaskEntity.class);
        }

        return AjaxResult.success();
    }
}
