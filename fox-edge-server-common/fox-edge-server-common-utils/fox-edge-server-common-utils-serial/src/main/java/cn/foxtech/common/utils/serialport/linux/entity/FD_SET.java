/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.serialport.linux.entity;

import com.sun.jna.Structure;

@Structure.FieldOrder({"fds_bits"})
public class FD_SET extends Structure {
    public long[] fds_bits = new long[16];
    public FD_SET() {
    }
}
