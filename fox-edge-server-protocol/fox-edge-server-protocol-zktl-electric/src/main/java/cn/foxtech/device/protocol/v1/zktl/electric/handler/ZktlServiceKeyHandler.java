package cn.foxtech.device.protocol.v1.zktl.electric.handler;

import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.zktl.electric.encoder.Encoder;
import cn.foxtech.device.protocol.v1.zktl.electric.entity.ZktlDataEntity;

public class ZktlServiceKeyHandler extends ServiceKeyHandler {
    @Override
    public String getServiceKey(byte[] pdu) {
        ZktlDataEntity entity = Encoder.decodeDataEntity(pdu);
        return entity.getServiceKey();
    }
}
