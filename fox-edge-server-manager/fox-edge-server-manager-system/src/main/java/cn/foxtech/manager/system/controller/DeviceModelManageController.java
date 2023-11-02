package cn.foxtech.manager.system.controller;

import cn.foxtech.common.entity.constant.DeviceModelVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceModelEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.manager.system.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

/**
 * 数据的来源：link数据是由link服务启动阶段，根据自己的配置文件保存在内存中的。
 * 数据库记录的生成：它们的数据由system进程向各link服务进程收集，并保存到数据库中。
 * 数据库记录的消费：device服务进程和调度服务进程，从数据库中读取并消费
 */
@RestController
@RequestMapping("/kernel/manager/device/model")
public class DeviceModelManageController {
    @Autowired
    private EntityManageService entityManageService;


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceModelEntity.class);
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceModelEntity.class, (Object value) -> {
                DeviceModelEntity entity = (DeviceModelEntity) value;

                boolean result = true;

                if (body.containsKey(DeviceModelVOFieldConstant.field_model_name)) {
                    result = entity.getModelName().contains((String) body.get(DeviceModelVOFieldConstant.field_model_name));
                }
                if (body.containsKey(DeviceModelVOFieldConstant.field_model_type)) {
                    result &= entity.getModelType().equals(body.get(DeviceModelVOFieldConstant.field_model_type));
                }
                if (body.containsKey(DeviceModelVOFieldConstant.field_provider)) {
                    result &= entity.getProvider().equals(body.get(DeviceModelVOFieldConstant.field_provider));
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
        if (id == null) {
            return AjaxResult.error("输入的id为null!");
        }
        DeviceModelEntity exist = this.entityManageService.getEntity(id, DeviceModelEntity.class);
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
            String modelType = (String) params.get(DeviceModelVOFieldConstant.field_model_type);
            String modelName = (String) params.get(DeviceModelVOFieldConstant.field_model_name);
            String provider = (String) params.get(DeviceModelVOFieldConstant.field_provider);
            Map<String, Object> serviceParam = (Map<String, Object>) params.get(DeviceModelVOFieldConstant.field_service_param);
            Map<String, Object> modelSchema = (Map<String, Object>) params.get(DeviceModelVOFieldConstant.field_model_schema);

            // 简单校验参数
            if (MethodUtils.hasNull(modelType, modelName, provider, serviceParam, modelSchema)) {
                return AjaxResult.error("参数不能为空: modelType, modelName, provider, serviceParam, modelSchema ");
            }

            // 构造作为参数的实体
            DeviceModelEntity entity = new DeviceModelEntity();
            entity.setModelName(modelName);
            entity.setModelType(modelType);
            entity.setProvider(provider);
            entity.setServiceParam(serviceParam);
            entity.setModelSchema(modelSchema);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }


            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
                DeviceModelEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), DeviceModelEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                DeviceModelEntity exist = this.entityManageService.getEntity(id, DeviceModelEntity.class);
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
        DeviceModelEntity exist = this.entityManageService.getEntity(id, DeviceModelEntity.class);
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

            this.entityManageService.deleteEntity(Long.parseLong(id), DeviceModelEntity.class);
        }

        return AjaxResult.success();
    }
}
