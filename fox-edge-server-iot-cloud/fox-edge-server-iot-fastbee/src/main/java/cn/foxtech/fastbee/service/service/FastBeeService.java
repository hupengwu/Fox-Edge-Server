/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.fastbee.service.service;

import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.utils.number.NumberUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FastBeeService {
    @Autowired
    private InitialConfigService configService;

    @Getter
    private int timeInterval;
    @Getter
    private String timeUnit;

    @Getter
    private String productId;
    @Getter
    private String deviceNum;

    @Getter
    private String iotName;

    @Getter
    private String subsetName;


    public void initialize() {
        Map<String, Object> configValue = this.configService.getConfigParam("serverConfig");
        // 取出全局配置参数：扩展字段的名称
        Map<String, Object> params = (Map<String, Object>) configValue.getOrDefault("fast-bee", new HashMap<>());
        this.iotName = (String) params.getOrDefault("iotName", "FastBee");
        this.subsetName = (String) params.getOrDefault("subsetName", "default");
        this.productId = (String) params.getOrDefault("productId", "productId");
        this.productId = (String) params.getOrDefault("productId", "productId");
        this.deviceNum = (String) params.getOrDefault("deviceNum", "deviceNum");
        this.timeUnit = (String) params.getOrDefault("timeUnit", "minute");
        this.timeInterval = NumberUtils.makeInteger(params.getOrDefault("timeInterval", 300));
    }
}
