package cn.foxtech.channel.common.javascript;

import cn.foxtech.common.entity.entity.OperateEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.script.ScriptEngine;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ScriptServiceKey extends OperateEntity {
    /**
     * 执行引擎
     */
    private ScriptEngine scriptEngine;
    /**
     * 解码器脚本
     */
    private String script;

    public String getServiceKey(byte[] pack) {
        try {
            String hex = byteArrayToHexString(pack);
            String serviceKey = (String) scriptEngine.eval("getServiceKey(" + hex + ")");
            return serviceKey;
        } catch (Exception e) {
            return "null";
        }
    }

    public String byteArrayToHexString(byte[] byteArray) {
        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10) {
                // 0~F前面不零
                hexString.append("0");
            }

            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString();
    }
}
