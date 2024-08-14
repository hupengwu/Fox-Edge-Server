/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.haiwu.air.v200;


import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.haiwu.air.v200.enums.Type;
import cn.foxtech.device.protocol.v1.haiwu.air.v200.uitls.TypeValueUtils;
import cn.foxtech.device.protocol.v1.haiwu.air.v200.uitls.ValueUtils;
import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送范例：7e 31 30 30 31 36 30 34 37 30 30 30 30 46 44 41 44 0d
 * 返回范例：7e 31 30 30 31 36 30 30 30 41 30 34 32 32 30 32 30 32 30 32 30 30 30 32 33 30 30 30 30 30 30 35 41 32 30 32 30 30 30 31 39 30 39 30 30 30 31 30 30 30 33 32 30 32 30 30 30 30 30 30 30 31 38 30 30 32 32 30 30 30 30 30 30 30 32 30 30 30 32 46 30 45 45 0d
 */
@FoxEdgeDeviceType(value = "基站空调(V2.00)", manufacturer = "广东海悟科技有限公司")
public class GetParam {
    @FoxEdgeOperate(name = "获取系统参数（定点数）", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
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
        pduEntity.setCid2(0x47);

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
    @FoxEdgeOperate(name = "获取系统参数（定点数）", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCid1() != 0x60) {
            throw new ProtocolException("返回的CID1不正确!");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rtn", entity.getCid2());

        byte[] data = entity.getData();
        if (data.length != 33) {
            throw new ProtocolException("数据长度不正确");
        }

        int index = 0;
        int value;

//        2020 空调开机温度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("空调开机温度", value);
        }
//        2020 空调关机温度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("空调关机温度", value);
        }
//        0023 回风温度上限
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("回风温度上限", value);
        }
//        0000 回风温度下限
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("回风温度下限", value);
        }
//        005a 回风湿度上限
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("回风湿度上限", value);
        }
//        2020 回风湿度下限
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("回风湿度下限", value);
        }
//        0019 温度设定值
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("温度设定值", value);
        }
//        09 用户自定义遥测数量
        result.put("用户自定义遥测数量", data[index]);
        index++;
//        0001 运行模式设定
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("运行模式设定", value);
            result.put("运行模式设定-TEXT", TypeValueUtils.getTypeValueText(Type.EC0,value));
        }
//        0003 内风机风速设定
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("内风机风速设定", value);
            result.put("内风机风速设定-TEXT", TypeValueUtils.getTypeValueText(Type.EC1,value));
        }
//        2020 摆风功能设定
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("摆风功能设定", value);
            result.put("摆风功能设定-TEXT", TypeValueUtils.getTypeValueText(Type.EC2,value));
        }
//        0000 屏蔽本地操作
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("屏蔽本地操作", value);
            result.put("屏蔽本地操作-TEXT", TypeValueUtils.getTypeValueText(Type.EC3,value));
        }
//        0018 双机备份切换时间
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("双机备份切换时间", value);
        }
//        0022 高温同开温度设定
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("高温同开温度设定", value);
        }
//        0000 制热模式温度设定值
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("制热模式温度设定值", value);
        }
//        0002 制冷温控精度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("制冷温控精度", value);
        }
//        0002 制热温控精度
        value = ValueUtils.decodeInteger(data, index);
        index += 2;
        if (value != 0x2020) {
            result.put("制热温控精度", value);
        }

        return result;
    }
}