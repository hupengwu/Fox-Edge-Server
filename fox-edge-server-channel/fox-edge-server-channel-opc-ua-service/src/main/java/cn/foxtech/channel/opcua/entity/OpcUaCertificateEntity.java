package cn.foxtech.channel.opcua.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OpcUaCertificateEntity {
    private String stateName;

    private String countryCode;

    private String dnsName;

    private String ipAddress;

    private String path;

    private String file;

    private String alias;

    private String commonName;

    private String localityName;

    private String organization;

    private String keystorePassword;

    private String organizationUnit;
}