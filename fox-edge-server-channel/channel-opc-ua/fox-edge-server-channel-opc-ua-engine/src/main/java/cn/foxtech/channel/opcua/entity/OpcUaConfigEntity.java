package cn.foxtech.channel.opcua.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OpcUaConfigEntity {
    private String endpointUrl;

    private String idpUsername;

    private String idpPassword;

    private String appName;

    private String appUri;

    private OpcUaCertificateEntity certificate;
}
