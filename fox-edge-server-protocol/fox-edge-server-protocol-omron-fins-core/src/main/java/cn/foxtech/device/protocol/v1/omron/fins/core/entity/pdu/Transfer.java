/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class Transfer {
    /**
     * header
     */
    private Header header = new Header();

    /**
     * 主请求码:读数据0x01,0x01；写数据0x01,0x02
     */
    private int mrc = 0x01;
    /**
     * 次请求码:读数据0x01,0x01；写数据0x01,0x02
     */
    private int src = 0x01;

    /**
     * 数据
     */
    private byte[] data = new byte[0];
}
