/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.service.controller;


import cn.foxtech.common.entity.constant.ConfigVOFieldConstant;
import cn.foxtech.common.entity.constant.DeviceRecordVOFieldConstant;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.exchange.RedisExchangeService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exchange")
public class ExchangeManageController {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RedisExchangeService redisExchangeService;


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
            List<Map<String, Object>> entityList = this.redisExchangeService.readConfigValues();

            // 获得分页数据
            if (isPage) {
                return PageUtils.getPageMapList(entityList, body);
            } else {
                body.put(DeviceRecordVOFieldConstant.field_page_num, 1);
                body.put(DeviceRecordVOFieldConstant.field_page_size, 1000);
                return PageUtils.getPageMapList(entityList, body);
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("entity")
    public AjaxResult insertEntity(@RequestBody Map<String, Object> params) {
        params.remove("id");
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
            Map<String, Object> configValue = (Map<String, Object>) params.get(ConfigVOFieldConstant.field_config_value);

            // 简单校验参数
            if (MethodUtils.hasNull(configName, serviceName, serviceType, configValue)) {
                return AjaxResult.error("参数不能为空:configName, serviceName, serviceType, configValue");
            }

            this.redisExchangeService.saveConfigValue(serviceType, serviceName, configName, configValue);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    @PostMapping("entity/delete")
    public AjaxResult deleteEntity(@RequestBody Map<String, Object> params) {
        // 提取业务参数
        String configName = (String) params.get(ConfigVOFieldConstant.field_config_name);
        String serviceType = (String) params.get(ConfigVOFieldConstant.field_service_type);
        String serviceName = (String) params.get(ConfigVOFieldConstant.field_service_name);

        // 简单校验参数
        if (MethodUtils.hasNull(configName, serviceName, serviceType)) {
            return AjaxResult.error("参数不能为空:configName, serviceName, serviceType");
        }

        this.redisExchangeService.removeConfigValue(serviceType, serviceName, configName);

        return AjaxResult.success();
    }
}
