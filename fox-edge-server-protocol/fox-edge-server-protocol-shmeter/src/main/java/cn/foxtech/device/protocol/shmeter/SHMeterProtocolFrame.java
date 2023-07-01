package cn.foxtech.device.protocol.shmeter;


import cn.foxtech.device.protocol.dlt645.core.DLT645Protocol;

import java.util.Map;

/**
 * 上海电表采用的是DLT645的协议框架
 */
public class SHMeterProtocolFrame extends DLT645Protocol {
    /**
     * @param param
     * @return
     */
    public static byte[] packCmd(Map<String, Object> param) {
        return DLT645Protocol.packCmd(param);
    }

    /**
     * ZX的电源自己会在结尾处额外再加一个0X0D，所以要重载处理
     *
     * @param arrCmd
     * @return
     */
    public static Map<String, Object> unPackCmd2Map(byte[] arrCmd, Map<String, Object> param) {
        return DLT645Protocol.unPackCmd2Map(arrCmd);
    }

    /**
     * 如果没有夹带版本和地址参数，那么填写默认参数
     *
     * @param param
     */
    public static void pretreatParam(Map<String, Object> param) {
        if (!param.containsKey(ADR)) {
            // 默认设备地址0x01
            byte[] arrAddr = new byte[6];
            arrAddr[0] = 0x01;
            param.put(ADR, 0x01);
        }
        if (!param.containsKey(FUN)) {
            // 读数据功能码 0x01
            param.put(FUN, 0x01);
        }
    }
}
