/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.AddressUtil;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.IntegerUtil;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.TimeUtil;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.VersionUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 控制单元
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class TcpCtrlEntity extends CtrlEntity {
    /**
     * 业务流水号(2 字节)：业务流水号由通信主动发起方维护，按小端格式传输，从 1 开始计数，2B 满值后自动翻转为 0。帧内容发
     * 生改变，流水号增加
     */
    private int sn = 0;
    /**
     * 协议版本号(2 字节)：协议版本号传输，主版本号在前，次版本号在后，当前协议版本号为 v1.04；
     */
    private String protocolVersion = "1.00";
    /**
     * 时间标签(6 字节)：控制单元中时间标签传输，秒在前，年在后，取自系统当前时间，如 15:14:17 11/9/19；
     */
    private String time = "2000-01-01 00:00:00";
    /**
     * 源地址(6 字节):源地址/目的地址为设备地址时，可以是设备 SN 号、IMEI 号等，取后 12 位，按大端格式传输；源地址/目
     * 的地址为平台地址时，默认全 F，允许填 0；
     */
    private String srcAddr = AddressUtil.PLATFORM_DEFAULT;
    /**
     * 目的地址(6 字节)：同上
     */
    private String dstAddr = AddressUtil.PLATFORM_DEFAULT;
    /**
     * 应用数据单元长度(2 字节):按小端格式传输，信息对象数目不能过大，应用数据单元总长度不得超过 512 字节；
     */
    private int aduLength = 0;
    /**
     * 命令字(1 字节)
     */
    private int cmd = 0;

    public static int size() {
        return 25;
    }

    /**
     * 报文长度位置的偏移量
     *
     * @return 报文长度，在控制域中的偏移量
     */
    public static int getLengthOffset() {
        return TcpCtrlEntity.size() - 3;
    }

    public static byte[] encodeEntity(TcpCtrlEntity entity) {
        byte[] data = new byte[TcpCtrlEntity.size()];

        int index = 0;


        // 业务流水号(2 字节)
        IntegerUtil.encodeInteger2byte(entity.sn, data, index);
        index += 2;

        // 协议版本(2 字节)
        VersionUtil.encodeVersion2byte(entity.protocolVersion, data, index);
        index += 2;


        // 时间标签(6 字节)
        TimeUtil.encodeTime6byte(entity.time, data, index);
        index += 6;

        // 源地址(6 字节)
        AddressUtil.encodeAddress6byte(entity.srcAddr, data, index);
        index += 6;

        // 目的地址(6 字节)
        AddressUtil.encodeAddress6byte(entity.dstAddr, data, index);
        index += 6;

        // 应用数据单元长度(2 字节)
        IntegerUtil.encodeInteger2byte(entity.aduLength, data, index);
        index += 2;

        /**
         * 命令字(1 字节)
         */
        data[index++] = (byte) entity.cmd;

        return data;
    }

    public static void decodeEntity(byte[] data, int offset, TcpCtrlEntity entity) {
        if (data.length < TcpCtrlEntity.size() + offset) {
            throw new ProtocolException("控制单元，固定长度为25");
        }


        int index = offset;

        // 业务流水号(2 字节)
        entity.sn = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;

        // 协议版本(2 字节)
        entity.protocolVersion = VersionUtil.decodeVersion2byte(data, index);
        index += 2;


        // 时间标签(6 字节)
        entity.time = TimeUtil.decodeTime6byte(data, index);
        index += 6;

        // 源地址(6 字节)
        entity.srcAddr = AddressUtil.decodeAddress6byte(data, index);
        index += 6;

        // 目的地址(6 字节)
        entity.dstAddr = AddressUtil.decodeAddress6byte(data, index);
        index += 6;

        // 应用数据单元长度(2 字节)
        entity.aduLength = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;

        /**
         * 命令字(1 字节)
         */
        entity.cmd = data[index++] & 0xff;
    }

    public void bind(TcpCtrlEntity other) {
        this.sn = other.sn;
        this.protocolVersion = other.protocolVersion;
        this.time = other.time;
        this.srcAddr = other.srcAddr;
        this.dstAddr = other.dstAddr;
        this.aduLength = other.aduLength;
        this.cmd = other.cmd;
    }

    public void swapAddr() {
        String tmp = this.dstAddr;
        this.dstAddr = this.srcAddr;
        this.srcAddr = tmp;
    }
}
