/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.device.protocol.v1.core.annotation;

import java.lang.annotation.*;

/**
 * 标识在解码类上，用于扫描器识别这是为一个自动加载的FoxEdge解码器类型
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FoxEdgeDeviceType {
    /**
     * 设备类型
     *
     * @return
     */
    String value() default "";

    /**
     * 设备版本
     *
     * @return
     */
    String version() default "";

    /**
     * 描述
     *
     * @return
     */
    String description() default "";

    /**
     * 制造商
     *
     * @return
     */
    String manufacturer() default "";
}
