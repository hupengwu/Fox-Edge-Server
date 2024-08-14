/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.midea.dcm4;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.midea.dcm4.entity.PduEntity;
import cn.foxtech.device.protocol.v1.midea.dcm4.uitls.AddrUtils;
import cn.foxtech.device.protocol.v1.midea.dcm4.uitls.SettingsUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送范例：aa a0 a1 c1 b1 80 18 00 00 00 00 00 b5 55
 * 返回范例：aa a1 a0 c1 b1 80 18 00 00 00 00 00 b5 55
 */
@FoxEdgeDeviceType(value = "基站空调(DCM4)", manufacturer = "美的集团股份有限公司")
public class SetParam {
    @FoxEdgeOperate(name = "遥控空调", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String encodePdu(Map<String, Object> param) {
        // 取出设备地址
        Integer devAddr = (Integer) param.get("devAddr");

        // 检查输入参数
        if (MethodUtils.hasEmpty(devAddr)) {
            throw new ProtocolException("输入参数不能为空: devAddr");
        }

        PduEntity pduEntity = new PduEntity();
        pduEntity.setSrcAddr(0xA0);
        pduEntity.setDstAddr(0xA1);
        pduEntity.setCmd(0xC1);
        byte[] data = pduEntity.getData();

        // 地址
        data[0] = AddrUtils.encode(devAddr.intValue());

        // DC2：空调设定模式和设定风速
        data[1] = (byte) SettingsUtils.encodeSettingsMode(param);

        // DC3：空调设定温度
        Object par = param.get("设定温度");
        if (par == null) {
            throw new ProtocolException("参数缺失:设定温度");
        }
        data[2] = ((Integer) par).byteValue();


        byte[] pdu = PduEntity.encodePdu(pduEntity);


        return HexUtils.byteArrayToHexString(pdu);
    }

    /**
     * 获得系统参数
     * 备注：0x2020 代表该数据尚未设置
     *
     * @param hexString 返回内容
     * @param param     辅助参数
     * @return 解码内容
     */
    @FoxEdgeOperate(name = "遥控空调", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCmd() != 0xC1) {
            throw new ProtocolException("返回的命令字不正确!");
        }

        Map<String, Object> result = new HashMap<>();

        byte[] data = entity.getData();
        if (data.length != 8) {
            throw new ProtocolException("数据长度不正确");
        }

        // 空调设定模式和设定风速
        SettingsUtils.decodeSettingsMode(data[1], result);

        result.put("设定温度", data[2] & 0xff);

        return result;
    }


}