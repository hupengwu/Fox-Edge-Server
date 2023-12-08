package cn.foxtech.channel.serialport.script;

import cn.foxtech.common.utils.hex.HexUtils;
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

    public String decode(byte[] buff) {
        try {
            String message = "";
            if (format.equals("TXT")) {
                message = new String(buff, "GB2312");
            } else {
                message = HexUtils.byteArrayToHexString(buff);
            }

            this.scriptEngine.eval(script);
            Invocable invoke = (Invocable) scriptEngine;
            return (String) invoke.invokeFunction("decode", message);
        } catch (Exception e) {
            return "";
        }
    }

}
