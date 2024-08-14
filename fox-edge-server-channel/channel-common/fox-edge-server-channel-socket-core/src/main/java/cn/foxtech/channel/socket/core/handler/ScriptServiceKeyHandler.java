/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.socket.core.handler;

import cn.foxtech.channel.socket.core.script.ScriptServiceKey;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Script版本的身份识别Handler
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ScriptServiceKeyHandler extends ServiceKeyHandler {
    private ScriptServiceKey scriptServiceKey;

    @Override
    public String getServiceKey(byte[] pdu) {
        return this.scriptServiceKey.getServiceKey(pdu);
    }

}
