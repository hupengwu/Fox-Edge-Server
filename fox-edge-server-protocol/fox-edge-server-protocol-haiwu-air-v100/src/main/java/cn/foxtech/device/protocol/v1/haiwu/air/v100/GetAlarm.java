package cn.foxtech.device.protocol.v1.haiwu.air.v100;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送范例：7e 31 30 30 31 36 30 34 34 30 30 30 30 46 44 42 30 0d
 * 返回范例：7e 31 30 30 31 36 30 30 30 30 30 34 43 30 31 30 30 32 30 32 30 32 30 32 30 32 30 30 30 30 30 30 30 30 30 30 30 31 39 30 30 30 30 30 30 30 30 30 30 32 30 30 30 32 30 30 30 32 30 30 30 30 30 30 30 30 30 32 30 30 30 30 30 30 30 32 30 32 30 30 30 30 30 30 30 32 30 32 30 45 46 33 43 0d
 */
@FoxEdgeDeviceType(value = "基站空调(V1.00)", manufacturer = "广东海悟科技有限公司")
public class GetAlarm {
    @FoxEdgeOperate(name = "获取告警状态", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
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
        pduEntity.setCid2(0x44);

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
    @FoxEdgeOperate(name = "获取告警状态", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCid1() != 0x60) {
            throw new ProtocolException("返回的CID1不正确!");
        }


        Map<String, Object> result = new HashMap<>();
        result.put("rtn", entity.getCid2());

        byte[] data = entity.getData();
        if (data.length != 38) {
            throw new ProtocolException("数据长度不正确");
        }

        int index = 0;
        int value;


        index++;

//        A相交流电压
        putValue("A相交流电压", data[index++], result);
//        B相交流电压
        putValue("B相交流电压", data[index++], result);
//        C相交流电压
        putValue("C相交流电压", data[index++], result);
//        A相交流电流
        putValue("A相交流电流", data[index++], result);
//        B相交流电流
        putValue("B相交流电流", data[index++], result);
//        C相交流电流
        putValue("C相交流电流", data[index++], result);
//        回风温度
        putValue("回风温度", data[index++], result);
//        回风湿度
        putValue("回风湿度", data[index++], result);
//        过滤网保护
        putValue("过滤网保护", data[index++], result);
//        压缩机保护
        putValue("压缩机保护", data[index++], result);
//        风机保护
        putValue("风机保护", data[index++], result);
//        用户自定义状态数量
        result.put("用户自定义状态数量", data[index++]);
//        高压保护
        putValue("高压保护", data[index++], result);
//        低压保护
        putValue("低压保护", data[index++], result);
//        排气温度保护
        putValue("排气温度保护", data[index++], result);
//        内外机通讯故障
        putValue("内外机通讯故障", data[index++], result);
//        室内环境温度传感器
        putValue("室内环境温度传感器", data[index++], result);
//        内机盘管进口温度传感器
        putValue("内机盘管进口温度传感器", data[index++], result);
//        内机盘管中间温度传感器
        putValue("内机盘管中间温度传感器", data[index++], result);
//        内机盘管出口温度传感器
        putValue("内机盘管出口温度传感器", data[index++], result);
//        室外环境温度传感器
        putValue("室外环境温度传感器", data[index++], result);
//        预留
        putValue("预留", data[index++], result);
//        外机盘管温度传感器
        putValue("外机盘管温度传感器", data[index++], result);
//        排气温度传感器
        putValue("排气温度传感器", data[index++], result);
//        相序错误
        putValue("相序错误", data[index++], result);
//        缺相保护
        putValue("缺相保护", data[index++], result);
//        室外风机保护
        putValue("室外风机保护", data[index++], result);
//        EEPROM故障
        putValue("EEPROM故障", data[index++], result);
//        消防报警
        putValue("消防报警", data[index++], result);
//        室内湿度传感器
        putValue("室内湿度传感器", data[index++], result);
//        室外湿度传感器
        putValue("室外湿度传感器", data[index++], result);
//        系统异常
        putValue("系统异常", data[index++], result);
//        水浸告警
        putValue("水浸告警", data[index++], result);
//        其他不正常告警
        putValue("其他不正常告警", data[index++], result);
//        外机被盗告警
        putValue("外机被盗告警", data[index++], result);
//        预留
        putValue("预留", data[index++], result);

        return result;
    }

    private static void putValue(String key, byte value, Map<String, Object> result) {
        switch (value) {
            case 0x00:
                result.put(key, "正常");
                break;
            case 0x01:
                result.put(key, "低于下限");
                break;
            case 0x02:
                result.put(key, "高于上限");
                break;
            case (byte) 0xF0:
                result.put(key, "故障");
                break;
        }
    }
}