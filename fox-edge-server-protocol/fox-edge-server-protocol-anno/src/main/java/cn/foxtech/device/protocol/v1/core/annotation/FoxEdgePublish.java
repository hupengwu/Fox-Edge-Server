/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.core.annotation;

import java.lang.annotation.*;

/**
 * 发布注解：某些设备支持接收广播通知，它们并不返回数据
 * 它需要依赖FoxEdgeOperate注解
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FoxEdgePublish {
}
