/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object.TlvObject;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.IntegerUtil;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.TimeUtil;
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
public abstract class InfObjGeneralEntity extends InfObjEntity {
    /**
     * 系统类型（1 字节）
     */
    private int sysType = 0;
    /**
     * 系统地址（1 字节）
     */
    private int sysAddress = 0;
    /**
     * 部件类型（1 字节）
     */
    private int compType = 0;
    /**
     * 部件回路（2 字节）
     */
    private int compCirc = 0;
    /**
     * 部件节点（2 字节）
     */
    private int compNode = 0;
    /**
     * 模拟量类型（1 字节）
     */
    private List<TlvObject> tlvs = new ArrayList<>();

    /**
     * 时间标签(6 字节)：控制单元中时间标签传输，秒在前，年在后，取自系统当前时间，如 15:14:17 11/9/19；
     */
    private String time = "2000-01-01 00:00:00";

    public static void decodeEntity(byte[] data, InfObjGeneralEntity entity) {
        // 最小长度检测
        if (data.length < 15) {
            throw new ProtocolException("信息对象，最小长度为15");
        }

        // 信息对象的数量
        int count = data[7] & 0xff;

        // 实际长度检测
        if (data.length != 15 + count) {
            throw new ProtocolException("信息对象，长度=" + 15 + count);
        }


        int index = 0;

        // 系统类型(1 字节)
        entity.sysType = data[index++] & 0xff;

        // 系统地址(1 字节)
        entity.sysAddress = data[index++] & 0xff;

        // 部件类型(1 字节)
        entity.compType = data[index++] & 0xff;

        // 部件回路(2 字节)
        entity.compCirc = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;

        // 部件节点(2 字节)
        entity.compNode = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;

        // TLV的数据长度(2 字节)
        int tlvsSize = entity.compNode = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;

        // TLV列表的分解
        entity.tlvs = TlvObject.decodeTlvList(data, index, tlvsSize);
        index += tlvsSize;

        // 时间标签(6 字节)
        entity.time = TimeUtil.decodeTime6byte(data, index);
        index += 6;
    }

    public static byte[] encodeEntity(InfObjGeneralEntity entity) {
        // TLV的总长度
        int tlvsSize = entity.getTlvsSize();

        // 分配ADU空间
        byte[] data = new byte[15 + tlvsSize];


        int index = 0;

        // 系统类型(1 字节)
        data[index++] = (byte) entity.sysType;

        // 系统地址(1 字节)
        data[index++] = (byte) entity.sysAddress;

        // 部件类型(1 字节)
        data[index++] = (byte) entity.compType;

        // 部件回路(2 字节)
        IntegerUtil.encodeInteger2byte(entity.compCirc, data, index);
        index += 2;

        // 部件节点(2 字节)
        IntegerUtil.encodeInteger2byte(entity.compNode, data, index);
        index += 2;

        // 数据总长度(2 字节)
        IntegerUtil.encodeInteger2byte(tlvsSize, data, index);
        index += 2;

        // 模拟量列表
        for (TlvObject tlv : entity.tlvs) {
            int length = tlv.encode(data, index);
            index += length;
        }


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
        // N=前面数据[7 字节]+模拟量数量[1 字节]+模拟量数目[1字节]+模拟量[4字节*N]

        List<Integer> aduList = new ArrayList<>();

        int index = 2;
        for (int i = 0; i < count; i++) {
            // 前面部分的数据（7 字节）
            index += 7;

            // 简单校验长度
            if (offset + index >= data.length) {
                throw new ProtocolException("验证ADU的长度与具体的格式，不匹配");
            }

            // 数据长度（2 字节）
            int length = IntegerUtil.decodeInteger2byte(data, offset + index);
            index += 2;

            // 数据内容( N字节)
            index += length;

            // 时间标签（6 字节）
            index += 6;

            aduList.add(15 + length);
        }

        if (aduLength != index) {
            throw new ProtocolException("验证ADU的长度与具体的格式，不匹配");
        }

        // 返回列表
        return aduList;
    }

    public int getTlvsSize() {
        int length = 0;
        for (TlvObject tlv : this.tlvs) {
            length += tlv.getEncodeLength();
        }
        return length;
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

