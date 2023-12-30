package cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.ParFmt;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.ParType;
import cn.foxtech.device.protocol.v1.utils.BcdUtils;
import cn.foxtech.device.protocol.v1.utils.ByteUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 不定长参数
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ParVarObject {
    /**
     * 参数类型（1 字节）
     */
    private ParType type = ParType.host;

    /**
     * 参数数值（N 字节）：数据类型未知，根据type来确定
     */
    private String value = "";

    /**
     * 编码长度
     *
     * @return 编码长度
     */
    public int getSize() {
        // 截断数据
        String txt = this.value.substring(0, Math.min(this.value.length(), 250));

        if (this.type.getFmt().equals(ParFmt.ascii)) {
            return txt.length();
        }
        if (this.type.getFmt().equals(ParFmt.hex)) {
            String hex = txt.replaceAll(" ", "");
            return hex.length() / 2;
        }
        if (this.type.getFmt().equals(ParFmt.bcd)) {
            String bcd = txt.replaceAll(" ", "");
            return bcd.length() / 2;
        }

        return 0;
    }

    public int encode(byte[] data, int offset) {
        // 截断数据
        String txt = this.value.substring(0, Math.min(this.value.length(), 250));

        // 参数类型（1 字节）
        data[offset + 0] = (byte) this.type.getType();


        if (this.type.getFmt().equals(ParFmt.ascii)) {
            // 参数长度（1 字节）
            data[offset + 1] = (byte) txt.length();

            // 参数数值（N 字节）
            ByteUtils.encodeAscii(txt, data, offset + 2, txt.length(), true);
            return data[offset + 1] & 0xff;
        }
        if (this.type.getFmt().equals(ParFmt.hex)) {
            String hex = txt.replaceAll(" ", "");

            // 参数长度（1 字节）
            data[offset + 1] = (byte) (hex.length() / 2);

            // 参数数值（N 字节）
            HexUtils.hexStringToByteArray(hex, data, offset + 2);
            return data[offset + 1] & 0xff;
        }
        if (this.type.getFmt().equals(ParFmt.bcd)) {
            String bcd = txt.replaceAll(" ", "");

            // 参数长度（1 字节）
            data[offset + 1] = (byte) (bcd.length() / 2);

            BcdUtils.str2bcd(bcd, data, offset + 2, true);
            return data[offset + 1] & 0xff;
        }

        return data[offset + 1] & 0xff;
    }

    public void decode(byte[] data, int offset) {
        // 参数类型（1 字节）
        this.type = ParType.getEnum(data[offset + 0]);
        if (this.type == null) {
            throw new ProtocolException("未定义的参数类型：" + data[offset + 0]);
        }

        // 参数长度（1 字节）
        int length = data[offset + 1];

        if (this.type.getFmt().equals(ParFmt.ascii)) {
            // 参数数值（N 字节）
            this.value = ByteUtils.decodeAscii(data, offset + 2, length, true);
            return;
        }
        if (this.type.getFmt().equals(ParFmt.hex)) {
            // 参数数值（N 字节）
            this.value = HexUtils.byteArrayToHexString(data, offset + 2, length, true);
            return;
        }
        if (this.type.getFmt().equals(ParFmt.bcd)) {
            this.value = BcdUtils.bcd2str(data, offset + 2, length, true);
            return;
        }
    }
}
