/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.service.controller;

import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceSequenceEntity;
import cn.foxtech.common.entity.service.devicesequence.DeviceSequenceEntityService;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/device/sequence/record")
public class DeviceSequenceEntityManageController {
    @Autowired
    private DeviceSequenceEntityService deviceSequenceEntityService;

    @Autowired
    private EntityManageService entityManageService;

    @PostMapping("page")
    public AjaxResult selectEntityListByPage(@RequestBody Map<String, Object> body) {
        List<DeviceSequenceEntity> entityList = this.deviceSequenceEntityService.queryRecords(body);
        List<Map<String, Object>> mapList = EntityVOBuilder.buildVOList(entityList);

        this.extend(mapList);

        Map<String, Object> data = new HashMap<>();
        data.put("list", mapList);
        data.put("total", 0);

        return AjaxResult.success(data);

    }

    private void extend(List<Map<String, Object>> list) {
        for (Map<String, Object> row : list) {
            DeviceEntity deviceEntity = this.entityManageService.getDeviceEntity((String) row.getOrDefault("deviceName", ""));
            if (deviceEntity != null) {
                row.put("manufacturer", deviceEntity.getManufacturer());
                row.put("deviceType", deviceEntity.getDeviceType());
            }
        }
    }
}
