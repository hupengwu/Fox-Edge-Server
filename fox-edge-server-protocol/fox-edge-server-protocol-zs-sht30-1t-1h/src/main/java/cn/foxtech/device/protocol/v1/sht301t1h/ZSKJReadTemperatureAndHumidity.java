/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.sht301t1h;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusConstants;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusProtocol;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusProtocolFactory;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusReadRegistersRespond;

import java.util.HashMap;
import java.util.Map;

/**
 * 读取Registers
 */
@FoxEdgeDeviceType(value = "温湿度采集模块(ZS-SHT30-1T-1H-485)", manufacturer = "中盛科技")
public class ZSKJReadTemperatureAndHumidity {
    /**
     * 读取保持寄存器
     *
     * @param param 必须包含device_addr和modbus_holding_registers_template两个输入参数
     * @return 16进制文本格式的报文
     */
    @FoxEdgeOperate(name = "读取温湿度", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packReadInputRegister(Map<String, Object> param) {
        return (String) operateReadRegister("", param);
    }

    /**
     * 解码保持寄存器
     *
     * @param hexString 16进制文本格式的报文
     * @param param     必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeOperate(name = "读取温湿度", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unpackReadInputRegister(String hexString, Map<String, Object> param) {
        return (Map<String, Object>) operateReadRegister(hexString, param);
    }

    /**
     * 编码/解码 读取保持寄存器
     *
     * @param hexString 解码报文：如果它为空，那么为编码操作，如果不为空，那么为编码操作
     * @param param     参数表
     * @return 编码为String，解码为Map<String, Object>
     */
    private static Object operateReadRegister(String hexString, Map<String, Object> param) {
        // 取出设备地址
        Integer devAddr = (Integer) param.get("devAddr");
        String modbusMode = (String) param.get("modbusMode");


        // 检查输入参数
        if (MethodUtils.hasEmpty(devAddr, modbusMode)) {
            throw new ProtocolException("输入参数不能为空:devAddr, modbusMode");
        }


        // 准备参数
        param.put(ModBusConstants.ADDR, devAddr);
        param.put(ModBusConstants.REG_ADDR, 0);
        param.put(ModBusConstants.REG_CNT, 6);
        param.put(ModBusConstants.MODE, modbusMode);

        // 选择对应的协议类型
        ModBusProtocol protocol = ModBusProtocolFactory.createProtocol(modbusMode);

        // 编码处理
        if (hexString.isEmpty()) {
            byte[] pack = protocol.packCmdReadRegisters4Map((byte) 0x04, param);
            return HexUtils.byteArrayToHexString(pack);
        }

        // 解码处理

        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);
        // 报文解析
        ModBusReadRegistersRespond respond = protocol.unPackCmdReadInputRegisters2Respond(arrCmd);
        if (respond == null) {
            throw new ProtocolException("报文格式不正确，解析失败！");
        }

        Map<String, Object> value = new HashMap<>();
        if (respond.getStatus().length < 2) {
            return value;
        }

        int status = respond.getStatus()[0];
        if (status < 10000) {
            value.put("温度", status * 0.1);
        } else {
            value.put("温度", (status - 10000) * -0.1);
        }

        status = respond.getStatus()[1];
        value.put("湿度", status * 0.1);

        return value;
    }
}
