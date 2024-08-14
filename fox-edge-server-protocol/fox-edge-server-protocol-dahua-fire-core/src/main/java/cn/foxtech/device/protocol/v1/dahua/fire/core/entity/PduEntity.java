/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.entity;

public abstract class PduEntity {
    /**
     * 获得流水号
     *
     * @return sn
     */
    public abstract int getSn();

    /**
     * 填写流水号
     *
     * @param sn sn
     */
    public abstract void setSn(int sn);
}
