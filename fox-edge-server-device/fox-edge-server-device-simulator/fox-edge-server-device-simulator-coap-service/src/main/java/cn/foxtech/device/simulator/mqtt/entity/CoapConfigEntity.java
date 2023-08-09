package cn.foxtech.device.simulator.mqtt.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class CoapConfigEntity {
    /**
     * 默认的端口
     */
    private int coapPort = 5684;

    /**
     * 格式:byte[],string,hex
     */
    private String returnFormat = "string";

    /**
     * 默认访问超时
     */
    private int timeout = 2000;

    /**
     * 最大客户端并发数量
     */
    private int maxActivePeers = 1000;

    /**
     * 资源
     */
    private List<CoapConfigRes> resources = new ArrayList<>();
}
