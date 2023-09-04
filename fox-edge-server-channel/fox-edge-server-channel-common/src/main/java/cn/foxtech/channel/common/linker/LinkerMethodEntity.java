package cn.foxtech.channel.common.linker;


import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeLinkerAction;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 链路层的编码：约定包含下面指定函数名称和格式的类型
 * <p>
 * 创建链路请求的编码:encodeCreateLinkerRequest
 * 创建链路响应的解码:decodeCreateLinkerRespond
 * 链路心跳请求的编码:encodeActiveLinkerRequest
 * 链路心跳响应的编码:decodeActiveLinkerRespond
 */
//@FoxEdgeDeviceType(value = "omron-fins", manufacturer = "欧姆龙")
@Data
public class LinkerMethodEntity {
    public static final String PARAM_KEY = "link-encoder";
    /**
     * 设备类型名称：来自解码器jar包中的@FoxEdgeDeviceType(value = "xxxx")
     */
    private String deviceType;

    /**
     * 制造商
     */
    private String manufacturer;

    /**
     * 创建链路请求的编码：链路建立线程会自动调用它，跟设备建立连接
     */
    private Method encodeCreateLinkerRequest;
    /**
     * 创建链路响应的解码：链路建立线程会自动调用它，跟设备建立连接
     */
    private Method decodeCreateLinkerRespond;
    /**
     * 链路心跳请求的编码：心跳线程会自动调用它，跟设备维持连接
     */
    private Method encodeActiveLinkerRequest;
    /**
     * 链路心跳响应的解码：心跳线程会自动调用它，跟设备维持连接
     */
    private Method decodeActiveLinkerRespond;
    /**
     * 链路断开的解码：ChannelClientAPI会自动拦截设备返回的报文，检查链路的连接/断开状态
     */
    private Method decodeInterceptLinkerRespond;

    /**
     * 创建链路请求的编码
     *
     * @param param Channel中的配置参数
     * @return 发送给设备的编码
     * @throws Exception 异常错误
     */
    public static Object encodeCreateLinkerRequest(Map<String, Object> param) throws Exception {
        return "68 00 00 00 00 00 00 68 01 02 4A F4 11 16";
    }

    /**
     * 创建链路响应的解码
     *
     * @param param   Channel中的配置参数
     * @param respond 从设备返回的编码
     * @return 设备是否告知正确连接上了
     * @throws Exception 异常错误
     */
    public static boolean decodeCreateLinkerRespond(Map<String, Object> param, Object respond) throws Exception {
        return true;
    }

    /**
     * 链路心跳请求的编码
     *
     * @param param Channel中的配置参数
     * @return 发送给设备的编码
     * @throws Exception 异常错误
     */
    public static Object encodeActiveLinkerRequest(Map<String, Object> param) throws Exception {
        return "68 00 00 00 00 00 00 68 01 02 4A F4 11 16 ";
    }

    /**
     * 链路心跳响应的解码
     *
     * @param param   Channel中的配置参数
     * @param respond 从设备返回的编码
     * @return 设备是否告知是否心跳正确
     * @throws Exception 异常错误
     */
    public static boolean decodeActiveLinkerRespond(Map<String, Object> param, Object respond) throws Exception {
        return true;
    }

    /**
     * 拦截响应，判断链路连接状态
     *
     * @param param   Channel中的配置参数
     * @param respond 从设备返回的编码
     * @return 设备的响应中，是否连接正常状态。如果设备的返回中，提醒实际连接已经断开，那么要返回false
     * @throws Exception 异常错误
     */
    public static boolean decodeInterceptLinkerRespond(Map<String, Object> param, Object respond) throws Exception {
        return true;
    }

    public void setMethod(Method method) {
        FoxEdgeLinkerAction annotation = method.getAnnotation(FoxEdgeLinkerAction.class);
        if (annotation == null) {
            return;
        }

        if (FoxEdgeLinkerAction.CREATE_LINKER_REQUEST.equals(annotation.value())) {
            this.encodeCreateLinkerRequest = method;
            return;
        }
        if (FoxEdgeLinkerAction.CREATE_LINKER_RESPOND.equals(annotation.value())) {
            this.decodeCreateLinkerRespond = method;
            return;
        }
        if (FoxEdgeLinkerAction.ACTIVE_LINKER_REQUEST.equals(annotation.value())) {
            this.encodeActiveLinkerRequest = method;
            return;
        }
        if (FoxEdgeLinkerAction.ACTIVE_LINKER_RESPOND.equals(annotation.value())) {
            this.decodeActiveLinkerRespond = method;
            return;
        }
        if (FoxEdgeLinkerAction.INTERCEPT_LINKER_RESPOND.equals(annotation.value())) {
            this.decodeInterceptLinkerRespond = method;
            return;
        }
    }

    public boolean isNull() {
        if (this.encodeCreateLinkerRequest == null) {
            return true;
        }
        if (this.decodeCreateLinkerRespond == null) {
            return true;
        }
        if (this.encodeActiveLinkerRequest == null) {
            return true;
        }
        if (this.decodeActiveLinkerRespond == null) {
            return true;
        }


        return this.decodeInterceptLinkerRespond == null;
    }
}
