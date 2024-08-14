/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.zktl.air5in1.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZktlConfigEntity extends ZktlDataEntity{
    private int value = 0;
    public String getServiceKey() {
        return "air5in1=" + super.getCommunTypeName() + ":" + super.getDeviceTypeName() + ":" + this.value;
    }
}
