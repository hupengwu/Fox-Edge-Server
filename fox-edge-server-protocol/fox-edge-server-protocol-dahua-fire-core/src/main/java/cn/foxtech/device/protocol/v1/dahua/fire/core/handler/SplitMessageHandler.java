/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.handler;

/**
 * 分包处理器：报头（40 40）+ xxxx（各字段）+报文长度（byte[24]，byte[25]），至少26字节
 */
public class SplitMessageHandler extends cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler {
    public SplitMessageHandler() {
        this.header = new int[26];
    }

    /**
     * 是否为非法报文：通过检查报文头部，这些协议中约定的起始标记，判定该报文是否为合法的报文
     *
     * @return 非法报文
     */
    @Override
    public boolean isInvalidPack() {
        // 报头是否为非法字符
        return this.header[0] != 0x40 || this.header[1] != 0x40;
    }

    /**
     * 从minPack数组中，取出报文长度信息
     *
     * @return
     */
    @Override
    public int getPackLength() {
        int cl = this.header[24] & 0xff;
        int ch = this.header[25] & 0xff;

        int length = cl + ch * 0x100;

        return 30 + length;
    }
}
