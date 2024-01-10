package cn.foxtech.device.protocol.v1.hikvision.fire.core.entity;

public abstract class PduEntity {
    /**
     * 获得流水号
     *
     * @return sn
     */
    public abstract int getSn();

    /**
     * 填写流水号
     *
     * @param sn sn
     */
    public abstract void setSn(int sn);
}
