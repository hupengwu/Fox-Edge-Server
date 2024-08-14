/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.midea.dcm4;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.midea.dcm4.entity.PduEntity;
import cn.foxtech.device.protocol.v1.midea.dcm4.uitls.AddrUtils;
import cn.foxtech.device.protocol.v1.midea.dcm4.uitls.BitValueUtils;
import cn.foxtech.device.protocol.v1.midea.dcm4.uitls.SettingsUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送范例：aa a0 a1 c2 b1 00 00 00 00 00 00 00 4c 55
 * 返回范例：aa a1 a0 c2 b1 80 18 02 19 00 00 00 99 55
 */
@FoxEdgeDeviceType(value = "基站空调(DCM4)", manufacturer = "美的集团股份有限公司")
public class GetStatus {
    @FoxEdgeOperate(name = "查询空调状态", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
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
        pduEntity.setCmd(0xC2);
        byte[] data = pduEntity.getData();

        data[0] = AddrUtils.encode(devAddr.intValue());

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
    @FoxEdgeOperate(name = "查询空调状态", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCmd() != 0xC2) {
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

        int value = data[3] & 0xff;
        BitValueUtils.decodeBitValue(value, 0, "开", "关", "压缩机状态", result);
        BitValueUtils.decodeBitValue(value, 1, "开", "关", "室内风机状态", result);
        BitValueUtils.decodeBitValue(value, 2, "开", "关", "室外风机状态", result);
        BitValueUtils.decodeBitValue(value, 3, "开", "关", "四通阀状态", result);
        BitValueUtils.decodeBitValue(value, 4, "开", "关", "新风门状态", result);
        BitValueUtils.decodeBitValue(value, 5, "开", "关", "电辅热负载状态", result);
        BitValueUtils.decodeBitValue(value, 6, "开", "关", "电辅热功能状态", result);
        BitValueUtils.decodeBitValue(value, 7, "开", "关", "摇摆功能状态", result);

        result.put("空调T1温度", data[4] & 0xff);

        value = data[5] & 0xff;
        BitValueUtils.decodeBitValue(value, 0, "故障", "正常", "压缩机相序反接故障", result);
        BitValueUtils.decodeBitValue(value, 1, "故障", "正常", "压缩机缺相故障", result);
        BitValueUtils.decodeBitValue(value, 2, "故障", "正常", "室内外通信故障", result);
        BitValueUtils.decodeBitValue(value, 3, "故障", "正常", "排气温度传感器故障", result);
        BitValueUtils.decodeBitValue(value, 4, "故障", "正常", "T4传感器故障", result);
        BitValueUtils.decodeBitValue(value, 5, "故障", "正常", "T3传感器故障", result);
        BitValueUtils.decodeBitValue(value, 6, "故障", "正常", "T2传感器故障", result);
        BitValueUtils.decodeBitValue(value, 7, "故障", "正常", "T1传感器故障", result);

        value = data[6] & 0xff;
        BitValueUtils.decodeBitValue(value, 0, "故障", "正常", "EEPROM参数错误", result);
        BitValueUtils.decodeBitValue(value, 1, "故障", "正常", "压缩机电流过载故障", result);
        BitValueUtils.decodeBitValue(value, 2, "故障", "正常", "室内风机失速", result);
        BitValueUtils.decodeBitValue(value, 3, "故障", "正常", "过零检测出错", result);
        BitValueUtils.decodeBitValue(value, 4, "故障", "正常", "压缩机高压故障", result);
        BitValueUtils.decodeBitValue(value, 5, "故障", "正常", "压缩机低压故障", result);
        BitValueUtils.decodeBitValue(value, 6, "故障", "正常", "室外排气温度过高关压缩机故障", result);
        BitValueUtils.decodeBitValue(value, 7, "故障", "正常", "室外故障", result);

        value = data[7] & 0xff;
        BitValueUtils.decodeBitValue(value, 0, "故障", "正常", "NFM模块EEPROM读写故障", result);
        BitValueUtils.decodeBitValue(value, 1, "故障", "正常", "NFM与空调通信故障", result);
        BitValueUtils.decodeBitValue(value, 2, "故障", "正常", "新风门故障", result);
        BitValueUtils.decodeBitValue(value, 3, "故障", "正常", "显示板和驱动板通讯故障", result);
        BitValueUtils.decodeBitValue(value, 4, "故障", "正常", "室外风机失速", result);
        BitValueUtils.decodeBitValue(value, 5, "故障", "正常", "电压超限保护", result);
        BitValueUtils.decodeBitValue(value, 6, "故障", "正常", "主从机连接故障", result);
        BitValueUtils.decodeBitValue(value, 7, "故障", "正常", "集控器与NFM模块通信故障", result);


        return result;
    }


}