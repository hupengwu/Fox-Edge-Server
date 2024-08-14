/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.omron.fins.core.entity.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ReadDataRespond extends Respond {
    /**
     * 发送：一次只能写2字节
     */
    private byte[] data = new byte[2];
}
