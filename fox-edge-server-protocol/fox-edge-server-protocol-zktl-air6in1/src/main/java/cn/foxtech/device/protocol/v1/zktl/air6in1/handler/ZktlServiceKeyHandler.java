package cn.foxtech.device.protocol.v1.zktl.air6in1.handler;

import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.zktl.air6in1.encoder.Encoder;
import cn.foxtech.device.protocol.v1.zktl.air6in1.entity.ZktlDataEntity;

public class ZktlServiceKeyHandler extends ServiceKeyHandler {
    @Override
    public String getServiceKey(byte[] pdu) {
        ZktlDataEntity entity = Encoder.decodeDataEntity(pdu);
        return entity.getServiceKey();
    }
}
