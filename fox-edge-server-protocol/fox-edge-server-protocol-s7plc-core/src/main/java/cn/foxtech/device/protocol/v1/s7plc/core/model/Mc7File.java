package cn.foxtech.device.protocol.v1.s7plc.core.model;


import cn.foxtech.device.protocol.v1.s7plc.core.utils.HexUtil;
import cn.foxtech.device.protocol.v1.s7plc.core.common.buff.ByteReadBuff;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EFileBlockType;
import lombok.Data;

/**
 * mc7文件内容
 *
 * @author xingshuang
 */
@Data
public class Mc7File {

    /**
     * 源数据
     */
    private byte[] data = new byte[0];

    /**
     * 块类型
     */
    private EFileBlockType blockType;

    /**
     * 块编号
     */
    private int blockNumber;

    /**
     * mc7代码长度
     */
    private int mC7CodeLength;

    /**
     * 字节转换为对象
     *
     * @param data 字节数组
     * @return Mc7File
     */
    public static Mc7File fromBytes(final byte[] data) {
        if (data == null || data.length < 36) {
            throw new IllegalArgumentException("MC7文件内容至少要36个字节长度");
        }
        ByteReadBuff buff = new ByteReadBuff(data);
        Mc7File res = new Mc7File();
        res.data = data;
        byte blockTypeByte = buff.getByte(5);
        res.blockType = EFileBlockType.from(HexUtil.toHexString(new byte[]{blockTypeByte}));
        res.blockNumber = buff.getUInt16(6);
        res.mC7CodeLength = buff.getUInt16(34);
        return res;
    }

    /**
     * 字节数组总长度
     *
     * @return 长度
     */
    public int getLoadMemoryLength() {
        return this.data.length;
    }
}
