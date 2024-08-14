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
 
package cn.foxtech.channel.serialport.script;

import cn.foxtech.common.utils.hex.HexUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.script.Invocable;
import javax.script.ScriptEngine;

/**
 * 报文拆包脚本
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ScriptSplitMessage {
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

    public String decode(byte[] buff, int end) {
        try {
            String message = "";
            if (format.equals("TXT")) {
                message = new String(buff, 0, end, "GB2312");
            } else {
                message = HexUtils.byteArrayToHexString(buff, 0, end, false);
            }

            Invocable invoke = (Invocable) scriptEngine;
            return (String) invoke.invokeFunction("decode", message);
        } catch (Exception e) {
            return "";
        }
    }
}
