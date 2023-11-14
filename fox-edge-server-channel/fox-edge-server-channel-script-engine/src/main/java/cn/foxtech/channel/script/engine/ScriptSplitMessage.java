package cn.foxtech.channel.common.javascript;

import cn.foxtech.common.entity.entity.OperateEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.script.ScriptEngine;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ScriptSplitMessage extends OperateEntity {
    /**
     * 执行引擎
     */
    private ScriptEngine scriptEngine;
    /**
     * 解码器脚本
     */
    private String script;
    /**
     * 数据格式：Hex或者String
     */
    private String format = "Hex";

    public Integer getHeaderLength() {
        try {
            return (Integer) this.scriptEngine.eval("getHeaderLength()");
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer getPackLength(int[] pack) {
        try {
            String data = "";
            if (this.format.equals("Hex")) {
                data = byteArrayToHexString(pack);
            }
            if (this.format.equals("String")) {
                data = byteArray2String(pack);
            }

            Object value = scriptEngine.eval("getPackLength('" + data + "')");
            if (value instanceof Double) {
                double dv = (Double) value;
                return (int) dv;
            }
            if (value instanceof Float) {
                double dv = (Float) value;
                return (int) dv;
            }
            if (value instanceof Integer) {
                return (Integer) value;
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public Boolean isInvalidPack(int[] pack) {
        try {
            String data = "";
            if (this.format.equals("Hex")) {
                data = byteArrayToHexString(pack);
            }
            if (this.format.equals("String")) {
                data = byteArray2String(pack);
            }
            return (Boolean) scriptEngine.eval("isInvalidPack('" + data + "')");
        } catch (Exception e) {
            return true;
        }
    }

    private String byteArray2String(int[] pack) {
        StringBuilder sb = new StringBuilder();
        for (int at : pack) {
            sb.append((char) at);
        }
        return sb.toString();
    }

    public String byteArrayToHexString(int[] byteArray) {
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
