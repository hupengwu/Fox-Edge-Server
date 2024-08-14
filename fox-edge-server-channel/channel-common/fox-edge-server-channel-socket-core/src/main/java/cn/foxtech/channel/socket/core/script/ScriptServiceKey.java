/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.socket.core.script;

import cn.foxtech.channel.socket.core.utils.PackUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.script.Invocable;
import javax.script.ScriptEngine;

/**
 * 身份识别脚本
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ScriptServiceKey {
    /**
     * 执行引擎
     */
    private ScriptEngine scriptEngine;
    /**
     * 解码器脚本
     */
    private String script;

    /**
     * 数据格式：HEX或者TXT
     */
    private String format = "Hex";

    public String getServiceKey(byte[] pack) {
        try {
            String data = "";
            if (this.format.equals("TXT")) {
                data = PackUtil.byteArray2String(pack);
            } else {
                data = PackUtil.byteArrayToHexString(pack);
            }

            Invocable invoke = (Invocable) scriptEngine;
            String serviceKey = (String)invoke.invokeFunction("getServiceKey", data);
            return serviceKey;
        } catch (Exception e) {
            return "";
        }
    }

}
