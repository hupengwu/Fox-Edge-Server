package cn.foxtech.device.protocol.v1.hikvision.fire.core.entity.infobj;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.utils.TimeUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 信息对象: 注册包
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class InfObjDeviceOperateEntity extends InfObjEntity {
    /**
     * 复位（bit0）
     */
    private boolean reset = false;
    /**
     * 消音（bit1）
     */
    private boolean mute = false;
    /**
     * 手动告警（bit2）
     */
    private boolean manualAlarm = false;
    /**
     * 警情清除（bit1）
     */
    private boolean clearAlarm = false;
    /**
     * 自检（bit1）
     */
    private boolean selfCheck = false;
    /**
     * 预留1（bit1）
     */
    private boolean reserve1 = false;
    /**
     * 测试（bit1）
     */
    private boolean test = false;
    /**
     * 预留2（bit1）
     */
    private boolean reserve2 = false;

    /**
     * 操作员编号（1 字节）
     */
    private int operator = 0;

    /**
     * 时间标签(6 字节)：控制单元中时间标签传输，秒在前，年在后，取自系统当前时间，如 15:14:17 11/9/19；
     */
    private String time = "2000-01-01 00:00:00";

    public static void decodeEntity(byte[] data, InfObjDeviceOperateEntity entity) {
        if (data.length != entity.size()) {
            throw new ProtocolException("信息对象" + entity.getClass().getSimpleName() + "，必须长度为" + entity.size());
        }


        int index = 0;

        // 系统状态(1 字节)
        int sysStatus = data[index++] & 0xff;
        entity.reset = (sysStatus & 0x01) != 0;
        entity.mute = (sysStatus & 0x02) != 0;
        entity.manualAlarm = (sysStatus & 0x04) != 0;
        entity.clearAlarm = (sysStatus & 0x08) != 0;
        entity.selfCheck = (sysStatus & 0x10) != 0;
        entity.reserve1 = (sysStatus & 0x20) != 0;
        entity.test = (sysStatus & 0x40) != 0;
        entity.reserve2 = (sysStatus & 0x80) != 0;

        // 操作员编号(1 字节)
        entity.operator = data[index++] & 0xff;

        // 时间标签(6 字节)
        entity.time = TimeUtil.decodeTime6byte(data, index);
        index += 6;
    }

    public static byte[] encodeEntity(InfObjDeviceOperateEntity entity) {
        byte[] data = new byte[entity.size()];


        int index = 0;

        // 系统状态(1 字节)
        int sysStatus = 0;
        sysStatus |= entity.reset ? 0x01 : 0x00;
        sysStatus |= entity.mute ? 0x02 : 0x00;
        sysStatus |= entity.manualAlarm ? 0x04 : 0x00;
        sysStatus |= entity.clearAlarm ? 0x08 : 0x00;
        sysStatus |= entity.selfCheck ? 0x10 : 0x00;
        sysStatus |= entity.reserve1 ? 0x20 : 0x00;
        sysStatus |= entity.test ? 0x40 : 0x00;
        sysStatus |= entity.reserve2 ? 0x80 : 0x00;
        data[index++] = (byte) sysStatus;

        // 操作员编号(1 字节)
        data[index++] = (byte) entity.operator;

        // 时间标签(6 字节)
        TimeUtil.encodeTime6byte(entity.time, data, index);
        index += 6;

        return data;
    }

    @Override
    public List<Integer> getAduSizes(byte[] data, int offset, int aduLength) {
        // 信息体的数量
        int count = data[offset + 1];

        // 类型标志[1 字节]+信息体数量[1 字节]+多个信息体对象[N 字节]
        int length = count * this.size();

        if (aduLength != 2 + length) {
            throw new ProtocolException("验证ADU的长度与具体的格式，不匹配");
        }

        // 返回列表
        List<Integer> aduList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            aduList.add(this.size());
        }
        return aduList;
    }

    public int size() {
        return 2 + 6;
    }

    @Override
    public void decode(byte[] data) {
        decodeEntity(data, this);
    }

    @Override
    public byte[] encode() {
        return encodeEntity(this);
    }


}
