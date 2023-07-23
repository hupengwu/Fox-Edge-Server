package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.constant.DeviceMapperVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceMapperEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.manager.system.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/device/mapper")
public class DeviceMapperManageController {
    @Autowired
    private EntityManageService entityManageService;


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceMapperEntity.class);
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceMapperEntity.class, (Object value) -> {
                DeviceMapperEntity entity = (DeviceMapperEntity) value;

                boolean result = true;

                if (body.containsKey(DeviceMapperVOFieldConstant.field_device_type)) {
                    result &= entity.getDeviceType().equals(body.get(DeviceMapperVOFieldConstant.field_device_type));
                }
                if (body.containsKey(DeviceMapperVOFieldConstant.field_object_name)) {
                    result &= entity.getObjectName().equals(body.get(DeviceMapperVOFieldConstant.field_object_name));
                }
                if (body.containsKey(DeviceMapperVOFieldConstant.field_mapper_name)) {
                    result &= entity.getMapperName().equals(body.get(DeviceMapperVOFieldConstant.field_mapper_name));
                }
                if (body.containsKey(DeviceMapperVOFieldConstant.field_mapper_mode)) {
                    result &= entity.getMapperMode().equals(body.get(DeviceMapperVOFieldConstant.field_mapper_mode));
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
        DeviceMapperEntity exist = this.entityManageService.getEntity(id, DeviceMapperEntity.class);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        return AjaxResult.success(exist);
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
            List<Map<String, Object>> list = (List<Map<String, Object>>) params.get("list");

            // 简单验证
            if (MethodUtils.hasEmpty(list)) {
                throw new ServiceException("参数不能为空:list");
            }

            for (Map<String, Object> item : list) {
                Integer id = (Integer) item.get(DeviceMapperVOFieldConstant.field_id);
                String mapperName = (String) item.get(DeviceMapperVOFieldConstant.field_mapper_name);
                Integer mapperMode = (Integer) item.get(DeviceMapperVOFieldConstant.field_mapper_mode);

                if (MethodUtils.hasEmpty(id, mapperName, mapperMode)) {
                    throw new ServiceException("参数不能为空:id, mapperName, mapperMode");
                }

                DeviceMapperEntity entity = this.entityManageService.getEntity(NumberUtils.makeLong(id), DeviceMapperEntity.class);
                if (entity == null) {
                    continue;
                }

                if (!mapperMode.equals(entity.getMapperMode()) || !mapperName.equals(entity.getMapperName())) {
                    entity.setMapperName(mapperName);
                    entity.setMapperMode(mapperMode);

                    this.entityManageService.updateEntity(entity);
                }
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public AjaxResult deleteDecoderPackageFile(@RequestBody Map<String, Object> body) {
        try {
            // 提取业务参数
            List<Object> list = (List<Object>) body.get("list");

            // 简单验证
            if (MethodUtils.hasEmpty(list)) {
                throw new ServiceException("参数不能为空:list");
            }

            for (Object id : list) {
                this.entityManageService.deleteEntity(NumberUtils.makeLong(id), DeviceMapperEntity.class);
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
