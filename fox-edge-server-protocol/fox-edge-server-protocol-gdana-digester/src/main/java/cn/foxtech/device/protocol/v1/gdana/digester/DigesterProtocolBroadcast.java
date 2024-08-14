/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.gdana.digester;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 添加样本：这是一个手动操作方法，不是轮询查询方法
 */
@FoxEdgeDeviceType(value = "全自动消解控制器", manufacturer = "广州格丹纳仪器有限公司")
public class DigesterProtocolBroadcast {
    /**
     * 查询传感器状态
     *
     * @param param 输入参数
     * @return 操作是否成功
     */
    @FoxEdgeOperate(name = "广播读地址", type = FoxEdgeOperate.encoder)
    public static String encodePack(Map<String, Object> param) {
        byte[] pack = DigesterProtocolFrame.encodeBroadcast();
        return HexUtils.byteArrayToHexString(pack);
    }

    /**
     * 查询传感器状态
     *
     * @param hexString 数据报文
     * @return 解码是否成功
     */
    @FoxEdgeOperate(name = "广播读地址", type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePack(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        DigesterEntity entity = DigesterProtocolFrame.decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        if (entity.getFunc() != 0x80) {
            throw new ProtocolException("设备拒绝！");
        }


        // 解码数据
        byte[] data = entity.getData();
        if (data.length != 9) {
            throw new ProtocolException("数据长度不正确！");
        }

        Map<String, Object> value = new HashMap<>();
        value.put("设备地址", entity.getAddr());
        value.put("子设备地址", entity.getSubAddr());

        return value;
    }
}

