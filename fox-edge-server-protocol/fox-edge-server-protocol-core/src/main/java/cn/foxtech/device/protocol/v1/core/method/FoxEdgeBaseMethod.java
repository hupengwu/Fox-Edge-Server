/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.core.method;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 指明函数是否为编码/解码函数，通信超时需要多少，是否被轮询操作进行循环调用
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class FoxEdgeBaseMethod {
    /**
     * 制造厂商
     */
    private String manufacturer = "";
    /**
     * 设备类型
     */
    private String deviceType = "";

    /**
     * 操作名称
     */
    private String name = "";
}
