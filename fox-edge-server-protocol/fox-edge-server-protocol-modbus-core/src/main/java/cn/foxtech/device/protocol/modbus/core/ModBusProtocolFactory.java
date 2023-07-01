package cn.foxtech.device.protocol.modbus.core;

public class ModBusProtocolFactory {
    /**
     * 创建一个协议处理器
     *
     * @param protocolType
     * @return
     */
    public static ModBusProtocol createProtocol(String protocolType) {
        if (ModBusConstants.MODE_ASCII.equals(protocolType)) {
            return new ModBusAsciiProtocol();
        }
        if (ModBusConstants.MODE_RTU.equals(protocolType)) {
            return new ModBusRtuProtocol();
        }
        if (ModBusConstants.MODE_TCP.equals(protocolType)) {
            return new ModBusTcpProtocol();
        }

        return new ModBusRtuProtocol();
    }
}
