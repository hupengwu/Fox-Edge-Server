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

            this.scriptEngine.eval(script);
            Invocable invoke = (Invocable) scriptEngine;
            return (String) invoke.invokeFunction("decode", message);
        } catch (Exception e) {
            return "";
        }
    }
}
