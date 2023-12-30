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
     * 获得ADU们的长度
     * @param data 完整的PDU数据报
     * @param offset ADU在PDU中的起始位置
     * @param aduLength ADU在CTRL中标识的长度信息
     */
    public abstract List<Integer> getAduSizes(byte[] data, int offset, int aduLength);
}
