/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.iec104.core.encoder;


import cn.foxtech.device.protocol.v1.iec104.core.entity.ControlEntity;
import cn.foxtech.device.protocol.v1.iec104.core.entity.IControlEntity;
import cn.foxtech.device.protocol.v1.iec104.core.entity.SControlEntity;
import cn.foxtech.device.protocol.v1.iec104.core.entity.UControlEntity;
import cn.foxtech.device.protocol.v1.iec104.core.enums.FrameTypeEnum;
import cn.foxtech.device.protocol.v1.iec104.core.enums.UControlTypeEnum;

/**
 * APCI(应用协议控制信息)编码/解码器
 * 控制域为4字节长度，
 * I帧为信息帧：为长帧，用于传输数据
 * S帧为确认帧，为短帧，用于确认接收的I帧
 * U帧为控制帧，为短帧，用于控制启动/停止/测试
 */
public class ControlEncoder {
    public static byte[] encodeControl(ControlEntity entity) {
        if (entity instanceof IControlEntity) {
            return encodeControl((IControlEntity) entity);
        }
        if (entity instanceof SControlEntity) {
            return encodeControl((SControlEntity) entity);
        }
        if (entity instanceof UControlEntity) {
            return encodeControl((UControlEntity) entity);
        }

        return new byte[4];
    }

    public static byte[] encodeControl(IControlEntity entity) {
        byte[] control = new byte[4];

        // 向左移动一位 保证低位的D0 是0
        short send = entity.getSend();
        send = (short) (send << 1);
        control[0] = (byte) ((send));
        control[1] = (byte) ((send >> 8));

        short accept = entity.getAccept();
        accept = (short) (accept << 1);
        control[2] = (byte) ((accept));
        control[3] = (byte) ((accept >> 8));
        return control;
    }


    public static byte[] encodeControl(SControlEntity entity) {
        byte[] control = new byte[4];

        // 向左移动一位 保证低位的D0 是0
        short send = 1;
        control[0] = (byte) ((send));
        control[1] = (byte) ((send >> 8));

        short accept = entity.getAccept();
        accept = (short) (accept << 1);
        control[2] = (byte) ((accept));
        control[3] = (byte) ((accept >> 8));
        return control;
    }

    public static byte[] encodeControl(UControlEntity entity) {
        UControlTypeEnum value = entity.getValue();
        if (value != null) {
            return ValueEncoder.intToByteArray(value.getValue());
        } else {
            return new byte[4];
        }
    }

    public static void decodeControl(byte[] control, IControlEntity entity) {
        short send = decodeSend(control);
        short accept = decodeAccept(control);

        entity.setSend(send);
        entity.setAccept(accept);
    }

    public static void decodeControl(byte[] control, SControlEntity entity) {
        short accept = decodeAccept(control);
        entity.setAccept(accept);
    }

    public static void decodeControl(byte[] control, UControlEntity entity) {
        UControlTypeEnum value = decodeUControl(control);
        entity.setValue(value);
    }

    public static ControlEntity decodeEntity(byte[] control, FrameTypeEnum formatType) {
        if (FrameTypeEnum.I_FORMAT.equals(formatType)) {
            IControlEntity entity = new IControlEntity();
            decodeControl(control, entity);
            return entity;
        }

        if (FrameTypeEnum.S_FORMAT.equals(formatType)) {
            SControlEntity entity = new SControlEntity();
            decodeControl(control, entity);
            return entity;
        }

        if (FrameTypeEnum.U_FORMAT.equals(formatType)) {
            UControlEntity entity = new UControlEntity();
            decodeControl(control, entity);
            return entity;
        }

        return null;
    }


    /**
     * 返回控制域中的接收序号
     *
     * @param control
     * @return
     */
    private static short decodeAccept(byte[] control) {
        int accept = 0;
        short acceptLow = (short) (control[2] & 0xff);
        short acceptHigh = (short) (control[3] & 0xff);

        accept += acceptLow;
        accept += acceptHigh << 8;
        accept = accept >> 1;
        return (short) accept;
    }

    /**
     * 返回控制域中的发送序号
     *
     * @param control
     * @return
     */
    private static short decodeSend(byte[] control) {
        int send = 0;
        short acceptLow = (short) (control[0] & 0xff);
        short acceptHigh = (short) (control[1] & 0xff);

        send += acceptLow;
        send += acceptHigh << 8;
        send = send >> 1;
        return (short) send;
    }

    /**
     * @param control
     * @return
     */
    private static UControlTypeEnum decodeUControl(byte[] control) {
        if (control.length < 4 || control[1] != 0 || control[3] != 0 || control[2] != 0) {
            return null;
        }
        int controlInt = ValueEncoder.byteArrayToInt(control);
        for (UControlTypeEnum ucontrolEnum : UControlTypeEnum.values()) {
            if (ucontrolEnum.getValue() == controlInt) {
                return ucontrolEnum;
            }
        }

        return null;
    }
}
