package cn.foxtech.device.protocol.v1.midea.dcm9;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送范例：7e 31 30 30 31 36 30 34 33 30 30 30 30 46 44 42 31 0d
 * 返回范例：7e 31 30 30 31 36 30 30 30 37 30 31 38 31 30 30 30 30 39 30 31 30 30 32 30 30 30 30 30 32 30 30 30 32 30 32 30 46 39 31 35 0d
 */
@FoxEdgeDeviceType(value = "基站空调(V2.00)", manufacturer = "美的集团股份有限公司")
public class GetSwitch {
    @FoxEdgeOperate(name = "获取开关状态", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
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
        pduEntity.setCid2(0x43);

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
    @FoxEdgeOperate(name = "获取开关状态", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCid1() != 0x60) {
            throw new ProtocolException("返回的CID1不正确!");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rtn", entity.getCid2());

        byte[] data = entity.getData();
        if (data.length != 12) {
            throw new ProtocolException("数据长度不正确");
        }

        String txt = HexUtils.byteArrayToHexString(data, true);

        int index = 0;
        int value;

//        10
        index++;

//        00 空调状态
        value = data[index++];
        if (value != 0x20) {
            result.put("空调状态", value == 0 ? "关机" : "开机");
        }
//        09 用户自定义状态数量
        value = data[index++];
        result.put("用户自定义状态数量", value);
//        01 空调运行模式
        value = data[index++];
        if (value != 0x20) {
            switch (value) {
                case 0x00:
                    result.put("空调运行模式", "自动");
                    break;
                case 0x01:
                    result.put("空调运行模式", "制冷");
                    break;
                case 0x02:
                    result.put("空调运行模式", "除湿");
                    break;
                case 0x03:
                    result.put("空调运行模式", "送风");
                    break;
                case 0x04:
                    result.put("空调运行模式", "制热");
                    break;
            }
        }
//        00 内风机状态
        value = data[index++];
        if (value != 0x20) {
            switch (value) {
                case 0x00:
                    result.put("内风机状态", "停");
                    break;
                case 0x01:
                    result.put("内风机状态", "低风");
                    break;
                case 0x02:
                    result.put("内风机状态", "中风");
                    break;
                case 0x03:
                    result.put("内风机状态", "高风");
                    break;
            }
        }
//        20 四通阀状态
        value = data[index++];
        if (value != 0x20) {
            result.put("四通阀状态", value == 0 ? "停止" : "运转");
        }
//        00 压缩机状态
        value = data[index++];
        if (value != 0x20) {
            result.put("压缩机状态", value == 0 ? "停止" : "运转");
        }
//        00 外风机状态
        value = data[index++];
        if (value != 0x20) {
            switch (value) {
                case 0x00:
                    result.put("内风机状态", "停");
                    break;
                case 0x01:
                    result.put("内风机状态", "低风");
                    break;
                case 0x02:
                    result.put("内风机状态", "中风");
                    break;
                case 0x03:
                    result.put("内风机状态", "高风");
                    break;
            }
        }
//        20 摆风状态
        value = data[index++];
        if (value != 0x20) {
            result.put("压缩机状态", value == 0 ? "停止" : "运转");
        }
//        00 电加热状态
        value = data[index++];
        if (value != 0x20) {
            result.put("压缩机状态", value == 0 ? "停止" : "运转");
        }
//        20
//        20

        return result;
    }
}