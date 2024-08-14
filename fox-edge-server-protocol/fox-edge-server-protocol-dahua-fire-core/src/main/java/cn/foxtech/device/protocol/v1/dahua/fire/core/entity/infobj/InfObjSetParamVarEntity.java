/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object.ParVarObject;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.IntegerUtil;
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
public class InfObjSetParamVarEntity extends InfObjEntity {
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
     * 参数（1+1+N 字节）
     */
    private ParVarObject param = new ParVarObject();

    public static void decodeEntity(byte[] data, InfObjSetParamVarEntity entity) {
        if (data.length < 9) {
            throw new ProtocolException("同步参数（可变）的信息对象，长度不能小于9");
        }

        if (data.length != data[8] + 9) {
            throw new ProtocolException("同步参数（可变）的信息对象，长度不能长度不正确");
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

        // 参数(1+1+N 字节)
        entity.param.decode(data, index);
        index += 2;
    }

    public static byte[] encodeEntity(InfObjSetParamVarEntity entity) {
        byte[] data = new byte[entity.size()];


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


        // 参数数值(4 字节)
        index += entity.param.encode(data, index);

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

            // 参数类型（1 字节）
            int type = data[offset + index++];

            // 参数长度（1 字节）
            int length = data[offset + index++];

            // 参数内容（N 字节）
            index += length;


            aduList.add(9 + length);
        }

        if (aduLength != index) {
            throw new ProtocolException("验证ADU的长度与具体的格式，不匹配");
        }

        // 返回列表
        return aduList;
    }

    /**
     * 包长度
     *
     * @return 包长度
     */
    public int size() {
        return 9 + this.param.getSize();
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
