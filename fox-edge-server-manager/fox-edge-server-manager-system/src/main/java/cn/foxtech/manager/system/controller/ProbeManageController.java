package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ProbeEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.manager.system.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/probe")
public class ProbeManageController {
    @Autowired
    private EntityManageService entityManageService;

    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = entityManageService.getProbeEntityList();
        return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
    }

    @PostMapping("entity")
    public AjaxResult insertEntity(@RequestBody Map<String, Object> params) {
        String deviceName = (String) params.get("deviceName");
        String deviceType = (String) params.get("deviceType");
        String manufacturer = (String) params.get("manufacturer");
        String operateName = (String) params.get("operateName");
        Map<String, Object> configParam = (Map<String, Object>) params.get("params");
        Map<String, Object> periodParam = (Map<String, Object>) params.get("period");

        // 简单校验参数
        if (MethodUtils.hasNull(deviceName, manufacturer, deviceType, operateName, configParam, periodParam)) {
            return AjaxResult.error("参数不能为空:deviceName, manufacturer, deviceType, operateName, params, period");
        }

        ProbeEntity entity = new ProbeEntity();
        entity.setDeviceName(deviceName);
        entity.setDeviceType(deviceType);
        entity.setManufacturer(manufacturer);
        entity.setOperateName(operateName);
        entity.setParams(configParam);
        entity.setPeriod(periodParam);

        if (entity.hasNullServiceKey()) {
            return AjaxResult.error("具有null的service key！");
        }

        if (configParam == null || periodParam == null) {
            return AjaxResult.error("params和period不能为空！");
        }

        entityManageService.insertEntity(entity);
        return AjaxResult.success();
    }

    @PutMapping("entity")
    public AjaxResult updateEntity(@RequestBody Map<String, Object> params) {
        Object id = params.get("id");
        String deviceName = (String) params.get("deviceName");
        String deviceType = (String) params.get("deviceType");
        String manufacturer = (String) params.get("manufacturer");
        String operateName = (String) params.get("operateName");
        Map<String, Object> configParam = (Map<String, Object>) params.get("params");
        Map<String, Object> periodParam = (Map<String, Object>) params.get("period");

        // 简单校验参数
        if (MethodUtils.hasNull(id, deviceName, manufacturer, deviceType, operateName, configParam, periodParam)) {
            return AjaxResult.error("参数不能为空:id, deviceName, manufacturer, deviceType, operateName, params, period");
        }

        ProbeEntity entity = new ProbeEntity();
        entity.setDeviceName(deviceName);
        entity.setManufacturer(manufacturer);
        entity.setDeviceType(deviceType);
        entity.setOperateName(operateName);
        entity.setParams(configParam);
        entity.setPeriod(periodParam);

        ProbeEntity exist = entityManageService.getProbeEntity(Long.valueOf(id.toString()));
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        entityManageService.updateEntity(entity);
        return AjaxResult.success();
    }

    @DeleteMapping("entity")
    public AjaxResult deleteEntity(@QueryParam("id") Long id) {
        ProbeEntity exist = entityManageService.getProbeEntity(id);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        entityManageService.deleteEntity(exist);
        return AjaxResult.success();
    }
}
