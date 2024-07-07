package cn.foxtech.kernel.system.service.controller;


import cn.foxtech.common.entity.constant.ConfigVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.service.config.ConfigVOMaker;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigManageController {
    @Autowired
    private EntityManageService entityManageService;

    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(ConfigEntity.class);
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(ConfigEntity.class, (Object value) -> {
                ConfigEntity entity = (ConfigEntity) value;

                boolean result = true;


                if (body.containsKey(ConfigVOFieldConstant.field_config_name)) {
                    result &= entity.getConfigName().contains((String) body.get(ConfigVOFieldConstant.field_config_name));
                }
                if (body.containsKey(ConfigVOFieldConstant.field_service_name)) {
                    result &= entity.getServiceName().contains((String) body.get(ConfigVOFieldConstant.field_service_name));
                }

                return result;
            });

            // 对返回的内容进行后期处理：主要是涉及密码的显示
            entityList = ConfigVOMaker.build().postProcess(entityList);

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
        ConfigEntity exist = this.entityManageService.getEntity(id, ConfigEntity.class);
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
            String configName = (String) params.get(ConfigVOFieldConstant.field_config_name);
            String serviceType = (String) params.get(ConfigVOFieldConstant.field_service_type);
            String serviceName = (String) params.get(ConfigVOFieldConstant.field_service_name);
            String remark = (String) params.get(ConfigVOFieldConstant.field_remark);
            Map<String, Object> configValue = (Map<String, Object>) params.get(ConfigVOFieldConstant.field_config_value);

            // 简单校验参数
            if (MethodUtils.hasNull(configName, serviceName, serviceType, configValue)) {
                return AjaxResult.error("参数不能为空:configName, serviceName, serviceType, configValue");
            }

            // 构造作为参数的实体
            ConfigEntity entity = new ConfigEntity();
            entity.setServiceName(serviceName);
            entity.setServiceType(serviceType);
            entity.setConfigName(configName);
            entity.setRemark(remark);
            entity.setConfigValue(configValue);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }


            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (params.get("id") == null) {
                ConfigEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), ConfigEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                ConfigEntity exist = this.entityManageService.getEntity(id, ConfigEntity.class);
                if (exist == null) {
                    return AjaxResult.error("实体不存在");
                }

                if (Boolean.TRUE.equals(exist.getConfigParam().get("readOnly"))) {
                    return AjaxResult.error("只读配置，不允许修改");
                }

                // configParam参数是通过脚本来刷新的，不是界面配置的
                entity.setConfigParam(exist.getConfigParam());

                // 进行加密预处理
                ConfigVOMaker maker = ConfigVOMaker.build();
                maker.preProcess(entity, exist);

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
        ConfigEntity exist = this.entityManageService.getEntity(id, ConfigEntity.class);
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

            this.entityManageService.deleteEntity(Long.parseLong(id), ConfigEntity.class);
        }

        return AjaxResult.success();
    }
}
