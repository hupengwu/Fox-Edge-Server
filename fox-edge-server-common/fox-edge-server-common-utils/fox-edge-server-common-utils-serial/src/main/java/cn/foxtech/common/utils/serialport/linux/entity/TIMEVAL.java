/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.serialport.linux.entity;

import com.sun.jna.Structure;

@Structure.FieldOrder({"tv_sec", "tv_usec"})
public class TIMEVAL extends Structure {
    public long tv_sec = 0;
    public long tv_usec = 0;
    public TIMEVAL() {
    }
}
