package cn.foxtech.device.protocol.v1.omron.fins;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.omron.fins.core.encoder.DataEncoder;
import cn.foxtech.device.protocol.v1.omron.fins.core.encoder.PduEncoder;
import cn.foxtech.device.protocol.v1.omron.fins.core.encoder.ValueEncoder;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.data.ReadDataRequest;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.data.ReadDataRespond;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.data.Respond;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.Header;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.TransferRequest;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.TransferRespond;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 读取Registers
 */
@FoxEdgeDeviceType(value = "omron-fins", manufacturer = "omron")
public class ReadData {
    /**
     * 读取保持寄存器
     *
     * @param param 必须包含device_addr和modbus_holding_registers_template两个输入参数
     * @return 16进制文本格式的报文
     */
    @FoxEdgeOperate(name = "Read Data", polling = true, type = FoxEdgeOperate.encoder)
    public static String packReadData(Map<String, Object> param) {
        return (String) readData("", param);
    }

    /**
     * 解码保持寄存器
     *
     * @param hexString 16进制文本格式的报文
     * @param param     必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeOperate(name = "Read Data", polling = true, type = FoxEdgeOperate.decoder)
    public static Map<String, Object> unpackReadData(String hexString, Map<String, Object> param) {
        return (Map<String, Object>) readData(hexString, param);
    }

    public static Object readData(String hexString, Map<String, Object> param) {
        // Header信息
        String ICF = getParamValue(param, "ICF", "80", String.class);// 80 上位机固定位80，表示位主从应答
        Integer GCT = getParamValue(param, "GCT", 2, Integer.class);// 02 网关数，点对点通信固定位2
        Integer DA1 = getParamValue(param, "DA1", null, Integer.class);// 0A 设备ID，在connect命令返回
        Integer SA1 = getParamValue(param, "SA1", null, Integer.class);// 71 上位机ID
        Integer SID = getParamValue(param, "SID", 255, Integer.class);// FF 可变序号，设备返回相同的序号
        String AREA = getParamValue(param, "AREA", null, String.class);// B1 区域代码
        Integer WADR = getParamValue(param, "WADR", null, Integer.class);// 字地址
        Integer BADR = getParamValue(param, "BADR", null, Integer.class);// 位地址
        Integer CNT = getParamValue(param, "CNT", 1, Integer.class);// 字的数量
        String FMT = getParamValue(param, "FMT", null, String.class);// 格式
        String NAME = getParamValue(param, "NAME", null, String.class);// 名称

        // 检查输入参数
        if (MethodUtils.hasEmpty(ICF, GCT, DA1, SA1, SID, AREA, WADR, BADR, CNT, FMT, NAME)) {
            throw new ProtocolException("输入参数不能为空:ICF, GCT, DA1, SA1, SID, AREA, WADR, BADR, CNT, FMT,NAME");
        }

//        发送报文
//【46 49 4E 53】【00 00 00 1A】【 00 00 00 02】【00 00 00 00】【80 00 02 00 0A 00 00 71 00 FF】【 01 】      【01 】      【B1】     【  00 0A 】 【  00    】【00 01】
//【    FINS   】【   命令长度  】【    命令码   】【   错误码   】【     10字节FINS Header        】【FINS主命令】【FINS从命令】【寄存器控制位】【开始字地址】【开始位地址】【 数值 】
//        接收报文
//【46 49 4E 53】【00 00 00 18 】【00 00 00 02】【00 00 00 00】【C0 00 02 00 71 00 00 0A 00 FF 】【01】    【 01】  【00 00 00 0C】
//【    FINS   】【   命令长度  】【    命令码   】【   错误码   】【     10字节FINS Header        】【主结束码】【次结束码】【   数值    】

        if (hexString.isEmpty()) {
            // 构造二级报文
            ReadDataRequest readDataRequest = new ReadDataRequest();
            readDataRequest.setArea(Integer.parseInt(AREA, 16));
            readDataRequest.setWordAddress(WADR);
            readDataRequest.setBitAddress(BADR);
            readDataRequest.setCount(CNT);
            byte[] data = DataEncoder.encodeReadData(readDataRequest);

            Header header = new Header();
            header.setICF(Integer.parseInt(ICF, 16));
            header.setGCT(GCT);
            header.setDA1(DA1);
            header.setSA1(SA1);
            header.setSID(SID);

            // 构造一级报文
            TransferRequest transferRequest = new TransferRequest();
            transferRequest.setMrc(0x01);
            transferRequest.setSrc(0x01);
            transferRequest.setData(data);
            transferRequest.setHeader(header);

            // 报文一级打包
            byte[] pack = PduEncoder.encodePduPack(transferRequest);

            return HexUtils.byteArrayToHexString(pack);
        } else {

            byte[] pack = HexUtils.hexStringToByteArray(hexString);

            // 一级报文解码
            TransferRespond transferRespond = PduEncoder.decodePdu(pack, TransferRespond.class);
            Header header = transferRespond.getHeader();
            if (header.getSID() != SID) {
                throw new ProtocolException("设备返回的流水号跟发送流水号不一致");
            }

            // 二级报文解码
            ReadDataRespond readDataRespond = DataEncoder.decodeReadData(transferRespond.getData());
            if (readDataRespond.getMain() != 0 || readDataRespond.getSub() != 0) {
                StringBuilder sb = new StringBuilder();
                Respond.checkEndCode(readDataRespond.getMain(), readDataRespond.getSub(), sb);
                throw new ProtocolException("设备返回出错信息:" + sb);
            }

            byte[] value = readDataRespond.getData();
            Map<String, Object> values = new HashMap<>();
            values.put(NAME, ValueEncoder.decodeReadData(value, 1, FMT));
            return values;
        }
    }


    public static <T> T getParamValue(Map<String, Object> param, String key, T defaultValue, Class<T> clazz) {
        Object value = param.get(key);

        // 如果这个对象不存在，则返回默认值
        if (value == null) {
            return defaultValue;
        }

        // 如果是指定的数据类型，那么返回该对象
        if (clazz.isInstance(value)) {
            return (T) value;
        }

        return defaultValue;
    }
}
