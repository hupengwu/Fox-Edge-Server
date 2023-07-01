package cn.foxtech.channel.snmp.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.snmp4j.mp.SnmpConstants;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class SnmpEntity {
    /**
     * 本地IP
     */
    private String localIp;
    /**
     * 本地端口
     */
    private Integer localPort = 161;
    /**
     * 设备IP
     */
    private String targetIp;
    /**
     * 设备端口
     */
    private Integer targetPort = 161;
    /**
     * 团体属性
     */
    private String community = "public";
    /**
     * 版本
     */
    private int version = SnmpConstants.version2c;
}
