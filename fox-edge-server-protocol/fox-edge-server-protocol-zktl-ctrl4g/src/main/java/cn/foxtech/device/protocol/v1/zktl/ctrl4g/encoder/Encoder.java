/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.zktl.ctrl4g.encoder;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.AsciiUtils;
import cn.foxtech.device.protocol.v1.zktl.ctrl4g.entity.*;

public class Encoder {
    public static String encodePdu(ZktlConfigEntity entity) {
        return "cmd=" + entity.getCmd().toUpperCase() + "=" + entity.getValue() + "\r\n";
    }

    public static ZktlPduEntity decodePduEntity(String pdu) {
        if (pdu.length() < 33) {
            throw new ProtocolException("报文长度非法");
        }

        // 包头 2323
        if (pdu.charAt(0) != '2' || pdu.charAt(1) != '3' || pdu.charAt(2) != '2' || pdu.charAt(3) != '3') {
            throw new ProtocolException("报头非法");
        }
        // 包尾 aa或者AA
        if (!((pdu.charAt(pdu.length() - 1) == 'a' && pdu.charAt(pdu.length() - 2) == 'a') || (pdu.charAt(pdu.length() - 1) == 'A' && pdu.charAt(pdu.length() - 2) == 'A'))) {
            throw new ProtocolException("报尾非法");
        }

        // 通信类型
        int communType = AsciiUtils.asciiToHex((byte) pdu.charAt(4));
        // 设备类型
        int deviceType = AsciiUtils.asciiToHex((byte) pdu.charAt(5));
        // 报文长度
        int len = Integer.parseInt(pdu.substring(6, 8), 16);
        if (pdu.length() != len + 8) {
            throw new ProtocolException("报文长度非法");
        }
        // 包类型
        int packType = AsciiUtils.asciiToHex((byte) pdu.charAt(8));

        ZktlPduEntity entity = new ZktlPduEntity();
        entity.setCommunType(communType);
        entity.setDeviceType(deviceType);
        entity.setPackType(packType);
        entity.setAddr(pdu.substring(9, 33));
        entity.setData(pdu.substring(33, pdu.length() - 2));
        return entity;
    }

    public static ZktlPduEntity decodePduEntity(byte[] pdu) {
        String str = new String(pdu);
        return decodePduEntity(str);
    }


    public static ZktlPeriodDataEntity decodePeriodDataEntity(String data) {
        if (data.length() != 47) {
            throw new ProtocolException("数据长度不正确!");
        }

        int pos = 0;
        String ICCID = substring(data, pos, 20);
        pos += 20;
        String powerSupplyType = substring(data, pos, 1);
        pos += 1;
        String voltage = substring(data, pos, 2);
        pos += 2;
        String ADC0 = substring(data, pos, 4);
        pos += 4;
        String ADC1 = substring(data, pos, 4);
        pos += 4;
        String relayStatus = substring(data, pos, 2);
        pos += 2;
        String outIO = substring(data, pos, 4);
        pos += 4;
        String signalCSQ = substring(data, pos, 2);
        pos += 2;
        String reserve = substring(data, pos, 2);
        pos += 2;

        ZktlPeriodDataEntity entity = new ZktlPeriodDataEntity();
        entity.setIccid(ICCID);
        entity.setPowerSupplyType(hex2int(powerSupplyType));
        entity.setVoltage(hex2int(voltage));
        entity.setAdc0(hex2int(ADC0));
        entity.setAdc1(hex2int(ADC1));
        entity.setRelayStatus(hex2int(relayStatus));
        entity.setOutIO(hex2int(outIO));
        entity.setSignalCSQ(hex2int(signalCSQ));
        entity.setVoltage(hex2int(voltage));
        entity.setReserve(hex2int(reserve));

        return entity;
    }

    public static ZktlHartDataEntity decodeHartDataEntity(String data) {
        if (data.length() != 6) {
            throw new ProtocolException("数据长度不正确!");
        }

        int pos = 0;
        String reserve = substring(data, pos, 2);
        pos += 2;


        ZktlHartDataEntity entity = new ZktlHartDataEntity();
        entity.setReserve(hex2int(reserve));

        return entity;
    }

    public static ZktlDataEntity decodeDataEntity(byte[] pdu) {
        String str = new String(pdu);
        return decodeDataEntity(str);
    }

    public static ZktlDataEntity decodeDataEntity(String pdu) {
        ZktlPduEntity pduEntity = decodePduEntity(pdu);
        ZktlDataEntity dataEntity = decodeDataEntity(pduEntity.getCommunType(), pduEntity.getData());
        dataEntity.setCommunType(pduEntity.getCommunType());
        dataEntity.setDeviceType(pduEntity.getDeviceType());
        dataEntity.setAddr(pduEntity.getAddr());
        return dataEntity;
    }

    public static ZktlDataEntity decodeDataEntity(int packType, String data) {
        if (packType == 0) {
            return decodeHartDataEntity(data);
        }
        if (packType == 1) {
            return decodePeriodDataEntity(data);
        }

        throw new ProtocolException("未定义的包类型:" + packType);
    }

    private static String substring(String data, int start, int len) {
        return data.substring(start, start + len);
    }

    private static int hex2int(String value) {
        long sum = 0L;
        for (int index = 0; index < value.length(); index++) {
            String cv = "" + value.charAt(index);
            int iv = Integer.parseInt(cv, 16);
            sum *= 0x10;
            sum += iv;
        }

        return (int) sum;
    }

    public static void main(String[] args) {
        ZktlPduEntity entity = Encoder.decodePduEntity("23235121031D467F00053009000a900640000AAAA");
        Encoder.decodeDataEntity(entity.getPackType(), entity.getData());
        entity = Encoder.decodePduEntity("2323514A131D467F00053009000a9006400D467F00053009000a9001D467F00053009000a9002200AA");
        Encoder.decodeDataEntity(entity.getPackType(), entity.getData());
    }


}
