package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ParamTemplateEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.manager.system.service.EntityManageService;
import cn.foxtech.common.entity.constant.DeviceTemplateVOFieldConstant;
import cn.foxtech.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

/**
 * 设备模板：它用于给其他设备提供快速复制的范本
 */
@RestController
@RequestMapping("/kernel/manager/param/template")
public class ParamTemplateManageController {
    @Autowired
    private EntityManageService entityManageService;


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(ParamTemplateEntity.class);
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(ParamTemplateEntity.class, (Object value) -> {
                ParamTemplateEntity entity = (ParamTemplateEntity) value;

                boolean result = true;

                if (body.containsKey(DeviceTemplateVOFieldConstant.field_template_name)) {
                    result = entity.getTemplateName().contains((String) body.get(DeviceTemplateVOFieldConstant.field_template_name));
                }
                if (body.containsKey(DeviceTemplateVOFieldConstant.field_template_type)) {
                    result &= entity.getTemplateType().equals(body.get(DeviceTemplateVOFieldConstant.field_template_type));
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
        ParamTemplateEntity exist = this.entityManageService.getEntity(id, ParamTemplateEntity.class);
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
            String templateName = (String) params.get(DeviceTemplateVOFieldConstant.field_template_name);
            String templateType = (String) params.get(DeviceTemplateVOFieldConstant.field_template_type);
            Object templateParam = params.get(DeviceTemplateVOFieldConstant.field_template_param);

            // 简单校验参数
            if (MethodUtils.hasNull(templateName, templateType, templateParam)) {
                return AjaxResult.error("参数不能为空:templateName, templateType, templateParam");
            }

            // 构造作为参数的实体
            ParamTemplateEntity entity = new ParamTemplateEntity();
            entity.setTemplateName(templateName);
            entity.setTemplateType(templateType);
            entity.setTemplateParam(templateParam);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }


            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
                ParamTemplateEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), ParamTemplateEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                ParamTemplateEntity exist = this.entityManageService.getEntity(id, ParamTemplateEntity.class);
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
        ParamTemplateEntity exist = this.entityManageService.getEntity(id, ParamTemplateEntity.class);
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

            this.entityManageService.deleteEntity(Long.parseLong(id), ParamTemplateEntity.class);
        }

        return AjaxResult.success();
    }
}
