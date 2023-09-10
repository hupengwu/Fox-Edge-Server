package cn.foxtech.device.protocol.v1.cjt188;

import cn.foxtech.device.protocol.v1.cjt188.core.CJT188Address;
import cn.foxtech.device.protocol.v1.cjt188.core.CJT188Entity;
import cn.foxtech.device.protocol.v1.cjt188.core.CJT188ProtocolFrame;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

public class Demo {
    public static void main(String[] args) {
        CJT188Entity entity = new CJT188Entity();
        entity.getType().setValue(0xAA);
        entity.setAddress(new CJT188Address());
        entity.getCtrl().setValue(0x03);
        entity.setData(new byte[3]);
        entity.getData()[0] = (byte) 0x81;
        entity.getData()[1] = (byte) 0x0a;
        entity.getData()[2] = (byte) 0x00;

        byte[] pack = CJT188ProtocolFrame.encodePack(entity);
        String ss = HexUtils.byteArrayToHexString(pack);
        CJT188Entity entity1 = new CJT188Entity();
        entity1 = CJT188ProtocolFrame.decodePack(pack);
        entity1 = CJT188ProtocolFrame.decodePack(HexUtils.hexStringToByteArray("68 10 01 00 00 05 08 00 00 01 03 1F 90 00 39 16"));


        entity1 = CJT188ProtocolFrame.decodePack(HexUtils.hexStringToByteArray("FE FE FE 68 10 01 00 00 05 08 00 00 81 16 90 1F 00 00 23 01 00 2C 00 00 00 00 2C 00 00 00 00 00 00 00 00 00 48 16 "));
        entity1 = CJT188ProtocolFrame.decodePack(HexUtils.hexStringToByteArray("FE FE FE 68 10 18 02 12 20 20 00 00 81 16 90 1F 00 00 02 00 00 2C 00 02 00 00 2C 00 00 00 00 00 00 00 00 FF 85 16"));
        entity1 = CJT188ProtocolFrame.decodePack(HexUtils.hexStringToByteArray("68 10 44 33 22 11 00 33 78 81 16 1F 90 00 00 77 66 55 2C 00 77 66 55 2C 31 01 22 11 05 15 20 21 84 13 16"));
        ss = HexUtils.byteArrayToHexString(pack);

        Map<String,Object> param = new HashMap<>();
        Map<String,Object> value = new HashMap<>();
        param.put("type",0x10);
        param.put("address","01 00 00 05 08 00 00");
        String hex = CJT188ProtocolGetData.encode(param);

        value = CJT188ProtocolGetData.decode("68 10 44 33 22 11 00 33 78 81 16 1F 90 00 00 77 66 55 2C 00 77 66 55 2C 31 01 22 11 05 15 20 21 84 13 16",param);

        value = CJT188ProtocolGetAddress.decode("68 10 01 00 00 05 08 00 00 83 03 81 0A 00 97 16",param);

        value = CJT188ProtocolSetAddress.decode("68 10 02 00 00 05 08 00 00 15 0a a0 18 00 01 00 00 05 08 00 00 6C 16",param);

        value = CJT188ProtocolOperatingValve.decode("FE FE FE 68 10 01 00 00 05 08 00 00 AA 05 A0 17 00 01 FF EC 16",param);

    }
}
