package cn.foxtech.device.protocol.v1.test;


import cn.foxtech.device.protocol.v1.modbus.core.ModBusConstants;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusEntity;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusProtocol;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusProtocolFactory;
import cn.foxtech.device.protocol.v1.utils.Crc16Utils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.enums.CrcType;

public class TestRTU {
    public static void main(String[] args) {
        byte[] pdu = HexUtils.hexStringToByteArray("01 04 0c 01 1a 01 ba 41 e1 fa 9c 42 30 ed cb ef ca");

        int crc = Crc16Utils.getCRC16(pdu, 0, pdu.length - 2, CrcType.CRC16MODBUS);


        byte[] crcs = new byte[2];
        crcs[0] = (byte) (crc >> 8 & 0xff);
        crcs[1] = (byte) (crc >> 0 & 0xff);

        pdu = HexUtils.hexStringToByteArray("01 03 10 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 E4 59");

        pdu = HexUtils.hexStringToByteArray("01 03 00 06 45 21 45 23 45 34 36 21");

        ModBusProtocol modBusProtocol = ModBusProtocolFactory.createProtocol(ModBusConstants.MODE_RTU);
        ModBusEntity modBusEntity = modBusProtocol.unPackCmd2Entity(pdu);




    }


}
