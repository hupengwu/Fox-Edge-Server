/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.iec104.core.builder;

import cn.foxtech.device.protocol.v1.iec104.core.encoder.MessageUtils;
import cn.foxtech.device.protocol.v1.iec104.core.encoder.ValueEncoder;
import cn.foxtech.device.protocol.v1.iec104.core.entity.*;
import cn.foxtech.device.protocol.v1.iec104.core.enums.FrameTypeEnum;
import cn.foxtech.device.protocol.v1.iec104.core.enums.UControlTypeEnum;
import cn.foxtech.device.protocol.v1.iec104.core.vo.ApduVO;

public class ApduVOBuilder {
    /**
     * 转换实体对象
     *
     * @param apduVO apduVO
     * @return ApduEntity
     */
    public static ApduEntity buildEntity(ApduVO apduVO) {
        ApduEntity apduEntity = new ApduEntity();

        ControlEntity controlVO = buildControlEntity(apduVO.getControl());
        apduEntity.setControl(controlVO);

        AsduEntity asduEntity = buildAsduEntity(apduVO.getAsdu());
        apduEntity.setAsdu(asduEntity);

        return apduEntity;
    }

    /**
     * 生成VO
     *
     * @param apduEntity 实体
     * @return VO
     */
    public static ApduVO buildVO(ApduEntity apduEntity) {
        ApduVO apduVO = new ApduVO();

        ApduVO.ControlVO controlVO = buildControlVO(apduEntity.getControl());
        apduVO.setControl(controlVO);

        ApduVO.AsduVO asduVO = buildAsduVO(apduEntity.getAsdu());
        apduVO.setAsdu(asduVO);

        return apduVO;
    }

    /**
     * 生成控制实体
     *
     * @param controlVO VO
     * @return 实体
     */
    private static ControlEntity buildControlEntity(ApduVO.ControlVO controlVO) {
        if (FrameTypeEnum.I_FORMAT.name().equals(controlVO.getType())) {
            IControlEntity controlEntity = new IControlEntity();

            controlEntity.setAccept(controlVO.getAccept());
            controlEntity.setSend(controlVO.getSend());

            return controlEntity;
        }

        if (FrameTypeEnum.S_FORMAT.name().equals(controlVO.getType())) {
            SControlEntity controlEntity = new SControlEntity();

            controlEntity.setAccept(controlVO.getAccept());

            return controlEntity;
        }

        if (FrameTypeEnum.U_FORMAT.name().equals(controlVO.getType())) {
            UControlEntity controlEntity = new UControlEntity();

            UControlTypeEnum value = UControlTypeEnum.valueOf(controlVO.getCmd());
            controlEntity.setValue(value);

            return controlEntity;
        }

        return null;
    }

    /**
     * 生成VO
     *
     * @param controlEntity 实体
     * @return VO
     */
    private static ApduVO.ControlVO buildControlVO(ControlEntity controlEntity) {
        ApduVO.ControlVO controlVO = new ApduVO.ControlVO();
        if (controlEntity instanceof IControlEntity) {
            controlVO.setType(FrameTypeEnum.I_FORMAT.name());
            controlVO.setAccept(((IControlEntity) controlEntity).getAccept());
            controlVO.setSend(((IControlEntity) controlEntity).getSend());
        }
        if (controlEntity instanceof UControlEntity) {
            controlVO.setType(FrameTypeEnum.U_FORMAT.name());
            controlVO.setCmd(((UControlEntity) controlEntity).getValue().name());
        }
        if (controlEntity instanceof SControlEntity) {
            controlVO.setType(FrameTypeEnum.S_FORMAT.name());
            controlVO.setAccept(((SControlEntity) controlEntity).getAccept());
        }

        return controlVO;
    }

    /**
     * 生成实体
     *
     * @param asduVO VO
     * @return 实体
     */
    private static AsduEntity buildAsduEntity(ApduVO.AsduVO asduVO) {
        if (asduVO == null) {
            return null;
        }

        AsduEntity asduEntity = new AsduEntity();
        asduEntity.setData(ValueEncoder.hexStringToBytes(asduVO.getData()));
        asduEntity.setCommonAddress(asduVO.getCommonAddress());
        asduEntity.setTypeId(asduVO.getTypeId());
        asduEntity.setVsq(buildVsqEntity(asduVO.getVsq()));
        asduEntity.setCot(buildCotEntity(asduVO.getCot()));

        return asduEntity;
    }

    /**
     * 生成VO
     *
     * @param asduEntity 实体
     * @return VO
     */
    private static ApduVO.AsduVO buildAsduVO(AsduEntity asduEntity) {
        if (asduEntity == null) {
            return null;
        }

        ApduVO.AsduVO asduVO = new ApduVO.AsduVO();
        asduVO.setData(ValueEncoder.byteArrayToHexString(asduEntity.getData()));
        asduVO.setCommonAddress(asduEntity.getCommonAddress());
        asduVO.setTypeId(asduEntity.getTypeId());
        asduVO.setVsq(buildVsqVO(asduEntity.getVsq()));
        asduVO.setCot(buildCotVO(asduEntity.getCot()));

        return asduVO;
    }

    /**
     * 生成实体
     *
     * @param vsqVO VO
     * @return 实体
     */
    private static VsqEntity buildVsqEntity(ApduVO.VsqVO vsqVO) {
        VsqEntity vsqEntity = new VsqEntity();
        vsqEntity.setNum(vsqVO.getNum());
        vsqEntity.setSq(vsqVO.isSq());

        return vsqEntity;
    }

    /**
     * 生成VO
     *
     * @param vsqEntity 实体
     * @return VO
     */
    private static ApduVO.VsqVO buildVsqVO(VsqEntity vsqEntity) {
        ApduVO.VsqVO vsqVO = new ApduVO.VsqVO();
        vsqVO.setNum(vsqEntity.getNum());
        vsqVO.setSq(vsqEntity.isSq());

        return vsqVO;
    }

    private static CotEntity buildCotEntity(ApduVO.CotVO cotVO) {
        CotEntity cotEntity = new CotEntity();
        cotEntity.setPn(cotVO.isPn());
        cotEntity.setTest(cotVO.isTest());
        cotEntity.setAddr(cotVO.getAddr());
        cotEntity.setReason(cotVO.getReason());

        return cotEntity;
    }

    private static ApduVO.CotVO buildCotVO(CotEntity cotEntity) {
        ApduVO.CotVO cotVO = new ApduVO.CotVO();
        cotVO.setPn(cotEntity.isPn());
        cotVO.setTest(cotEntity.isTest());
        cotVO.setAddr(cotEntity.getAddr());
        cotVO.setReason(cotEntity.getReason());
        cotVO.setReasonMsg(MessageUtils.getReasonMessage(cotEntity.getReason()));

        return cotVO;
    }


}
