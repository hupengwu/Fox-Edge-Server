/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.StringUtil;
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
public abstract class InfObjUpgradeStatusEntity extends InfObjEntity {
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
     * 状态（1 字节）
     */
    private int status = 0;


    public static void decodeEntity(byte[] data, InfObjUpgradeStatusEntity entity) {
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

        // 状态(1 字节)
        entity.status = data[index++] & 0xff;
    }

    public static byte[] encodeEntity(InfObjUpgradeStatusEntity entity) {
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

        // 软件类型(1 字节)
        data[index++] = (byte) entity.status;

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
        return 37;
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
