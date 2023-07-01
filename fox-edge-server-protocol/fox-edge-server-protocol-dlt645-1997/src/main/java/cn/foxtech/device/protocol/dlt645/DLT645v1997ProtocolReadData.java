package cn.foxtech.device.protocol.dlt645;

import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.dlt645.core.DLT645Define;
import cn.foxtech.device.protocol.dlt645.core.DLT645Protocol;
import cn.foxtech.device.protocol.dlt645.core.DLT645Template;
import cn.foxtech.device.protocol.dlt645.core.entity.DLT645DataEntity;
import cn.foxtech.device.protocol.dlt645.core.entity.DLT645FunEntity;
import cn.foxtech.device.protocol.dlt645.core.entity.DLT645v1997DataEntity;
import cn.foxtech.device.protocol.core.exception.ProtocolException;
import cn.foxtech.device.protocol.core.utils.HexUtils;
import cn.foxtech.device.protocol.core.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 读数据
 */
@FoxEdgeDeviceType(value = "DLT645 v1997", manufacturer = "Fox Edge")
public class DLT645v1997ProtocolReadData {
    /**
     * 读数据
     *
     * @param param 必须包含device_addr和modbus_holding_registers_template两个输入参数
     * @return 16进制文本格式的报文
     */
    @FoxEdgeOperate(name = "读数据", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packReadData(Map<String, Object> param) {
        // 提取业务参数：设备地址/对象名称/CSV模板文件
        String deviceAddress = (String) param.get("deviceAddress");
        String objectName = (String) param.get("objectName");
        String tableName = (String) param.get("tableName");

        // 简单校验参数
        if (MethodUtils.hasNull(deviceAddress, objectName, tableName)) {
            throw new ProtocolException("参数不能为空:deviceAddress, objectName, tableName");
        }


        // 对设备地址进行编码
        byte[] tmp = HexUtils.hexStringToByteArray(deviceAddress);
        byte[] adrr = new byte[6];
        System.arraycopy(tmp, 0, adrr, 0, Math.min(tmp.length, adrr.length));
        for (int i = 0; i < adrr.length / 2; i++) {
            byte by = adrr[i];
            adrr[i] = adrr[5 - i];
            adrr[5 - i] = by;
        }

        // 根据对象名获取对象格式信息，这个格式信息，记录在CSV文件中
        DLT645DataEntity dataEntity = DLT645Template.inst().getTemplateByName(DLT645Define.PRO_VER_1997, tableName).get(objectName);
        if (dataEntity == null) {
            throw new ProtocolException("CSV模板文件: " + tableName + " 中未定义对象:" + objectName + " ，你需要在模板中添加该对象信息");
        }

        // 组装成协议框架的三要素ADR/FUN/DAT
        param.put(DLT645Protocol.ADR, adrr);
        param.put(DLT645Protocol.FUN, DLT645FunEntity.getCodev1997("读数据"));
        param.put(DLT645Protocol.DAT, dataEntity.getDIn());

        // 使用DLT645协议框架编码
        byte[] pack = DLT645Protocol.packCmd(param);

        // 将报文按要求的16进制格式的String对象返回
        return HexUtils.byteArrayToHexString(pack);
    }

    /**
     * 解码保持寄存器
     *
     * @param hexString 16进制文本格式的报文
     * @param param     必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeOperate(name = "读数据", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unpackReadData(String hexString, Map<String, Object> param) {
        String tableName = (String) param.get("tableName");

        // 简单校验参数
        if (MethodUtils.hasNull(tableName)) {
            throw new ProtocolException("参数不能为空:tableName");
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
        dataEntity.decodeValue(data, DLT645Template.inst().getTemplateByDIn(DLT645Define.PRO_VER_1997, tableName));

        //将解码出来的数字，按要求的Map<String, Object>格式返回
        Map<String, Object> result = new HashMap<>();
        result.put(dataEntity.getName(), dataEntity.getValue());
        if (dataEntity.getValue2nd() != null) {
            result.put(dataEntity.getName() + ":相关值", dataEntity.getValue2nd());
        }

        return result;
    }
}
