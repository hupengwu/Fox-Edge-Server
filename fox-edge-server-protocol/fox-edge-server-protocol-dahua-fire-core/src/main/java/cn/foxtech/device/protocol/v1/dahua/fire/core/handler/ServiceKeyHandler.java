package cn.foxtech.device.protocol.v1.dahua.fire.core.handler;

import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.AddressUtil;

public class ServiceKeyHandler extends cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler {
    @Override
    public String getServiceKey(byte[] pdu) {
        String srcAddr = AddressUtil.decodeAddress6byte(pdu, 12);
        return srcAddr;
    }
}
