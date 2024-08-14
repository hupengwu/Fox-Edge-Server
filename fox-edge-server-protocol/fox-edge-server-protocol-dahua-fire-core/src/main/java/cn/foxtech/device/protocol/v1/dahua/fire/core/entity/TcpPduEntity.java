/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * TCP模式下对的报文单元
 * 报文结构：启动符 + 控制单元 + 应用数据单元 + 校验和 + 结束符
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class TcpPduEntity extends PduEntity {
    /**
     * 控制单元
     */
    private TcpCtrlEntity ctrlEntity = new TcpCtrlEntity();
    /**
     * 应用数据单元：可选项目
     */
    private AduEntity aduEntity;

    public static int getSum(byte[] data, int offset, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += data[offset + i] & 0xff;
        }

        return sum;
    }

    public static byte[] encodeEntity(TcpPduEntity pduEntity) {
        // 对应用单元，进行数据编码
        byte[] aduData = pduEntity.aduEntity == null ? null : AduEntity.encodeEntity(pduEntity.aduEntity);

        // 确定：应用单元和它的长度
        int aduDataLength = (aduData != null ? aduData.length : 0);

        // 将应用单元的长度，记录到控制单元上
        pduEntity.ctrlEntity.setAduLength(aduDataLength);

        // 对控制单元进行数据编码
        byte[] ctrlData = TcpCtrlEntity.encodeEntity(pduEntity.ctrlEntity);

        // 分配PDU的数据块
        byte[] data = new byte[5 + ctrlData.length + aduDataLength];

        int index = 0;

        // 启动符（2 字节）
        data[index++] = (byte) '@';
        data[index++] = (byte) '@';

        // 控制单元
        System.arraycopy(ctrlData, 0, data, index, ctrlData.length);
        index += ctrlData.length;

        // 应用单元
        if (aduData != null) {
            System.arraycopy(aduData, 0, data, index, aduData.length);
            index += aduData.length;
        }


        // 校验和（1 字节）
        int sum = getSum(data, 2, data.length - 5);
        data[index++] = (byte) sum;

        // 结束符（2 字节）
        data[index++] = (byte) '#';
        data[index++] = (byte) '#';

        return data;
    }

    public static TcpPduEntity decodeEntity(byte[] data) {
        TcpPduEntity pduEntity = new TcpPduEntity();

        // 获得PDU的大小：PUD外层格式，是否合法
        int pduSize = getPduSize(data);

        int index = 2;

        // 解码：控制单元
        TcpCtrlEntity ctrlEntity = pduEntity.getCtrlEntity();
        TcpCtrlEntity.decodeEntity(data, index, ctrlEntity);
        index += TcpCtrlEntity.size();


        // 检测：是否有应用数据
        if (ctrlEntity.getAduLength() == 0) {
            pduEntity.aduEntity = null;
            return pduEntity;
        }

        // 解码：应用数据单元
        AduEntity aduEntity = new AduEntity();
        AduEntity.decodeEntity(data, index, ctrlEntity.getAduLength(), ctrlEntity.getCmd(), aduEntity);
        pduEntity.aduEntity = aduEntity;

        return pduEntity;
    }

    public static int getPduSize(byte[] data) {
        if (data.length < 5) {
            throw new ProtocolException("PDU的长度，至少5个字节");
        }

        // 启动符（2 字节）
        int h = data[0] & 0xff;
        int l = data[1] & 0xff;
        if ((h != (int) '@') || (l != (int) '@')) {
            throw new ProtocolException("起始符。必须为@@");
        }

        // 控制单元的报文长度
        int ctrlLength = TcpCtrlEntity.size();

        // 应用单元的报文长度
        int lengthOffset = TcpCtrlEntity.getLengthOffset();
        l = data[2 + lengthOffset + 0] & 0xff;
        h = data[2 + lengthOffset + 1] & 0xff;
        int aduLength = h * 0x100 + l;

        // 报文长度不正确
        if (data.length < 5 + ctrlLength + aduLength) {
            throw new ProtocolException("PDU的长度，不匹配");
        }

        // 结束符（2 字节）
        h = data[3 + ctrlLength + aduLength] & 0xff;
        l = data[4 + ctrlLength + aduLength] & 0xff;
        if ((h != (int) '#') || (l != (int) '#')) {
            throw new ProtocolException("结束符。必须为##");
        }

        // 校验和（1 字节）
        int sum = getSum(data, 2, ctrlLength + aduLength);
        if (data[2 + ctrlLength + aduLength] != (byte) sum) {
            throw new ProtocolException("校验和不正确");
        }

        return ctrlLength + aduLength + 5;
    }

    public int getSn() {
        return this.getCtrlEntity().getSn();
    }

    public void setSn(int sn) {
        this.getCtrlEntity().setSn(sn);
    }
}

