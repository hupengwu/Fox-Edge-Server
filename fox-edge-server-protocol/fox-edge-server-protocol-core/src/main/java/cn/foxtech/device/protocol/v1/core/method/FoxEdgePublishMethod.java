package cn.foxtech.device.protocol.v1.core.method;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 指明函数是否为编码/解码函数，通信超时需要多少，是否被轮询操作进行循环调用
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class FoxEdgePublishMethod extends FoxEdgeBaseMethod {
    /**
     * 最大通信超时：写在解码器上
     */
    private Integer timeout = 1000;

    /**
     * 该操作是否需要被轮询调度
     */
    private boolean polling = false;

    /**
     * 编码函数
     */
    private Method encoderMethod;

    /**
     * 参数表
     */
    private Map<String, String> encoderParams = new HashMap<>();
}
