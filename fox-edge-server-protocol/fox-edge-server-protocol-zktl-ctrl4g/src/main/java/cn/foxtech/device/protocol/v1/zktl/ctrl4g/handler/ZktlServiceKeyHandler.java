package cn.foxtech.device.protocol.v1.zktl.ctrl4g.handler;

import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.zktl.ctrl4g.encoder.Encoder;
import cn.foxtech.device.protocol.v1.zktl.ctrl4g.entity.ZktlDataEntity;
import cn.foxtech.device.protocol.v1.zktl.ctrl4g.entity.ZktlPduEntity;

public class ZktlServiceKeyHandler extends ServiceKeyHandler {
    @Override
    public String getServiceKey(byte[] pdu) {
        ZktlPduEntity entity = Encoder.decodePduEntity(pdu);
        return entity.getServiceKey();
    }
}
