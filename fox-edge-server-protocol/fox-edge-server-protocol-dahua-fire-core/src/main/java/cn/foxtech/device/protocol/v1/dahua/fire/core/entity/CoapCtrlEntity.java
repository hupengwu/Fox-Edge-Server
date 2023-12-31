package cn.foxtech.device.protocol.v1.dahua.fire.core.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.IntegerUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class CoapCtrlEntity extends CtrlEntity {
    /**
     * 应用数据单元长度(2 字节):按小端格式传输，信息对象数目不能过大，应用数据单元总长度不得超过 512 字节；
     */
    private int aduLength = 0;
    /**
     * 命令字(1 字节)
     */
    private int cmd = 0;


    public static int size() {
        return 3;
    }

    /**
     * 报文长度位置的偏移量
     *
     * @return 报文长度，在控制域中的偏移量
     */
    public static int getLengthOffset() {
        return 0;
    }

    public static byte[] encodeEntity(CoapCtrlEntity entity) {
        byte[] data = new byte[CoapCtrlEntity.size()];

        int index = 0;

        // 应用数据单元长度(2 字节)
        IntegerUtil.encodeInteger2byte(entity.aduLength, data, index);
        index += 2;

        /**
         * 命令字(1 字节)
         */
        data[index++] = (byte) entity.cmd;

        return data;
    }

    public static void decodeEntity(byte[] data, int offset, CoapCtrlEntity entity) {
        if (data.length < CoapCtrlEntity.size() + offset) {
            throw new ProtocolException("控制单元，固定长度为3");
        }

        int index = offset;


        // 应用数据单元长度(2 字节)
        entity.aduLength = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;

        /**
         * 命令字(1 字节)
         */
        entity.cmd = data[index++] & 0xff;
    }
}
