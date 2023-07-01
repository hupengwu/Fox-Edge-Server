package cn.foxtech.device.protocol.zxdu58;


import cn.foxtech.device.protocol.telecom.core.TelecomProtocol;

import java.util.Arrays;
import java.util.Map;

/**
 * ZXDU58的协议框架是电信总局的子协议
 * 在解码器的jar包被fox-edge-server-device-adapter服务扫描加载时，本JAR包在发布时
 * 因基类TelecomProtocol在另一个包fox-edge-server-protocol-common.jar里，
 * 所有要把这个jar包带上一起发布，否则在扫描阶段说找不到类，实际上是缺TelecomProtocol
 */
public class ZXDU58ProtocolFrame extends TelecomProtocol {
    /**
     * @param param
     * @return
     */
    public static byte[] packCmd(Map<String, Object> param) {
        return TelecomProtocol.packCmd4Map(param);
    }

    /**
     * ZX的电源自己会在结尾处额外再加一个0X0D，所以要重载处理
     *
     * @param arrCmd
     * @return
     */
    public static Map<String, Object> unPackCmd4Map(byte[] arrCmd) {
        if (arrCmd.length < 2) {
            return null;
        }

        // ZX的电源自己会在结尾处额外再加一个0X0D
        if (arrCmd[arrCmd.length - 2] == 0x0D && arrCmd[arrCmd.length - 1] == 0x0D) {
            arrCmd = Arrays.copyOf(arrCmd, arrCmd.length - 1);
        }

        return TelecomProtocol.unPackCmd2Map(arrCmd);
    }

    /**
     * 如果没有夹带版本和地址参数，那么填写默认参数
     *
     * @param param
     */
    public static void pretreatParam(Map<String, Object> param) {
        if (!param.containsKey(VER)) {
            param.put(VER, 0x20);
        }
        if (!param.containsKey(ADR)) {
            param.put(ADR, 0x01);
        }
    }
}
