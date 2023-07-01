package cn.foxtech.device.protocol.omron.fins.core.entity.pdu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ConnectRespond {
    private int clientNode = 0;
    private int serverNode = 0;
}
