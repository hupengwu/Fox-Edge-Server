package cn.foxtech.device.protocol.core.reference;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class BytesRef {
    private byte[] value = new byte[] {};
}
