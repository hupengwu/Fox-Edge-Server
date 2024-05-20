package cn.foxtech.kernel.system.service.controller;


import cn.foxtech.common.entity.constant.DeviceValueExTaskVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceValueExTaskEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

/**
 * operate的数据是由deviceService服务启动阶段，自动扫描第三方jar生成的，不需要手动维护
 */
@RestController
@RequestMapping("/kernel/manager/device/value/ex/task")
public class DeviceValueExTaskController {
    @Autowired
    private EntityManageService entityManageService;


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceValueExTaskEntity.class);
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceValueExTaskEntity.class, (Object value) -> {
                DeviceValueExTaskEntity entity = (DeviceValueExTaskEntity) value;

                boolean result = true;

                if (body.containsKey("keyword")) {
                    result &= entity.getTaskName().indexOf((String) body.get("keyword")) != -1;
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
        DeviceValueExTaskEntity exist = this.entityManageService.getEntity(id, DeviceValueExTaskEntity.class);
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
            String taskName = (String) params.get(DeviceValueExTaskVOFieldConstant.field_task_name);
            Map<String, Object> taskParam = (Map<String, Object>) params.get(DeviceValueExTaskVOFieldConstant.field_task_param);

            // 简单校验参数
            if (MethodUtils.hasNull(taskName, taskParam)) {
                return AjaxResult.error("参数不能为空: taskName, taskParam ");
            }

            // 构造作为参数的实体
            DeviceValueExTaskEntity entity = new DeviceValueExTaskEntity();
            entity.setTaskName(taskName);
            entity.setTaskParam(taskParam);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }

            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (params.get("id") == null) {
                DeviceValueExTaskEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), DeviceValueExTaskEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                DeviceValueExTaskEntity exist = this.entityManageService.getEntity(id, DeviceValueExTaskEntity.class);
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


    @PutMapping("entity/script")
    public AjaxResult updateScript(@RequestBody Map<String, Object> params) {
        try {
            // 提取业务参数
            Long id = Long.parseLong(params.get("id").toString());
            String script = (String) params.get(DeviceValueExTaskVOFieldConstant.field_script);

            // 简单校验参数
            if (MethodUtils.hasEmpty(id, script)) {
                return AjaxResult.error("参数不能为空: id, script ");
            }

            DeviceValueExTaskEntity exist = this.entityManageService.getEntity(id, DeviceValueExTaskEntity.class);
            if (exist == null) {
                return AjaxResult.error("实体不存在");
            }

            DeviceValueExTaskEntity clone = JsonUtils.clone(exist);
            clone.updateScript(script);

            // 修改数据
            this.entityManageService.updateEntity(clone);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("entity")
    public AjaxResult deleteEntity(@QueryParam("id") Long id) {
        DeviceValueExTaskEntity exist = this.entityManageService.getEntity(id, DeviceValueExTaskEntity.class);
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

            this.entityManageService.deleteEntity(Long.parseLong(id), DeviceValueExTaskEntity.class);
        }

        return AjaxResult.success();
    }
}
