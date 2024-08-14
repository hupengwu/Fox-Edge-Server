/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.StringUtil;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.VersionUtil;
import cn.foxtech.device.protocol.v1.utils.BcdUtils;
import cn.foxtech.device.protocol.v1.utils.ByteUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
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
public class InfObjRegisterExEntity extends InfObjEntity {
    /**
     * 协议版本号（2 字节）
     */
    private String protocolVersion = "1.00";
    /**
     * 设备ID（8 字节）
     */
    private String imei = "";
    /**
     * 保活时间（4 字节）
     */
    private long activeTime = 0;
    /**
     * 软件版本号（2 字节）
     */
    private String softwareVersion = "1.00";
    /**
     * TAU时间（2 字节）
     */
    private int tauTime = 0;
    /**
     * 序列号（16 字节）
     */
    private String sn = "";
    /**
     * 序列号（10 字节）
     */
    private String ccid = "";
    /**
     * 额定电压（2 字节）
     */
    private double ratedVoltage = 0;
    /**
     * 欠压电压（2 字节）
     */
    private double underVoltage = 0;
    /**
     * NB频段（1 字节）
     */
    private int nbFreqBand = 0;
    /**
     * IMSI（8 字节）
     */
    private String imsi = "";
    /**
     * CELL ID（5 字节）
     */
    private String cellId = "";
    /**
     * APN（10 字节）
     */
    private String apn = "";
    /**
     * 预留（4 字节）
     */
    private String reserve1 = "";
    /**
     * 系统类型（1 字节）
     */
    private int sysType = 0;
    /**
     * 重连标志（1 字节）
     */
    private int reconnect = 0;
    /**
     * 设备型号（1 字节）
     */
    private String deviceType = "";
    /**
     * 设备UUID（12 字节）
     */
    private String deviceUuid = "";
    /**
     * 预留2（4 字节）
     */
    private String reserve2 = "";

    public static void decodeEntity(byte[] data, InfObjRegisterExEntity entity) {
        if (data.length != entity.size()) {
            throw new ProtocolException("信息对象" + entity.getClass().getSimpleName() + "，必须长度为" + entity.size());
        }


        int index = 0;

        // 协议版本(2 字节)
        entity.protocolVersion = VersionUtil.decodeVersion2byte(data, index);
        index += 2;


        // IMEI(8 字节):BCD格式
        entity.imei = BcdUtils.bcd2str(data, index, 8, true);
        index += 8;


        // 保活时间(4 字节)
        entity.activeTime = ByteUtils.decodeInt32(data, index, false);
        index += 4;

        // 软件版本(2 字节)
        entity.softwareVersion = VersionUtil.decodeVersion2byte(data, index);
        index += 2;

        // TAU时间(2 字节)
        int hv = data[index++] & 0xff;
        int lv = data[index++] & 0xff;
        entity.tauTime = hv * 100 + lv;

        // SN(16 字节):ASCII格式
        entity.sn = ByteUtils.decodeAscii(data, index, 16, true);
        index += 16;

        // CCID(10 字节):BCD格式
        entity.ccid = BcdUtils.bcd2str(data, index, 10, true);
        index += 10;

        // 额定电压(2 字节)
        entity.ratedVoltage = ByteUtils.decodeInt16(data, index, false) * 0.1;
        index += 2;

        // 欠压电压(2 字节)
        entity.underVoltage = ByteUtils.decodeInt16(data, index, false) * 0.1;
        index += 2;

        // NB频段(1 字节)
        entity.nbFreqBand = data[index++] & 0xff;

        // IMSI(8 字节):BCD格式
        entity.imsi = BcdUtils.bcd2str(data, index, 8, true);
        index += 8;

        // CELL ID(5 字节):BCD格式
        entity.cellId = BcdUtils.bcd2str(data, index, 5, true);
        index += 5;

        // APN(10 字节):ASCII格式
        entity.apn = ByteUtils.decodeAscii(data, index, 10, true);
        index += 10;

        // 预留1(4 字节)：HEX格式
        entity.reserve1 = HexUtils.byteArrayToHexString(data, index, 4, false);
        index += 4;

        // 系统类型(1 字节)
        entity.sysType = data[index++] & 0xff;

        // 重连接标志(1 字节)
        entity.reconnect = data[index++] & 0xff;

        // 设备型号(16 字节):ASCII格式
        entity.deviceType = ByteUtils.decodeAscii(data, index, 16, true);
        index += 16;

        // 设备UUID（12 字节）:ASCII格式
        entity.deviceUuid = ByteUtils.decodeAscii(data, index, 12, true);
        index += 12;

        // 预留2(4字节)：HEX格式
        entity.reserve2 = HexUtils.byteArrayToHexString(data, index, 4, false);
        index += 4;
    }

    public static byte[] encodeEntity(InfObjRegisterExEntity entity) {
        byte[] data = new byte[entity.size()];


        int index = 0;

        // 协议版本(2 字节)
        VersionUtil.encodeVersion2byte(entity.protocolVersion, data, index);
        index += 2;

        // IMEI(8 字节):BCD格式
        String imei = StringUtil.truncateString(entity.imei, 8 * 2);
        BcdUtils.str2bcd(imei, data, index, true);
        index += 8;

        // 保活时间(4 字节)
        ByteUtils.encodeInt32(entity.activeTime, data, index, false);
        index += 4;

        // 协议版本(2 字节)
        VersionUtil.encodeVersion2byte(entity.softwareVersion, data, index);
        index += 2;

        // TAU时间(2 字节)
        data[index++] = (byte) (entity.tauTime / 100);
        data[index++] = (byte) (entity.tauTime % 100);

        // SN(16 字节):ASCII格式
        String sn = StringUtil.truncateString(entity.sn, 16);
        ByteUtils.encodeAscii(sn, data, index, 16, true);
        index += 16;

        // CCID(10 字节):BCD格式
        String ccid = StringUtil.truncateString(entity.ccid, 10 * 2);
        BcdUtils.str2bcd(ccid, data, index, true);
        index += 10;

        // 额定电压(2 字节)
        ByteUtils.encodeInt16((int) (entity.ratedVoltage * 10), data, index, false);
        index += 2;

        // 欠压电压(2 字节)
        ByteUtils.encodeInt16((int) (entity.underVoltage * 10), data, index, false);
        index += 2;

        // NB频段(1 字节)
        data[index++] = (byte) entity.nbFreqBand;

        // IMSI(8 字节):BCD格式
        String imsi = StringUtil.truncateString(entity.imsi, 8 * 2);
        BcdUtils.str2bcd(imsi, data, index, true);
        index += 8;

        // CELL ID(5 字节):BCD格式
        String cellId = StringUtil.truncateString(entity.cellId, 5 * 2);
        BcdUtils.str2bcd(cellId, data, index, true);
        index += 5;

        // APN(10 字节):ASCII格式
        String apn = StringUtil.truncateString(entity.apn, 10);
        ByteUtils.encodeAscii(apn, data, index, 10, true);
        index += 10;

        // 预留1(4 字节)：HEX格式
        String reserve1 = StringUtil.truncateString(entity.reserve1, 4 * 2);
        HexUtils.hexStringToByteArray(reserve1, data, index);
        index += 4;

        // 系统类型(1 字节)
        data[index++] = (byte) entity.sysType;

        // 重连接标志(1 字节)
        data[index++] = (byte) entity.reconnect;

        // 设备型号(16 字节):ASCII格式
        String deviceType = StringUtil.truncateString(entity.deviceType, 16);
        ByteUtils.encodeAscii(deviceType, data, index, 16, true);
        index += 16;

        // 设备UUID(12 字节):ASCII格式
        String deviceUuid = StringUtil.truncateString(entity.deviceUuid, 12);
        ByteUtils.encodeAscii(deviceType, data, index, 12, true);
        index += 12;

        // 预留1(4 字节)：HEX格式
        String reserve2 = StringUtil.truncateString(entity.reserve2, 4 * 2);
        HexUtils.hexStringToByteArray(reserve2, data, index);
        index += 4;

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

    @Override
    public void decode(byte[] data) {
        decodeEntity(data, this);
    }

    @Override
    public byte[] encode() {
        return encodeEntity(this);
    }

    public int size() {
        return 110;
    }

}
