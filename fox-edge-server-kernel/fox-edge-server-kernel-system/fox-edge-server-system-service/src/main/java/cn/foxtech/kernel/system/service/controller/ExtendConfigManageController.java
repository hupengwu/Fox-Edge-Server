package cn.foxtech.kernel.system.service.controller;


import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.constant.ExtendVOFieldConstant;
import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

/**
 * 设备模板：它用于给其他设备提供快速复制的范本
 */
@RestController
@RequestMapping("/kernel/manager/extend")
public class ExtendConfigManageController {
    @Autowired
    private EntityManageService entityManageService;


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(ExtendConfigEntity.class);
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(ExtendConfigEntity.class, (Object value) -> {
                ExtendConfigEntity entity = (ExtendConfigEntity) value;

                boolean result = true;

                if (body.containsKey(ExtendVOFieldConstant.field_extend_name)) {
                    result = entity.getExtendName().contains((String) body.get(ExtendVOFieldConstant.field_extend_name));
                }
                if (body.containsKey(ExtendVOFieldConstant.field_extend_type)) {
                    result &= entity.getExtendType().equals(body.get(ExtendVOFieldConstant.field_extend_type));
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
        ExtendConfigEntity exist = this.entityManageService.getEntity(id, ExtendConfigEntity.class);
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
            String extendName = (String) params.get(ExtendVOFieldConstant.field_extend_name);
            String extendType = (String) params.get(ExtendVOFieldConstant.field_extend_type);
            Map<String, Object> extendParam = (Map<String, Object>) params.get(ExtendVOFieldConstant.field_extend_param);

            // 简单校验参数
            if (MethodUtils.hasNull(extendName, extendType, extendParam)) {
                throw new ServiceException("参数不能为空:extendName, extendType, extendParam");
            }

            // 验证格式的合法性
            ExtendParam extendParamEntity = JsonUtils.buildObjectWithoutException(extendParam, ExtendParam.class);
            if (extendParamEntity == null) {
                throw new ServiceException("extendParam的格式非法!");
            }


            if (extendType.equals(DeviceMapperEntity.class.getSimpleName() + "Type") // DeviceMapperEntity
                    || extendType.equals(DeviceEntity.class.getSimpleName() + "Type") // DeviceEntity
            ) {
                for (Object bind : extendParamEntity.getBinds()) {
                    if (!(bind instanceof Map) // Map
                            || !((Map<String, Object>) bind).containsKey(DeviceVOFieldConstant.field_manufacturer) //manufacturer
                            || !((Map<String, Object>) bind).containsKey(DeviceVOFieldConstant.field_device_type) //deviceType
                    ) {
                        throw new ServiceException("binds必须必须是一个包含manufacturer和deviceType的对象列表!");
                    }
                }
            }


            // 构造作为参数的实体
            ExtendConfigEntity entity = new ExtendConfigEntity();
            entity.setExtendName(extendName);
            entity.setExtendType(extendType);
            entity.setExtendParam(extendParamEntity);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                throw new ServiceException("具有null的service key！");
            }


            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
                ExtendConfigEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), ExtendConfigEntity.class);
                if (exist != null) {
                    throw new ServiceException("实体已存在");
                }

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                ExtendConfigEntity exist = this.entityManageService.getEntity(id, ExtendConfigEntity.class);
                if (exist == null) {
                    throw new ServiceException("实体不存在");
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
        ExtendConfigEntity exist = this.entityManageService.getEntity(id, ExtendConfigEntity.class);
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

            this.entityManageService.deleteEntity(Long.parseLong(id), ExtendConfigEntity.class);
        }

        return AjaxResult.success();
    }
}
