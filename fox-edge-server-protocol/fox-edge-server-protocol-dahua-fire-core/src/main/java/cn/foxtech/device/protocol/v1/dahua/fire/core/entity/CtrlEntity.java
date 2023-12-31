package cn.foxtech.device.protocol.v1.dahua.fire.core.entity;

public abstract class CtrlEntity {
    /**
     * 应用数据单元长度(2 字节):按小端格式传输，信息对象数目不能过大，应用数据单元总长度不得超过 512 字节；
     */
    public abstract int getAduLength();

    /**
     * 填写ADU长度
     *
     * @param aduLength AUD长度
     */
    public abstract void setAduLength(int aduLength);

    /**
     * 命令字(1 字节)
     */
    public abstract int getCmd();

    /**
     * 填写cmd
     *
     * @param cmd cmd长
     */
    public abstract void setCmd(int cmd);
}
