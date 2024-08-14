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
 
package cn.foxtech.device.protocol.v1.core.method;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 指明函数是否为编码/解码函数，通信超时需要多少，是否被轮询操作进行循环调用
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class FoxEdgeExchangeMethod extends FoxEdgeBaseMethod {
    /**
     * 最大通信超时：写在解码器上
     */
    private Integer timeout = 1000;

    /**
     * 该操作是否需要被轮询调度
     */
    private boolean polling = false;

    /**
     * 操作顺序
     */
    private int order = 0;

    /**
     * 返回的数据类型：状态模式，还是记录模式
     */
    private String mode = FoxEdgeOperate.status;


    /**
     * 编码函数
     */
    private Method encoderMethod;

    /**
     * 解码函数
     */
    private Method decoderMethod;

    /**
     * 参数表
     */
    private Map<String, String> encoderParams = new HashMap<>();

    /**
     * 参数表
     */
    private Map<String, String> decoderParams = new HashMap<>();
}
