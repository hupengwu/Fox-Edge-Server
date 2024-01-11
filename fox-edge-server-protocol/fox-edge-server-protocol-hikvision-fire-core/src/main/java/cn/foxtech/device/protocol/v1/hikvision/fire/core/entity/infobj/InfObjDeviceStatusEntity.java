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
public class InfObjDeviceStatusEntity extends InfObjEntity {
    /**
     * 正常状态（bit0）
     */
    private boolean normal = false;
    /**
     * 火警（bit1）
     */
    private boolean fireAlarm = false;
    /**
     * 故障（bit2）
     */
    private boolean fault = false;
    /**
     * 主电故障（bit1）
     */
    private boolean mainFault = false;
    /**
     * 备用电故障（bit1）
     */
    private boolean backFault = false;
    /**
     * 通信通道故障（bit1）
     */
    private boolean commFault = false;
    /**
     * 连接线路故障（bit1）
     */
    private boolean linkFault = false;
    /**
     * 预览（bit1）
     */
    private boolean reserve = false;

    /**
     * 时间标签(6 字节)：控制单元中时间标签传输，秒在前，年在后，取自系统当前时间，如 15:14:17 11/9/19；
     */
    private String time = "2000-01-01 00:00:00";

    public static void decodeEntity(byte[] data, InfObjDeviceStatusEntity entity) {
        if (data.length != entity.size()) {
            throw new ProtocolException("信息对象" + entity.getClass().getSimpleName() + "，必须长度为" + entity.size());
        }


        int index = 0;

        // 系统状态(1 字节)
        int sysStatus = data[index++] & 0xff;
        entity.normal = (sysStatus & 0x01) != 0;
        entity.fireAlarm = (sysStatus & 0x02) != 0;
        entity.fault = (sysStatus & 0x04) != 0;
        entity.mainFault = (sysStatus & 0x08) != 0;
        entity.backFault = (sysStatus & 0x10) != 0;
        entity.commFault = (sysStatus & 0x20) != 0;
        entity.linkFault = (sysStatus & 0x40) != 0;
        entity.reserve = (sysStatus & 0x80) != 0;

        // 时间标签(6 字节)
        entity.time = TimeUtil.decodeTime6byte(data, index);
        index += 6;
    }

    public static byte[] encodeEntity(InfObjDeviceStatusEntity entity) {
        byte[] data = new byte[entity.size()];


        int index = 0;


        // 系统状态(1 字节)
        int sysStatus = 0;
        sysStatus |= entity.normal ? 0x01 : 0x00;
        sysStatus |= entity.fireAlarm ? 0x02 : 0x00;
        sysStatus |= entity.fault ? 0x04 : 0x00;
        sysStatus |= entity.mainFault ? 0x08 : 0x00;
        sysStatus |= entity.backFault ? 0x10 : 0x00;
        sysStatus |= entity.commFault ? 0x20 : 0x00;
        sysStatus |= entity.linkFault ? 0x40 : 0x00;
        sysStatus |= entity.reserve ? 0x80 : 0x00;
        data[index++] = (byte) sysStatus;

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
        return 1 + 6;
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
