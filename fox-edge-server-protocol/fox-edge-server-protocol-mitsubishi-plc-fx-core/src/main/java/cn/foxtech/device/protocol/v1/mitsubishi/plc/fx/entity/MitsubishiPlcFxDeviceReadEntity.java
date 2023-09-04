package cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class MitsubishiPlcFxDeviceReadEntity extends MitsubishiPlcFxEntity {
    /**
     * 返回：读数据内容
     */
    private String data = "";

    /**
     * 发送：读取数量
     */
    private int count = 0;

    @Override
    public int encodeAddress() {
        String target = this.getTarget();
        int address = this.getAddress();

        if (address >= 0 && address < 1024) {
            if ("S".equals(target)) {
                return address * 2 + 0x0000;
            } else if ("X".equals(target)) {
                return address * 2 + 0x0080;
            } else if ("Y".equals(target)) {
                return address * 2 + 0x00A0;
            } else if ("T".equals(target)) {
                return address * 2 + 0x00C0;
            } else if ("M".equals(target)) {
                return address * 2 + 0x0100;
            } else if ("C".equals(target)) {
                return address * 2 + 0x01C0;
            } else if ("D".equals(target)) {
                return address * 2 + 0x1000;
            } else {
                throw new ProtocolException("Target类型不支持!");
            }
        } else if (address >= 8000 && address < 8512) {
            return (address - 8000) * 2 + 0x8000;
        } else {
            throw new ProtocolException("地址范围不支持!");
        }
    }
}
