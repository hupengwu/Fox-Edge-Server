/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.zktl.electric.encoder;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.AsciiUtils;
import cn.foxtech.device.protocol.v1.zktl.electric.entity.*;

public class Encoder {
    public static String encodePdu(ZktlConfigEntity entity) {
        return "SET" + entity.getCmd().toUpperCase() + "=" + entity.getValue() + "AA";
    }

    public static ZktlPduEntity decodePduEntity(String pdu) {
        if (pdu.length() < 8) {
            throw new ProtocolException("报文长度非法");
        }

        // 包头 2424
        if (pdu.charAt(0) != '2' || pdu.charAt(1) != '4' || pdu.charAt(2) != '2' || pdu.charAt(3) != '4') {
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
        if (data.length() < 124) {
            throw new ProtocolException("数据长度不正确!");
        }

        int pos = 0;
        String IMEI = substring(data, pos, 15);
        pos += 15;
        String ICCID = substring(data, pos, 20);
        pos += 20;
        String packType = substring(data, pos, 1);
        pos += 1;
        String silencerStatus = substring(data, pos, 1);
        pos += 1;
        String voltageStatus = substring(data, pos, 2);
        pos += 2;
        String currentStatus = substring(data, pos, 2);
        pos += 2;
        String tempStatus = substring(data, pos, 2);
        pos += 2;
        // 电压
        String voltageA = substring(data, pos, 2);
        pos += 2;
        String voltageB = substring(data, pos, 2);
        pos += 2;
        String voltageC = substring(data, pos, 2);
        pos += 2;
        // 电流
        String currentA = substring(data, pos, 3);
        pos += 3;
        String currentB = substring(data, pos, 3);
        pos += 3;
        String currentC = substring(data, pos, 3);
        pos += 3;
        String currentLeakage = substring(data, pos, 3);
        // 线温
        pos += 3;
        String lineTempeA = substring(data, pos, 2);
        pos += 2;
        String lineTempeB = substring(data, pos, 2);
        pos += 2;
        String lineTempeC = substring(data, pos, 2);
        pos += 2;
        String boxTemp = substring(data, pos, 2);
        pos += 2;
        // 有功功率
        String activePowerA = substring(data, pos, 3);
        pos += 3;
        String activePowerB = substring(data, pos, 3);
        pos += 3;
        String activePowerC = substring(data, pos, 3);
        pos += 3;
        String activePowerTotal = substring(data, pos, 4);
        pos += 4;
        // 无功功率
        String reactivePowerA = substring(data, pos, 3);
        pos += 3;
        String reactivePowerB = substring(data, pos, 3);
        pos += 3;
        String reactivePowerC = substring(data, pos, 3);
        pos += 3;
        String reactivePowerTotal = substring(data, pos, 4);
        pos += 4;
        // 视在功率
        String apparentPowerA = substring(data, pos, 3);
        pos += 3;
        String apparentPowerB = substring(data, pos, 3);
        pos += 3;
        String apparentPowerC = substring(data, pos, 3);
        pos += 3;
        String apparentPowertotal = substring(data, pos, 4);
        pos += 4;
        // 功率因数
        String powerFactorA = substring(data, pos, 2);
        pos += 2;
        String powerFactorB = substring(data, pos, 2);
        pos += 2;
        String powerFactorC = substring(data, pos, 2);
        pos += 2;
        String powerFactorTotal = substring(data, pos, 2);
        pos += 2;
        // 电网频率
        String gridFrequency = substring(data, pos, 2);
        pos += 2;
        String signal = substring(data, pos, 2);
        pos += 2;
        String packSn = substring(data, pos, 2);
        pos += 2;
        String reserve = substring(data, pos, 2);
        pos += 2;

        ZktlNbDataEntity entity = new ZktlNbDataEntity();
        entity.setImei(IMEI);
        entity.setIccid(ICCID);
        entity.setVoltageA(hex2int(voltageA) + 50);
        entity.setVoltageB(hex2int(voltageB) + 50);
        entity.setVoltageC(hex2int(voltageC) + 50);
        entity.setCurrentA(hex2int(currentA) * 0.2);
        entity.setCurrentB(hex2int(currentB) * 0.2);
        entity.setCurrentC(hex2int(currentC) * 0.2);
        entity.setCurrentLeakage(hex2int(currentLeakage));
        entity.setLineTempeA(hex2int(lineTempeA) - 50);
        entity.setLineTempeB(hex2int(lineTempeB) - 50);
        entity.setLineTempeC(hex2int(lineTempeC) - 50);
        entity.setBoxTemp(hex2int(boxTemp) - 50);
        entity.setActivePowerA(hex2int(activePowerA) * 0.1);
        entity.setActivePowerB(hex2int(activePowerB) * 0.1);
        entity.setActivePowerC(hex2int(activePowerC) * 0.1);
        entity.setActivePowerTotal(hex2int(activePowerTotal) * 0.1);
        entity.setReactivePowerA(hex2int(reactivePowerA) * 0.1);
        entity.setReactivePowerB(hex2int(reactivePowerB) * 0.1);
        entity.setReactivePowerC(hex2int(reactivePowerC) * 0.1);
        entity.setReactivePowerTotal(hex2int(reactivePowerTotal) * 0.1);
        entity.setApparentPowerA(hex2int(apparentPowerA) * 0.1);
        entity.setApparentPowerB(hex2int(apparentPowerB) * 0.1);
        entity.setApparentPowerC(hex2int(apparentPowerC) * 0.1);
        entity.setApparentPowertotal(hex2int(apparentPowertotal) * 0.1);
        entity.setPowerFactorA(hex2int(powerFactorA) * 0.01);
        entity.setPowerFactorB(hex2int(powerFactorB) * 0.01);
        entity.setPowerFactorC(hex2int(powerFactorC) * 0.01);
        entity.setPowerFactorTotal(hex2int(powerFactorTotal) * 0.01);
        entity.setPackType(hex2int(packType));
        entity.setVoltageStatus(hex2int(voltageStatus));
        entity.setSilencerStatus(hex2int(silencerStatus));
        entity.setCurrentStatus(hex2int(currentStatus));
        entity.setTempStatus(hex2int(tempStatus));
        entity.setGridFrequency(hex2int(gridFrequency));
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
        String addr = substring(data, pos, 8);
        pos += 8;
        String packType = substring(data, pos, 1);
        pos += 1;
        String silencerStatus = substring(data, pos, 1);
        pos += 1;
        String voltageStatus = substring(data, pos, 2);
        pos += 2;
        String currentStatus = substring(data, pos, 2);
        pos += 2;
        String tempStatus = substring(data, pos, 2);
        pos += 2;
        // 电压
        String voltageA = substring(data, pos, 2);
        pos += 2;
        String voltageB = substring(data, pos, 2);
        pos += 2;
        String voltageC = substring(data, pos, 2);
        pos += 2;
        // 电流
        String currentA = substring(data, pos, 3);
        pos += 3;
        String currentB = substring(data, pos, 3);
        pos += 3;
        String currentC = substring(data, pos, 3);
        pos += 3;
        String currentLeakage = substring(data, pos, 3);
        // 线温
        pos += 3;
        String lineTempeA = substring(data, pos, 2);
        pos += 2;
        String lineTempeB = substring(data, pos, 2);
        pos += 2;
        String lineTempeC = substring(data, pos, 2);
        pos += 2;
        String boxTemp = substring(data, pos, 2);
        pos += 2;

        // 电网频率
        String gridFrequency = substring(data, pos, 2);
        pos += 2;
        String packSn = substring(data, pos, 2);
        pos += 2;
        String reserve = substring(data, pos, 2);
        pos += 2;

        ZktlLoRaDataEntity entity = new ZktlLoRaDataEntity();
        entity.setAddr(addr);
        entity.setVoltageA(hex2int(voltageA) + 50);
        entity.setVoltageB(hex2int(voltageB) + 50);
        entity.setVoltageC(hex2int(voltageC) + 50);
        entity.setCurrentA(hex2int(currentA) * 0.2);
        entity.setCurrentB(hex2int(currentB) * 0.2);
        entity.setCurrentC(hex2int(currentC) * 0.2);
        entity.setCurrentLeakage(hex2int(currentLeakage));
        entity.setLineTempeA(hex2int(lineTempeA) - 50);
        entity.setLineTempeB(hex2int(lineTempeB) - 50);
        entity.setLineTempeC(hex2int(lineTempeC) - 50);
        entity.setBoxTemp(hex2int(boxTemp) - 50);
        entity.setPackType(hex2int(packType));
        entity.setVoltageStatus(hex2int(voltageStatus));
        entity.setSilencerStatus(hex2int(silencerStatus));
        entity.setCurrentStatus(hex2int(currentStatus));
        entity.setTempStatus(hex2int(tempStatus));
        entity.setGridFrequency(hex2int(gridFrequency));
        entity.setPackSn(hex2int(packSn));
        entity.setReserve(hex2int(reserve));

        return entity;
    }

    public static ZktlLoRaHartDataEntity decodeLoRaHartDataEntity(String data) {
        if (data.length() != 39) {
            throw new ProtocolException("数据长度不正确!");
        }

        int pos = 0;
        String addr = substring(data, pos, 8);
        pos += 8;
        String packType = substring(data, pos, 1);
        pos += 1;
        String PM1p0 = substring(data, pos, 4);
        pos += 4;
        String PM2p5 = substring(data, pos, 4);
        pos += 4;
        String PM10 = substring(data, pos, 4);
        pos += 4;
        String odor = substring(data, pos, 4);
        pos += 4;
        String temp = substring(data, pos, 4);
        pos += 4;
        String hum = substring(data, pos, 4);
        pos += 4;
        String signal = substring(data, pos, 2);
        pos += 2;
        String packSn = substring(data, pos, 2);
        pos += 2;
        String reserve = substring(data, pos, 2);
        pos += 2;

        if (!packType.equals("0")) {
            throw new ProtocolException("包类型不是心跳包！");
        }

        ZktlLoRaHartDataEntity entity = new ZktlLoRaHartDataEntity();
        entity.setAddr(addr);
        entity.setPackType(hex2int(packType));
        entity.setPm1p0(hex2int(PM1p0));
        entity.setPm2p5(hex2int(PM2p5));
        entity.setPm10(hex2int(PM10));
        entity.setOdor(hex2int(odor) / 100.0);
        entity.setTemp(hex2int(temp) / 10.0);
        entity.setHumidity(hex2int(hum) / 10.0);
        entity.setSignal(hex2int(signal) - 110);
        entity.setPackSn(hex2int(packSn));
        entity.setReserve(hex2int(reserve));

        return entity;
    }

    public static ZktlLoRaWanDataEntity decodeLoRaWanDataEntity(String data) {
        if (data.length() < 40) {
            throw new ProtocolException("数据长度不正确!");
        }

        int pos = 0;
        String packType = substring(data, pos, 1);
        pos += 1;
        String silencerStatus = substring(data, pos, 1);
        pos += 1;
        String voltageStatus = substring(data, pos, 2);
        pos += 2;
        String currentStatus = substring(data, pos, 2);
        pos += 2;
        String tempStatus = substring(data, pos, 2);
        pos += 2;
        // 电压
        String voltageA = substring(data, pos, 2);
        pos += 2;
        String voltageB = substring(data, pos, 2);
        pos += 2;
        String voltageC = substring(data, pos, 2);
        pos += 2;
        // 电流
        String currentA = substring(data, pos, 3);
        pos += 3;
        String currentB = substring(data, pos, 3);
        pos += 3;
        String currentC = substring(data, pos, 3);
        pos += 3;
        String currentLeakage = substring(data, pos, 3);
        // 线温
        pos += 3;
        String lineTempeA = substring(data, pos, 2);
        pos += 2;
        String lineTempeB = substring(data, pos, 2);
        pos += 2;
        String lineTempeC = substring(data, pos, 2);
        pos += 2;
        String boxTemp = substring(data, pos, 2);
        pos += 2;

        // 电网频率
        String gridFrequency = substring(data, pos, 2);
        pos += 2;
        String packSn = substring(data, pos, 2);
        pos += 2;
        String reserve = substring(data, pos, 2);
        pos += 2;

        ZktlLoRaWanDataEntity entity = new ZktlLoRaWanDataEntity();

        entity.setVoltageA(hex2int(voltageA) + 50);
        entity.setVoltageB(hex2int(voltageB) + 50);
        entity.setVoltageC(hex2int(voltageC) + 50);
        entity.setCurrentA(hex2int(currentA) * 0.2);
        entity.setCurrentB(hex2int(currentB) * 0.2);
        entity.setCurrentC(hex2int(currentC) * 0.2);
        entity.setCurrentLeakage(hex2int(currentLeakage));
        entity.setLineTempeA(hex2int(lineTempeA) - 50);
        entity.setLineTempeB(hex2int(lineTempeB) - 50);
        entity.setLineTempeC(hex2int(lineTempeC) - 50);
        entity.setBoxTemp(hex2int(boxTemp) - 50);
        entity.setPackType(hex2int(packType));
        entity.setVoltageStatus(hex2int(voltageStatus));
        entity.setSilencerStatus(hex2int(silencerStatus));
        entity.setCurrentStatus(hex2int(currentStatus));
        entity.setTempStatus(hex2int(tempStatus));
        entity.setGridFrequency(hex2int(gridFrequency));
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
            if (data.length() == 39) {
                return decodeLoRaHartDataEntity(data);
            } else {
                return decodeLoRaDataEntity(data);
            }

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
        ZktlPduEntity entity = Encoder.decodePduEntity("2424097E8685910581412428986112121101552282031000000BCBCBC0370370370EF4A4A4B4B01C01C01C0055000000000000001C01C01C0055C700004231130700AA");
        Encoder.decodeDataEntity(entity.getCommunType(), entity.getData());
        entity = Encoder.decodePduEntity("24241829131D467F00053009000a9006400bb019a000200aa");
        Encoder.decodeDataEntity(entity.getCommunType(), entity.getData());
        entity = Encoder.decodePduEntity("2424292A32000000C3C3C300000000000944444545310300AA");
        Encoder.decodeDataEntity(entity.getCommunType(), entity.getData());
        entity = Encoder.decodePduEntity("2424292A00000000BBBBBB07807807802C45444444320100AA");
        Encoder.decodeDataEntity(entity.getCommunType(), entity.getData());

    }


}
