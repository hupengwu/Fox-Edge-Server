/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.device.protocol.v1.zktl.air5in1;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeReport;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.utils.JsonUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.zktl.air5in1.encoder.Encoder;
import cn.foxtech.device.protocol.v1.zktl.air5in1.entity.ZktlDataEntity;

import java.io.IOException;
import java.util.Map;

/**
 * 读取Registers
 */
@FoxEdgeDeviceType(value = "五合一空气监测传感器", manufacturer = "武汉中科图灵科技有限公司")
public class DecodePduEntity {
    /**
     * 解码数据：对主动上报数据的解码
     *
     * @param hexString 16进制文本格式的报文
     * @param param     必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeReport(type = FoxEdgeReport.alarm)
    @FoxEdgeOperate(name = "解码器PDU报文", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePduEntity(String hexString, Map<String, Object> param) {
        try {
            // 转换数据
            byte[] pdu = HexUtils.hexStringToByteArray(hexString);

            // 解码数据
            ZktlDataEntity dataEntity = Encoder.decodeDataEntity(pdu);

            // 转换成Map对象
            Map<String, Object> value = JsonUtils.buildObject(dataEntity, Map.class);
            return value;
        } catch (IOException e) {
            throw new ProtocolException(e.getMessage());
        }
    }
}
