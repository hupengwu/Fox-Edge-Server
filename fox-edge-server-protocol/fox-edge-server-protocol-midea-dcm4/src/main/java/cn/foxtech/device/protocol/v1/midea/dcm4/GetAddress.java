/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.midea.dcm4;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.midea.dcm4.entity.PduEntity;
import cn.foxtech.device.protocol.v1.midea.dcm4.uitls.AddrUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送范例：aa a0 a1 c9 a1 00 00 00 00 00 00 00 55 55
 * 返回范例：aa a1 a0 c9 a1 00 b1 00 b2 00 00 00 f2 55
 */
@FoxEdgeDeviceType(value = "基站空调(DCM4)", manufacturer = "美的集团股份有限公司")
public class GetAddress {
    @FoxEdgeOperate(name = "查询空调地址", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String encodePdu(Map<String, Object> param) {
        PduEntity pduEntity = new PduEntity();
        pduEntity.setSrcAddr(0xA0);
        pduEntity.setDstAddr(0xA1);
        pduEntity.setCmd(0xC9);
        byte[] data = pduEntity.getData();

        data[0] = (byte) 0xA1;


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
    @FoxEdgeOperate(name = "查询空调地址", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCmd() != 0xC9) {
            throw new ProtocolException("返回的命令字不正确!");
        }

        Map<String, Object> result = new HashMap<>();

        byte[] data = entity.getData();
        if (data.length != 8) {
            throw new ProtocolException("数据长度不正确");
        }

        int index = 0;


        result.put("机组1的空调地址", AddrUtils.decode(data[2]));
        result.put("机组2的空调地址", AddrUtils.decode(data[4]));


        return result;
    }
}