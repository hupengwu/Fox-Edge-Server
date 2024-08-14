/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.common.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.stereotype.Component;

@Getter(value = AccessLevel.PUBLIC)
@Component
public class ChannelProperties {
    @Autowired
    private AbstractEnvironment environment;

    /**
     * 必填参数
     */
    @Value("${spring.fox-service.model.name}")
    private String channelType;

    /**
     * 是否打印收/发日志
     * 该参数只是临时变量，它的填写由各个channel服务在Initialize阶段，自己填写，自己在后续阶段使用。
     */
    @Setter
    private boolean logger = false;

    /**
     * 可选参数
     */
    private String initMode;


    public void initialize() {
        this.initMode = this.environment.getProperty("spring.channel.init-mode", String.class, "redis");
    }
}
