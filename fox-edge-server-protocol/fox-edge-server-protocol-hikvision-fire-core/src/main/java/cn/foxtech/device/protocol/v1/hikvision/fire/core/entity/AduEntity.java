/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.hikvision.fire.core.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.entity.infobj.InfObjEntity;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.enums.AduType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用数据单元
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class AduEntity {
    /**
     * 类型：AduType
     */
    private int type = 0;
    /**
     * 信息对象列表
     */
    private List<InfObjEntity> infObjEntities = new ArrayList<>();

    public static byte[] encodeEntity(AduEntity aduEntity) {
        int length = 2;

        List<byte[]> list = new ArrayList<>();
        for (InfObjEntity infObjEntity : aduEntity.infObjEntities) {
            byte[] data = infObjEntity.encode();

            length += data.length;
            list.add(data);
        }

        byte[] data = new byte[length];

        int index = 0;

        // 类型标志（1 字节）
        data[index++] = (byte) aduEntity.type;

        // 信息对象数目（1 字节）
        data[index++] = (byte) aduEntity.infObjEntities.size();

        for (byte[] item : list) {
            System.arraycopy(item, 0, data, index, item.length);
            index += item.length;
        }

        return data;
    }

    public static void decodeEntity(byte[] data, int offset, int aduLength, int cmd, AduEntity aduEntity) {
        if (data.length - offset < 2) {
            throw new ProtocolException("ADU的长度，至少2个字节");
        }


        int index = offset;

        // 类型标志（1 字节）
        int type = data[index++] & 0xff;
        aduEntity.type = type;

        // 信息对象数目（1 字节）
        int count = data[index++] & 0xff;


        // 获得对应的class
        Class clazz = AduInfObjMap.getInfObjClass(AduType.getEnum(aduEntity.type));
        if (clazz == null) {
            throw new ProtocolException("不支持的类型标识" + String.format("%02X", aduEntity.type));
        }


        // 清空列表
        aduEntity.infObjEntities.clear();

        // 信息体数量
        if (count < 1) {
            return;
        }

        try {
            // 实例化一个范例对象
            InfObjEntity entity = (InfObjEntity) clazz.newInstance();

            // 验证ADU的长度是否跟格式匹配：这决定了后面能否正确解码
            List<Integer> aduSizes = entity.getAduSizes(data, offset, aduLength);

            // 对ADU进行解码器
            for (Integer aduSize : aduSizes) {
                // 分配ADU的空间
                byte[] item = new byte[aduSize];

                System.arraycopy(data, index, item, 0, item.length);
                index += item.length;

                // 解码数据
                entity = (InfObjEntity) clazz.newInstance();
                entity.decode(item);

                // 保存数据
                aduEntity.infObjEntities.add(entity);
            }

        } catch (Exception e) {
            throw new ProtocolException("解码异常：" + e.getMessage());
        }
    }
}
