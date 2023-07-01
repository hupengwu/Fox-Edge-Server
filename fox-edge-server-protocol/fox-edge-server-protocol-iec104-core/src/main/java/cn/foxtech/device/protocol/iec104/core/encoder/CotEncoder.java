package cn.foxtech.device.protocol.iec104.core.encoder;

import cn.foxtech.device.protocol.iec104.core.entity.CotEntity;

public class CotEncoder {
    public static CotEntity decodeCot(int value) {
        CotEntity entity = new CotEntity();
        entity.setTest((value & 0x0080) != 0x00);
        entity.setPn((value & 0x0040) == 0x00);
        entity.setReason(value & 0x003f);
        entity.setAddr(value >> 8);
        return entity;
    }

    public static int encodeCot(CotEntity entity) {
        int result = entity.getReason();
        if (entity.isTest()) {
            result |= 0x0080;
        }
        if (!entity.isPn()) {
            result |= 0x0040;
        }
        result |= entity.getAddr() << 8;

        return result;
    }
}
