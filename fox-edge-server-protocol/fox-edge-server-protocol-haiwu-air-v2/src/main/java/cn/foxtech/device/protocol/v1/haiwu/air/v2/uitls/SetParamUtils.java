package cn.foxtech.device.protocol.v1.haiwu.air.v2.uitls;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;

import java.util.HashMap;
import java.util.Map;

public class SetParamUtils {
    public static byte[] encode(int adr, int type, int value) {
        PduEntity pduEntity = new PduEntity();
        pduEntity.setAddr(adr);
        pduEntity.setVer(0x10);
        pduEntity.setCid1(0x60);
        pduEntity.setCid1(0x49);
        pduEntity.setData(new byte[3]);

        byte[] data = pduEntity.getData();
        data[0] = (byte) type;
        data[1] = (byte) (value >> 8);
        data[2] = (byte) (value & 0xff);


        return PduEntity.encodePdu(pduEntity);
    }

    public static Map<String, Object> decode(byte[] pdu) {
        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCid1() != 0x60) {
            throw new ProtocolException("返回的CID1不正确!");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("return", entity.getCid2());

        return result;
    }
}
