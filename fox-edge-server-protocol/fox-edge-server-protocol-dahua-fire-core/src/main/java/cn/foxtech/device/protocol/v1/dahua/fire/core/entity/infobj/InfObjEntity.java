package cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj;

import java.util.List;

public abstract class InfObjEntity {
    /**
     * 编码
     *
     * @param data 数据
     */
    public abstract void decode(byte[] data);

    /**
     * 解码
     *
     * @return 数据
     */
    public abstract byte[] encode();

    /**
     * 编码时，确定包长度
     *
     * @return 包长度
     */
    public abstract int getEncodeSize();

    /**
     * 获得ADU们的长度
     * @param data 完整的PDU数据报
     * @param offset ADU在PDU中的起始位置
     * @param aduLength ADU在CTRL中标识的长度信息
     */
    public abstract List<Integer> getAduSizes(byte[] data, int offset, int aduLength);

    /**
     * 解码时，确定包长度
     *
     * @param data      整个数据报的数据
     * @param offset    adu偏移量
     * @param aduLength adu长度
     * @return 解码长度
     */
    public abstract int getDecodeSize(byte[] data, int offset, int aduLength);
}
