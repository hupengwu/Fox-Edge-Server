package cn.foxtech.device.protocol.v1.dlt645.v1997;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeReport;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dlt645.core.DLT645Define;
import cn.foxtech.device.protocol.v1.dlt645.core.DLT645Protocol;
import cn.foxtech.device.protocol.v1.dlt645.core.DLT645Template;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645FunEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645v1997DataEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 主动上报数据
 */
@FoxEdgeDeviceType(value = "DLT645 v1997", manufacturer = "Fox Edge")
public class DLT645v1997ProtocolReportData {
    /**
     * 解码保持寄存器
     *
     * @param hexString 16进制文本格式的报文
     * @param param     必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeReport(type = FoxEdgeReport.alarm)
    @FoxEdgeOperate(name = "上报数据", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unpackReadData(String hexString, Map<String, Object> param) {
        String modelName = (String) param.get("modelName");

        // 简单校验参数
        if (MethodUtils.hasNull(modelName)) {
            throw new ProtocolException("参数不能为空: modelName");
        }

        // 解码处理
        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);

        // 报文解析
        Map<String, Object> value = DLT645Protocol.unPackCmd2Map(arrCmd);
        if (value == null) {
            throw new ProtocolException("报文格式不正确，解析失败！");
        }

        // 功能码的解析
        byte func = (byte) value.get(DLT645Protocol.FUN);
        DLT645FunEntity entity = DLT645FunEntity.decodeEntity(func);
        if (entity.isError()) {
            throw new ProtocolException("设备返回出错代码：" + HexUtils.byteArrayToHexString((byte[]) value.get(DLT645Protocol.DAT)));
        }

        // 对数据进行解码
        byte[] data = (byte[]) value.get(DLT645Protocol.DAT);
        DLT645v1997DataEntity dataEntity = new DLT645v1997DataEntity();
        dataEntity.decodeValue(data, DLT645Template.inst().getTemplateByDIn(DLT645Define.PRO_VER_1997, modelName));

        //将解码出来的数字，按要求的Map<String, Object>格式返回
        Map<String, Object> result = new HashMap<>();
        result.put(dataEntity.getName(), dataEntity.getValue());
        if (dataEntity.getValue2nd() != null) {
            result.put(dataEntity.getName() + ":相关值", dataEntity.getValue2nd());
        }

        return result;
    }
}
