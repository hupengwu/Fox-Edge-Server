package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.constant.Constants;
import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.manager.system.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.*;

/**
 * operate的数据是由deviceService服务启动阶段，自动扫描第三方jar生成的，不需要手动维护
 */
@RestController
@RequestMapping("/kernel/manager/device/operate")
public class OperateManageController {
    @Autowired
    private EntityManageService entityManageService;


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(OperateEntity.class);
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

    @PostMapping("option")
    public AjaxResult selectOptionList(@RequestBody Map<String, Object> body) {
        String deviceType = (String) body.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) body.get(OperateVOFieldConstant.field_manufacturer);
        String operateName = (String) body.get(OperateVOFieldConstant.field_operate_name);
        if (MethodUtils.hasEmpty(manufacturer)) {
            throw new ServiceException("参数缺失：deviceType, manufacturer");
        }

        Set<String> operateModes = new HashSet<>();
        operateModes.add(Constants.OPERATE_MODE_PUBLISH);
        operateModes.add(Constants.OPERATE_MODE_EXCHANGE);

        Map<String, Object> param = new HashMap<>();
        param.put(OperateVOFieldConstant.field_manufacturer, manufacturer);
        param.put(OperateVOFieldConstant.field_operate_modes, operateModes);
        if (!MethodUtils.hasEmpty(deviceType)) {
            param.put(OperateVOFieldConstant.field_device_type, deviceType);
        }
        if (!MethodUtils.hasEmpty(operateName)) {
            param.put(OperateVOFieldConstant.field_operate_name, operateName);
        }

        // 转换为option格式
        AjaxResult ajxResult = this.selectEntityList(param, false);
        List<Map<String, Object>> data = (List<Map<String, Object>>) ajxResult.get("data");
        if (!MethodUtils.hasEmpty(data)) {

            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Map<String, Object> map : data) {
                Map<String, Object> result = new HashMap<>();

                if (deviceType != null && operateName == null) {
                    result.put("value", map.get(OperateVOFieldConstant.field_operate_name));
                    result.put("label", map.get(OperateVOFieldConstant.field_operate_name));
                }
                if (deviceType != null && operateName != null) {
                    result.put("value", map.get(OperateVOFieldConstant.field_operate_mode));
                    result.put("label", map.get(OperateVOFieldConstant.field_operate_mode));
                }

                resultList.add(result);
            }

            ajxResult.put("data", resultList);
        }

        return ajxResult;
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
            List<BaseEntity> entityList = this.entityManageService.getEntityList(OperateEntity.class, (Object value) -> {
                OperateEntity entity = (OperateEntity) value;

                boolean result = true;

                if (body.containsKey(OperateVOFieldConstant.field_device_type)) {
                    result = entity.getDeviceType().contains((String) body.get(OperateVOFieldConstant.field_device_type));
                }
                if (body.containsKey(OperateVOFieldConstant.field_manufacturer)) {
                    result &= entity.getManufacturer().equals(body.get(OperateVOFieldConstant.field_manufacturer));
                }
                if (body.containsKey(OperateVOFieldConstant.field_operate_name)) {
                    result &= entity.getOperateName().equals(body.get(OperateVOFieldConstant.field_operate_name));
                }
                if (body.containsKey(OperateVOFieldConstant.field_operate_mode)) {
                    result &= entity.getOperateMode().equals(body.get(OperateVOFieldConstant.field_operate_mode));
                }
                if (body.containsKey(OperateVOFieldConstant.field_operate_modes)) {
                    Set<String> operateModes = (Set<String>) body.get(OperateVOFieldConstant.field_operate_modes);
                    result &= operateModes.contains(entity.getOperateMode());
                }
                if (body.containsKey(OperateVOFieldConstant.field_data_type)) {
                    result &= entity.getDataType().equals(body.get(OperateVOFieldConstant.field_data_type));
                }
                if (body.containsKey(OperateVOFieldConstant.field_engine_type)) {
                    result &= entity.getEngineType().equals(body.get(OperateVOFieldConstant.field_engine_type));
                }
                if (body.containsKey(OperateVOFieldConstant.field_polling)) {
                    result &= entity.getPolling().equals(body.get(OperateVOFieldConstant.field_polling));
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
        OperateEntity exist = this.entityManageService.getEntity(id, OperateEntity.class);
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
            String deviceType = (String) params.get(OperateVOFieldConstant.field_device_type);
            String manufacturer = (String) params.get(OperateVOFieldConstant.field_manufacturer);
            String operateName = (String) params.get(OperateVOFieldConstant.field_operate_name);
            String operateMode = (String) params.get(OperateVOFieldConstant.field_operate_mode);
            String dataType = (String) params.get(OperateVOFieldConstant.field_data_type);
            String engineType = (String) params.get(OperateVOFieldConstant.field_engine_type);
            Boolean polling = (Boolean) params.get(OperateVOFieldConstant.field_polling);
            Integer timeout = (Integer) params.get(OperateVOFieldConstant.field_timeout);
            Map<String, Object> engineParam = (Map<String, Object>) params.get(OperateVOFieldConstant.field_engine_param);


            // 简单校验参数
            if (MethodUtils.hasEmpty(deviceType, operateName, operateMode, manufacturer, dataType, engineType, polling, timeout)) {
                return AjaxResult.error("参数不能为空: deviceType, operateName, operateMode, manufacturer, dataType, engineType, polling, timeout");
            }
            if (MethodUtils.hasNull(engineParam)) {
                return AjaxResult.error("参数不能为空: engineParam");
            }

            // 构造作为参数的实体
            OperateEntity entity = new OperateEntity();
            entity.setDeviceType(deviceType);
            entity.setManufacturer(manufacturer);
            entity.setEngineType(engineType);
            entity.setEngineParam(engineParam);
            entity.setOperateName(operateName);
            entity.setOperateMode(operateMode);
            entity.setDataType(dataType);
            entity.setPolling(polling);
            entity.setTimeout(timeout);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }


            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
                OperateEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), OperateEntity.class);
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                this.entityManageService.insertEntity(entity);
                return AjaxResult.success();
            } else {
                Long id = Long.parseLong(params.get("id").toString());
                OperateEntity exist = this.entityManageService.getEntity(id, OperateEntity.class);
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
        OperateEntity exist = this.entityManageService.getEntity(id, OperateEntity.class);
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

            this.entityManageService.deleteEntity(Long.parseLong(id), OperateEntity.class);
        }

        return AjaxResult.success();
    }
}
