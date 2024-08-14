/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.IntegerUtil;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * TLV结构（Tag Length Value）
 * 备注：浙江大华的TLV是允许嵌套的，例如TLV1（TLV1.1、TLV2.2（TVL2.2.1））,TLV2
 * 他们通过TLV结构的嵌套，变相实现了一个树形结构。
 * 当最后一个层级不再符合根据TLV的L长度进行判定的时候，说明这个是叶子节点
 * 。
 * 这边只实现树的第一层的拆解，后面应用层自己进一步去拆解数据。
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class TlvObject {
    /**
     * 数据类型（2 字节）
     */
    private int type = 0;

    /**
     * 参数数值（2字节长度 +N字节数值）：HEX文本格式的数组，里面的内容，应用层去进行二次拆解
     */
    private String value = "";

    public static List<TlvObject> decodeTlvList(byte[] data, int offset, int tlvsSize) {
        // 简单检查
        if (data.length < offset + tlvsSize) {
            throw new ProtocolException("TLV数据长度不正确");
        }

        List<TlvObject> tlvList = new ArrayList<>();

        int index = 0;

        while (index < tlvsSize) {
            // 数据类型（2 字节）
            int type = IntegerUtil.decodeInteger2byte(data, offset + index);
            index += 2;

            // 数据长度（2 字节）
            int length = IntegerUtil.decodeInteger2byte(data, offset + index);
            index += 2;

            // 长度检查
            if (data.length < offset + index + length) {
                throw new ProtocolException("TLV报文长度不正确");
            }

            String txt = HexUtils.byteArrayToHexString(data, offset + index, length, false);
            index += length;

            TlvObject tlv = new TlvObject();
            tlv.setType(type);
            tlv.setValue(txt);

            tlvList.add(tlv);
        }

        if (index != tlvsSize) {
            throw new ProtocolException("TLV报文长度不正确");
        }


        return tlvList;
    }

    public int getEncodeLength() {
        String txt = this.value;
        if (0 <= txt.indexOf(" ")) {
            txt.replaceAll(" ", "");
        }

        // 截断数据：最大只支持65535的数据
        txt = txt.substring(0, Math.min(this.value.length(), 0x10000 * 2));

        int length = 4 + txt.length() / 2;
        return length;
    }

    public int encode(byte[] data, int offset) {
        String txt = this.value;
        if (0 <= txt.indexOf(" ")) {
            txt.replaceAll(" ", "");
        }

        // 截断数据：最大只支持65535的数据
        txt = txt.substring(0, Math.min(this.value.length(), 0x10000 * 2));


        int index = 0;

        // 参数类型（2 字节）
        IntegerUtil.encodeInteger2byte(this.type, data, offset + index);
        index += 2;

        // 数据长度（2字节）
        int length = txt.length() / 2;
        IntegerUtil.encodeInteger2byte(length, data, offset + index);
        index += 2;

        HexUtils.hexStringToByteArray(txt, data, offset + index);
        index += length;

        return index;
    }

    public int getDecodeSize(byte[] data, int offset) {
        // 简单检查
        if (data.length < offset + 4) {
            throw new ProtocolException("TLV数据长度不正确");
        }

        // 数据长度（2 字节）
        int length = IntegerUtil.decodeInteger2byte(data, offset + 2);
        return length;
    }
}
