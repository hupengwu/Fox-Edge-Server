/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.haiwu.air.v100;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.haiwu.air.v100.uitls.ValueUtils;
import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送范例：7e 31 30 30 31 36 30 34 32 30 30 30 30 46 44 42 32 0d
 * 返回范例：7e 31 30 30 31 36 30 30 30 32 30 34 41 30 30 45 38 32 30 32 30 32 30 32 30 30 30 30 30 32 30 32 30 32 30 32 30 32 30 32 30 30 30 31 42 32 30 32 30 32 30 32 30 32 30 32 30 32 30 32 30 30 36 2d 2d 2d 2d 30 30 31 41 32 30 32 30 30 30 31 42 30 35 39 46 32 42 31 30 45 46 31 31 0d
 */
@FoxEdgeDeviceType(value = "基站空调(V1.00)", manufacturer = "广东海悟科技有限公司")
public class GetAnalog {
    @FoxEdgeOperate(name = "获取模拟量（定点数）", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String encodePdu(Map<String, Object> param) {
        // 取出设备地址
        Integer devAddr = (Integer) param.get("devAddr");

        // 检查输入参数
        if (MethodUtils.hasEmpty(devAddr)) {
            throw new ProtocolException("输入参数不能为空:devAddr");
        }

        PduEntity pduEntity = new PduEntity();
        pduEntity.setAddr(devAddr);
        pduEntity.setVer(0x10);
        pduEntity.setCid1(0x60);
        pduEntity.setCid2(0x42);

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
    @FoxEdgeOperate(name = "获取模拟量（定点数）", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        // 海悟对电信总局的数据编码，理解有偏差，引入一个编码0x2D表示无效数据场景，
        // 传感器离线或故障时，返回“----”，传送字节为2DH，2DH，2DH，2DH。
        PduEntity entity = PduEntity.decodePdu(pdu, false, (byte) 0x2D);

        if (entity.getCid1() != 0x60) {
            throw new ProtocolException("返回的CID1不正确!");
        }


        Map<String, Object> result = new HashMap<>();
        result.put("rtn", entity.getCid2());

        byte[] data = entity.getData();
        if (data.length != 37) {
            throw new ProtocolException("数据长度不正确");
        }

        int index = 0;
        int value;


//        00 e8 主机电源相电压A
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("主机电源相电压A", value);
        }
//        20 20 主机电源相电压B
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("主机电源相电压B", value);
        }
//        20 20 主机电源相电压C
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("主机电源相电压C", value);
        }
//        00 00 主机/压缩机工作电流A相
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("主机/压缩机工作电流A相", value);
        }
//        20 20 主机/压缩机工作电流B相
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("主机/压缩机工作电流B相", value);
        }
//        20 20 主机/压缩机工作电流C相
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("主机/压缩机工作电流C相", value);
        }
//        20 20 送风温度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("送风温度", value);
        }
//        00 1b 回风温度/室内环境温度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("回风温度/室内环境温度", value);
        }
//        20 20 送风湿度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("送风湿度", value);
        }
//        20 20 回风湿度/室内环境湿度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("回风湿度/室内环境湿度", value);
        }
//        20 20 压缩机吸气压力
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("压缩机吸气压力", value);
        }
//        20 20 压缩机排气压力
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("压缩机排气压力", value);
        }
//        06 用户自定义遥测数量
        result.put("用户自定义遥测数量", data[index]);
        index++;
//        2d 2d 室外环境温度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020 && value != 0x2d2d) {
            result.put("室外环境温度", value);
        }
//        00 1a 压缩机排气温度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("压缩机排气温度", value);
        }
//        20 20 室外湿度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("室外湿度", value);
        }
//        00 1b 室内盘管/蒸发器盘管温度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("室内盘管/蒸发器盘管温度", value);
        }
//        05 9f 压缩机运行时间
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("压缩机运行时间", value);
        }
//        2b 10 机组运行时间
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("机组运行时间", value);
        }

        return result;
    }
}