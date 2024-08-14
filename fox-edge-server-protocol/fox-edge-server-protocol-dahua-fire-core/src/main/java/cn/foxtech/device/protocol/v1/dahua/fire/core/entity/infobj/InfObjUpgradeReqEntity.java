/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object.AddrObject;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.IntegerUtil;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.StringUtil;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.VersionUtil;
import cn.foxtech.device.protocol.v1.utils.ByteUtils;
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
public class InfObjUpgradeReqEntity extends InfObjEntity {
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
     * 部件地址（32 字节）
     */
    private String compAddress = "";
    /**
     * 软件类型（1 字节）
     */
    private int softwareType = 0;
    /**
     * 软件版本：预留（2 字节）
     */
    private int softwareReserve = 0;
    /**
     * 软件版本：版本号（2 字节）
     */
    private String softwareVersion = "1.00";
    /**
     * 地址类型（1 字节）
     */
    private int addressType = 0;

    /**
     * 地址类型（N 字节）
     */
    private AddrObject addrObject = new AddrObject();


    public static void decodeEntity(byte[] data, InfObjUpgradeReqEntity entity) {
        if (data.length != entity.size()) {
            throw new ProtocolException("信息对象" + entity.getClass().getSimpleName() + "，必须长度为" + entity.size());
        }


        int index = 0;

        // 系统类型(1 字节)
        entity.sysType = data[index++] & 0xff;

        // 系统地址(1 字节)
        entity.sysAddress = data[index++] & 0xff;

        // 部件类型(1 字节)
        entity.compType = data[index++] & 0xff;

        // 部件地址(32 字节)
        entity.compAddress = ByteUtils.decodeAscii(data, index, 32, true);
        index += 32;

        // 软件类型(1 字节)
        entity.softwareType = data[index++] & 0xff;

        // 软件版本：预留(2 字节)
        entity.softwareReserve = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;

        // 模拟量数值(2 字节)
        entity.softwareVersion = VersionUtil.decodeVersion2byte(data, index);
        index += 2;

        // 地址类型(1 字节)
        entity.addressType = data[index++] & 0xff;

        // 地址对象（N 字节）
        int length = entity.addrObject.decode(data, index);
        index += length;
    }

    public static byte[] encodeEntity(InfObjUpgradeReqEntity entity) {
        byte[] data = new byte[entity.size()];


        int index = 0;

        // 系统类型(1 字节)
        data[index++] = (byte) entity.sysType;

        // 系统地址(1 字节)
        data[index++] = (byte) entity.sysAddress;

        // 部件类型(1 字节)
        data[index++] = (byte) entity.compType;

        // 部件地址(32 字节)
        String compAddress = StringUtil.truncateString(entity.compAddress, 32);
        ByteUtils.encodeAscii(compAddress, data, index, 32, true);
        index += 32;

        // 软件类型(1 字节)
        data[index++] = (byte) entity.softwareType;

        // 软件版本：预留(2 字节)
        IntegerUtil.encodeInteger2byte(entity.softwareReserve, data, index);
        index += 2;

        // 软件版本(2 字节)
        VersionUtil.encodeVersion2byte(entity.softwareVersion, data, index);
        index += 2;

        // 地址类型(1 字节)
        data[index++] = (byte) entity.addressType;

        // 地址信息（N 字节）
        entity.addrObject.encode(data, index);


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
            // 前面部分的数据（41 字节）
            index += 41;

            // 简单校验长度
            if (offset + index >= data.length) {
                throw new ProtocolException("验证ADU的长度与具体的格式，不匹配");
            }

            // 读取地址对象的数据长度
            int length = AddrObject.getSize(data, offset + index);
            index += length;


            aduList.add(41 + length);
        }

        if (aduLength != index) {
            throw new ProtocolException("验证ADU的长度与具体的格式，不匹配");
        }

        // 返回列表
        return aduList;
    }

    public int size() {
        return 41 + this.addrObject.getSize();
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
