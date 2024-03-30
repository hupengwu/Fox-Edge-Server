package cn.foxtech.device.protocol.v1.haiwu.air.v200;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送范例：7e 31 30 30 31 36 30 34 44 30 30 30 30 46 44 41 30 0d
 * 返回范例：7e 31 30 30 31 36 30 30 30 32 30 30 45 30 30 30 30 30 30 30 30 30 30 30 30 30 30 46 42 30 31 0d
 */
@FoxEdgeDeviceType(value = "基站空调(V2.00)", manufacturer = "广东海悟科技有限公司")
public class GetTime {
    @FoxEdgeOperate(name = "获取时间", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
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
        pduEntity.setCid2(0x4D);

        byte[] pdu = PduEntity.encodePdu(pduEntity);


        return HexUtils.byteArrayToHexString(pdu);
    }

    @FoxEdgeOperate(name = "获取时间", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCid1() != 0x60) {
            throw new ProtocolException("返回的CID1不正确!");
        }

        byte[] data = entity.getData();
        if (data.length != 7) {
            throw new ProtocolException("返回的数据长度不正确!");
        }

        int year = data[0] & 0xff * 0x100 + data[1] & 0xff;
        int month = data[2] & 0xff;
        int day = data[3] & 0xff;
        int hour = data[4] & 0xff;
        int minute = data[5] & 0xff;
        int second = data[6] & 0xff;


        Map<String, Object> result = new HashMap<>();
        result.put("时间", String.format("%04d-%02d-%02d %02d:%02d:%02d ", year, month, day, hour, minute, second));

        return result;
    }
}