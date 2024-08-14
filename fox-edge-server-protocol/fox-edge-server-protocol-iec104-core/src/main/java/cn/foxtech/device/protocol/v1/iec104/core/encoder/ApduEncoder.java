/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.iec104.core.encoder;

import cn.foxtech.device.protocol.v1.iec104.core.entity.*;
import cn.foxtech.device.protocol.v1.iec104.core.entity.*;
import cn.foxtech.device.protocol.v1.iec104.core.enums.FrameTypeEnum;
import cn.foxtech.device.protocol.v1.iec104.core.enums.UControlTypeEnum;

import java.util.Set;

/**
 * APDU的编码/解码器
 * I帧为信息帧：为长帧，大于6字节长度，用于传输数据
 * S帧为确认帧，为短帧，6字节长度，用于确认接收的I帧
 * U帧为控制帧，为短帧，6字节长度，用于控制启动/停止/测试
 */
public class ApduEncoder {
    /**
     * 编码APDU报文
     *
     * @param apdu apdu实体
     * @return 报文
     * @throws Exception 异常信息
     */
    public static byte[] encodeApdu(ApduEntity apdu) throws Exception {
        int length = 0;
        if (apdu.getControl() != null && apdu.getAsdu() == null) {
            // S帧和U帧
            length = 6;
        } else if (apdu.getControl() != null && apdu.getAsdu() != null) {
            // I帧
            length = 6 + 6 + apdu.getAsdu().getData().length;
        } else {
            throw new Exception("APCI不能为null");
        }


        byte[] pdu = new byte[length];
        // 启动字符
        pdu[0] = 0x68;
        // 报文长度
        pdu[1] = ((byte) (length - 2));
        // 控制域
        byte[] control = ControlEncoder.encodeControl(apdu.getControl());
        pdu[2] = control[0];
        pdu[3] = control[1];
        pdu[4] = control[2];
        pdu[5] = control[3];

        // 判定是否有ASDU
        if (apdu.getAsdu() == null) {
            return pdu;
        }

        AsduEntity asdu = apdu.getAsdu();
        // 类型标识
        pdu[6] = (byte) asdu.getTypeId();
        // 可变结构限定词
        pdu[7] = (byte) VsqEncoder.encodeVsq(asdu.getVsq());
        // 传送原因
        int cot = CotEncoder.encodeCot(asdu.getCot());
        pdu[8] = (byte) (cot & 0xff);
        pdu[9] = (byte) ((cot & 0xff00) >> 8);
        // 公共地址
        pdu[10] = (byte) (asdu.getCommonAddress() & 0xff);
        pdu[11] = (byte) ((asdu.getCommonAddress() & 0xff00) >> 8);
        // 信息体
        System.arraycopy(asdu.getData(), 0, pdu, 12, asdu.getData().length);

        return pdu;
    }

    /**
     * 解码APDU报文
     *
     * @param pdu pdu报文
     * @return APDU实体
     * @throws Exception 异常信息
     */
    public static ApduEntity decodeApdu(byte[] pdu) throws Exception {
        if (pdu.length < 6) {
            throw new Exception("帧长度小于6");
        }

        // 启动字符
        if ((pdu[0] & 0xff) != 0x68) {
            throw new Exception("启动字符必须为0x68");
        }
        if ((pdu[0] & 0xff) != 0x68) {
            throw new Exception("启动字符必须为0x68");
        }
        // 报文长度
        if (pdu.length != (pdu[1] & 0xff) + 2) {
            throw new Exception("报文长度不正确！");
        }

        // 控制域
        byte[] control = new byte[4];
        control[0] = pdu[2];
        control[1] = pdu[3];
        control[2] = pdu[4];
        control[3] = pdu[5];

        FrameTypeEnum formatType = identifyFormatType(pdu);
        if (FrameTypeEnum.ERR_FORMAT.equals(formatType)) {
            throw new Exception("报文格式非法！");
        }

        // 初始化结构
        ApduEntity apdu = new ApduEntity();
        if (FrameTypeEnum.I_FORMAT.equals(formatType)) {
            if (pdu.length <= 6) {
                throw new Exception("报文长度不正确！");
            }

            IControlEntity controlEntity = new IControlEntity();
            ControlEncoder.decodeControl(control, controlEntity);


            apdu.setControl(controlEntity);
            apdu.setAsdu(new AsduEntity());
        }
        if (FrameTypeEnum.S_FORMAT.equals(formatType)) {
            if (pdu.length != 6) {
                throw new Exception("报文长度不正确！");
            }

            SControlEntity controlEntity = new SControlEntity();
            ControlEncoder.decodeControl(control, controlEntity);

            apdu.setControl(controlEntity);
            apdu.setAsdu(null);
        }
        if (FrameTypeEnum.U_FORMAT.equals(formatType)) {
            if (pdu.length != 6) {
                throw new Exception("报文长度不正确！");
            }

            UControlEntity controlEntity = new UControlEntity();
            ControlEncoder.decodeControl(control, controlEntity);

            apdu.setControl(controlEntity);
            apdu.setAsdu(null);
        }


        // 判定是否有ASDU
        if (!FrameTypeEnum.I_FORMAT.equals(formatType)) {
            return apdu;
        }


        // ASDU的解码
        AsduEntity asdu = apdu.getAsdu();
        // 类型标识
        asdu.setTypeId(pdu[6] & 0xff);
        // 可变结构限定词
        VsqEntity vsq = VsqEncoder.decodeVsq(pdu[7] & 0xff);
        asdu.setVsq(vsq);
        // 传送原因
        CotEntity cot = CotEncoder.decodeCot((pdu[8] & 0xff) + ((pdu[9] & 0xff) << 8));
        asdu.setCot(cot);
        // 公共地址
        asdu.setCommonAddress((pdu[10] & 0xff) + ((pdu[11] & 0xff) << 8));
        // 信息体
        asdu.setData(new byte[(pdu[1] & 0xff) - 10]);
        System.arraycopy(pdu, 12, asdu.getData(), 0, asdu.getData().length);

        return apdu;
    }

    /**
     * 识别格式
     *
     * @param pdu pdu
     * @return 枚举类型
     */
    public static FrameTypeEnum identifyFormatType(byte[] pdu) {
        // 非法格式：报文长度小于最小长度
        if (pdu.length < 6) {
            return FrameTypeEnum.ERR_FORMAT;
        }
        // 非法格式：报文长度和长度字段不匹配
        if (pdu.length != (pdu[1] & 0xff) + 2) {
            return FrameTypeEnum.ERR_FORMAT;
        }

        // 检查：I帧格式
        if (pdu.length > 6) {
            if (((pdu[2] & 0x01) == 0x00) && ((pdu[4] & 0x01) == 0x00)) {
                return FrameTypeEnum.I_FORMAT;
            }

            return FrameTypeEnum.ERR_FORMAT;
        }

        if (pdu.length == 6) {
            // S帧的特征：样例报文680401000000
            boolean sformat = (pdu[2] == 0x01) && (pdu[3] == 0x00) && ((pdu[4] & 0x01) == 0x00);
            if (sformat == true) {
                return FrameTypeEnum.S_FORMAT;
            }

            // U帧的特征：样例报文68040B000000
            boolean uformat = true;
            Set<Integer> cmds = UControlTypeEnum.getTypes();
            if (!cmds.contains(pdu[2] & 0xff)) {
                uformat = false;
            }
            if (pdu[3] != 0x00) {
                uformat = false;
            }
            if (pdu[4] != 0x00) {
                uformat = false;
            }
            if (pdu[5] != 0x00) {
                uformat = false;
            }
            if (uformat == true) {
                return FrameTypeEnum.U_FORMAT;
            }

            return FrameTypeEnum.ERR_FORMAT;
        }


        return FrameTypeEnum.ERR_FORMAT;
    }


}
