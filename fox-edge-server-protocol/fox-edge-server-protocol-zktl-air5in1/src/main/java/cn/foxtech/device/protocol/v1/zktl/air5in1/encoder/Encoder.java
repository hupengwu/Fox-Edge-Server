/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.zktl.air5in1.encoder;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.AsciiUtils;
import cn.foxtech.device.protocol.v1.zktl.air5in1.entity.*;

public class Encoder {
    public static String encodePdu(ZktlConfigEntity entity) {
        return  "setdev=" + entity.getCommunType() + "," + entity.getDeviceType() + "," + entity.getValue() + "AA";
    }

    public static ZktlPduEntity decodePduEntity(String pdu) {
        if (pdu.length() < 8) {
            throw new ProtocolException("报文长度非法");
        }

        // 包头 2424
        if (pdu.charAt(0) != '2' || pdu.charAt(1) != '4' || pdu.charAt(2) != '2' || pdu.charAt(3) != '4') {
            throw new ProtocolException("报头非法");
        }
        // 包尾 aa
        if (pdu.charAt(pdu.length() - 1) != 'A' || pdu.charAt(pdu.length() - 2) != 'A') {
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

        ZktlPduEntity entity = new ZktlPduEntity();
        entity.setCommunType(communType);
        entity.setDeviceType(deviceType);
        entity.setData(pdu.substring(8, pdu.length() - 2));
        return entity;
    }

    public static ZktlPduEntity decodePduEntity(byte[] pdu) {
        String str = new String(pdu);
        return decodePduEntity(str);
    }


    public static ZktlNbDataEntity decodeNbDataEntity(String data) {
        if (data.length() < 54) {
            throw new ProtocolException("数据长度不正确!");
        }

        int pos = 0;
        String IMEI = substring(data, pos, 15);
        pos += 15;
        String ICCID = substring(data, pos, 20);
        pos += 20;
        String tamper = substring(data, pos, 1);
        pos += 1;
        String externalTrigger = substring(data, pos, 1);
        pos += 1;
        String externalSwitch2 = substring(data, pos, 1);
        pos += 1;
        String externalSwitch1 = substring(data, pos, 1);
        pos += 1;
        String packType = substring(data, pos, 1);
        pos += 1;
        String batteryVoltage = substring(data, pos, 4);
        pos += 4;
        String collectData = substring(data, pos, 4);
        pos += 4;
        String signal = substring(data, pos, 2);
        pos += 2;
        String packSn = substring(data, pos, 2);
        pos += 2;
        String reserve = substring(data, pos, 2);
        pos += 2;

        ZktlNbDataEntity entity = new ZktlNbDataEntity();
        entity.setImei(IMEI);
        entity.setIccid(ICCID);

        entity.setCollectData(hex2int(collectData) / 1000.0);
        entity.setBatteryVoltage(hex2int(batteryVoltage) / 1000.0);
        entity.setExternalSwitch1(hex2int(externalSwitch1));
        entity.setExternalSwitch2(hex2int(externalSwitch2));
        entity.setExternalTrigger(hex2int(externalTrigger));
        entity.setPackType(hex2int(packType));
        entity.setTamper(hex2int(tamper));
        entity.setSignal(hex2int(signal) - 110);
        entity.setPackSn(hex2int(packSn));
        entity.setReserve(hex2int(reserve));

        return entity;
    }

    public static ZktlLoRaDataEntity decodeLoRaDataEntity(String data) {
        if (data.length() < 36) {
            throw new ProtocolException("数据长度不正确!");
        }

        int pos = 0;
        String Addr = substring(data, pos, 8);
        pos += 8;
        String tamper = substring(data, pos, 1);
        pos += 1;
        String externalTrigger = substring(data, pos, 1);
        pos += 1;
        String externalSwitch2 = substring(data, pos, 1);
        pos += 1;
        String externalSwitch1 = substring(data, pos, 1);
        pos += 1;
        String packType = substring(data, pos, 1);
        pos += 1;
        String batteryVoltage = substring(data, pos, 4);
        pos += 4;
        String collectData = substring(data, pos, 4);
        pos += 4;
        String signal = substring(data, pos, 2);
        pos += 2;
        String packSn = substring(data, pos, 2);
        pos += 2;
        String reserve = substring(data, pos, 2);
        pos += 2;

        ZktlLoRaDataEntity entity = new ZktlLoRaDataEntity();
        entity.setAddr(Addr);

        entity.setCollectData(hex2int(collectData) / 1000.0);
        entity.setBatteryVoltage(hex2int(batteryVoltage) / 1000.0);
        entity.setExternalSwitch1(hex2int(externalSwitch1));
        entity.setExternalSwitch2(hex2int(externalSwitch2));
        entity.setExternalTrigger(hex2int(externalTrigger));
        entity.setPackType(hex2int(packType));
        entity.setTamper(hex2int(tamper));
        entity.setSignal(hex2int(signal) - 110);
        entity.setPackSn(hex2int(packSn));
        entity.setReserve(hex2int(reserve));

        return entity;
    }

    public static ZktlLoRaWanDataEntity decodeLoRaWanDataEntity(String data) {
        if (data.length() < 28) {
            throw new ProtocolException("数据长度不正确!");
        }

        int pos = 0;
        String tamper = substring(data, pos, 1);
        pos += 1;
        String externalTrigger = substring(data, pos, 1);
        pos += 1;
        String externalSwitch2 = substring(data, pos, 1);
        pos += 1;
        String externalSwitch1 = substring(data, pos, 1);
        pos += 1;
        String packType = substring(data, pos, 1);
        pos += 1;
        String batteryVoltage = substring(data, pos, 4);
        pos += 4;
        String collectData = substring(data, pos, 4);
        pos += 4;
        String signal = substring(data, pos, 2);
        pos += 2;
        String packSn = substring(data, pos, 2);
        pos += 2;
        String reserve = substring(data, pos, 2);
        pos += 2;

        ZktlLoRaWanDataEntity entity = new ZktlLoRaWanDataEntity();

        entity.setCollectData(hex2int(collectData) / 1000.0);
        entity.setBatteryVoltage(hex2int(batteryVoltage) / 1000.0);
        entity.setExternalSwitch1(hex2int(externalSwitch1));
        entity.setExternalSwitch2(hex2int(externalSwitch2));
        entity.setExternalTrigger(hex2int(externalTrigger));
        entity.setPackType(hex2int(packType));
        entity.setTamper(hex2int(tamper));
        entity.setSignal(hex2int(signal) - 110);
        entity.setPackSn(hex2int(packSn));
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
        return dataEntity;
    }

    public static ZktlDataEntity decodeDataEntity(int communType, String data) {
        if (communType == 0) {
            return decodeNbDataEntity(data);
        }
        if (communType == 1) {
            return decodeLoRaDataEntity(data);
        }
        if (communType == 2) {
            return decodeLoRaWanDataEntity(data);
        }

        throw new ProtocolException("未定义的通信类型:" + communType);
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
        ZktlPduEntity entity = Encoder.decodePduEntity("24240838867572058700527898611212450141741910058009b00b30064b60AA");
        Encoder.decodeDataEntity(entity.getCommunType(), entity.getData());
        entity = Encoder.decodePduEntity("24241828131D467F0053009000a9006400bb019a000200AA");
        Encoder.decodeDataEntity(entity.getCommunType(), entity.getData());
        entity = Encoder.decodePduEntity("242428200056008b009c006400bc0194000200AA");
        Encoder.decodeDataEntity(entity.getCommunType(), entity.getData());
        ZktlDataEntity entity1 = Encoder.decodeDataEntity("24240843867572058700527898611212450141741910058009b00b3006400b6019e110200AA");
        String key = entity1.getServiceKey();
    }


}
