package cn.foxtech.device.protocol.v1.core.method;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeReport;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * 指明函数是否为编码/解码函数，通信超时需要多少，是否被轮询操作进行循环调用
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class FoxEdgeReportMethod extends FoxEdgeBaseMethod {
    /**
     * 解码函数
     */
    private Method decoderMethod;

    /**
     * 模式：状态模式，还是记录模式
     */
    private String mode = FoxEdgeOperate.status;

    /**
     * 类型：alarm/event/log
     */
    private String type = FoxEdgeReport.alarm;
}
