package cn.foxtech.device.simulator.mqtt.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class CoapConfigRes {
    private String resource = "";
    private int mediaType = 0;
}
