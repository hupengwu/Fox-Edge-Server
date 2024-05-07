package cn.foxtech.device.protocol.v1.modbus.template;

import cn.foxtech.device.protocol.v1.core.context.ApplicationContext;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.ITemplate;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusWriteRegistersRequest;
import cn.foxtech.device.protocol.v1.utils.BitsUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HoldingRegisters数据实体的数据模板
 */
@Data
public class JReadRegistersTemplate implements ITemplate {
    public static final String READ_HOLDING_REGISTER = "Read Holding Register";
    public static final String READ_INPUT_REGISTER = "Read Input Register";
    public static final String WRITE_SINGLE_REGISTER = "Write Single Register";


    private JDecoderParam decoderParam = new JDecoderParam();

    /**
     * 缺省的寄存器格式
     *
     * @return 系统级别的模板名称
     */
    public String getSysTemplateName() {
        return "register default";
    }


    public void loadJsnModel(String modelName) {
        // 从进程的上下文中，获得设备模型信息
        Map<String, Object> deviceTemplateEntity = ApplicationContext.getDeviceModels(modelName);

        // 检测：上下文侧的时间戳和当前模型的时间戳是否一致
        Object updateTime = deviceTemplateEntity.getOrDefault("updateTime", 0L);
        if (this.decoderParam.updateTime.equals(updateTime)) {
            return;
        }

        // 取出JSON模型的数据列表
        Map<String, Object> modelParam = (Map<String, Object>) deviceTemplateEntity.getOrDefault("modelParam", new HashMap<>());
        List<Map<String, Object>> rows = (List<Map<String, Object>>) modelParam.getOrDefault("list", new ArrayList<>());

        // 转换成当前的JDecoderValueParam对象
        Map<String, JDecoderValueParam> map = new HashMap<>();
        for (Map<String, Object> row : rows) {
            if (row.size() < 7) {
                continue;
            }

            JDecoderValueParam jDecoderValueParam = new JDecoderValueParam();

            jDecoderValueParam.value_name = (String) row.get("value_name");
            jDecoderValueParam.value_index = getInteger(row.get("value_index"));
            jDecoderValueParam.bit_index = getInteger(row.get("bit_index"));
            jDecoderValueParam.bit_length = getInteger(row.get("bit_length"));
            jDecoderValueParam.value_type = (String) row.get("value_type");
            jDecoderValueParam.magnification = getDouble(row.get("magnification"));
            jDecoderValueParam.determine = (String) row.get("determine");


            map.put(jDecoderValueParam.getValue_name(), jDecoderValueParam);
        }

        // 保存信息
        this.decoderParam.valueMap = map;
        this.decoderParam.table = modelName;
        this.decoderParam.updateTime = updateTime;
        this.decoderParam.sourceType = "jsn";
    }

    private Integer getInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }

        return null;
    }

    private Double getDouble(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return Double.parseDouble((String) value);
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Float) {
            return ((Float) value).doubleValue();
        }

        return null;
    }


    /**
     * 对保持寄存器的数据进行处理
     *
     * @param address    地址
     * @param count      数量
     * @param statusList HoldingRegister状态
     * @return 数据表
     * @throws ProtocolException 异常信息
     */
    public Map<String, Object> decode(int address, int count, int[] statusList) throws ProtocolException {
        return this.decodeValue(address, count, statusList);
    }


    public ModBusWriteRegistersRequest encode(String objectName, Object objectValue) {
        ModBusWriteRegistersRequest request = new ModBusWriteRegistersRequest();

        JDecoderValueParam jDecoderValueParam = this.decoderParam.valueMap.get(objectName);
        if (jDecoderValueParam == null) {
            throw new ProtocolException("csv中未定义该对象的信息");
        }


        if (jDecoderValueParam.value_type.equals("int")) {
            request.setMemAddr(jDecoderValueParam.value_index);
            request.setValue((int) (Integer.valueOf(objectValue.toString()) / jDecoderValueParam.magnification));
        }
        if (jDecoderValueParam.value_type.equals("float")) {
            request.setMemAddr(jDecoderValueParam.value_index);
            request.setValue((int) (Float.valueOf(objectValue.toString()) / jDecoderValueParam.magnification));
        }

        if (jDecoderValueParam.value_type.equals("bool")) {
            throw new ProtocolException("不支持对bool进行编码，因为一个16位寄存器可能是多个bool在彼此共享");
        }

        return request;
    }


    private Map<String, Object> decodeValue(int address, int count, int[] statusList) {
        int offsetStart = address;
        int offsetEnd = address + count - 1;
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, JDecoderValueParam> entry : this.decoderParam.valueMap.entrySet()) {
            String name = entry.getKey();
            JDecoderValueParam jDecoderValueParam = entry.getValue();

            // 检查：csv的下标是否越界
            if (jDecoderValueParam.value_index < offsetStart) {
                continue;
            }
            if (jDecoderValueParam.value_index > offsetEnd + 1) {
                continue;
            }

            int index = jDecoderValueParam.value_index - offsetStart;
            if (index >= statusList.length) {
                continue;
            }

            int status = statusList[index];


            // 整数：比如284，编码格式为int16，但实际上是放大10倍，变为2840
            if (jDecoderValueParam.value_type.equals("int")) {
                result.put(name, (int) (status * jDecoderValueParam.magnification));
            }
            // 定点小数：比如284，编码格式为int16，但实际上是缩小10倍，变为实28.4
            if (jDecoderValueParam.value_type.equals("fix-float")) {
                result.put(name, status * jDecoderValueParam.magnification);
            }
            // 浮点数
            if (jDecoderValueParam.value_type.equals("float")) {
                if (jDecoderValueParam.bit_length.equals(32)) {
                    // 32位浮点（数默认格式：低位在前，高位在后）：例如，45 0f 60 00 数值位2294.0
                    byte bit0 = (byte) (statusList[index + 1] >> 0 & 0xff);
                    byte bit1 = (byte) (statusList[index + 1] >> 8 & 0xff);
                    byte bit2 = (byte) (statusList[index + 0] >> 0 & 0xff);
                    byte bit3 = (byte) (statusList[index + 0] >> 8 & 0xff);
                    float fValue = BitsUtils.bitsToFloat(bit0, bit1, bit2, bit3);
                    result.put(name, fValue * jDecoderValueParam.magnification);
                }

            }
            if (jDecoderValueParam.value_type.equals("bool")) {
                int value = 0;
                for (int i = 0; i < jDecoderValueParam.bit_length; i++) {
                    value += status & this.binary(jDecoderValueParam.bit_index + i);
                }

                if (jDecoderValueParam.determine.equals(">0")) {
                    result.put(name, value > 0);
                } else {
                    result.put(name, value == 0);
                }
            }
        }


        return result;
    }


    /**
     * 2位置进制转数字，比如0=0x01，1=0x02，2=0x04，3=0x08，4=0x10
     *
     * @param pos 位置
     * @return 值
     */
    private int binary(int pos) {
        int value = 1;
        while (pos-- >= 0) {
            value *= 2;
        }

        return value / 2;
    }

    @Data
    static public class JDecoderParam implements Serializable {
        private Object updateTime = 0;
        private String sourceType = "csv";
        private String table;
        private Map<String, JDecoderValueParam> valueMap = new HashMap<>();
    }


    @Data
    static public class JDecoderValueParam implements Serializable {
        /**
         * 值的名称
         */
        private String value_name;
        /**
         * 寄存器状态的偏移量位置
         */
        private Integer value_index;
        /**
         * 16位寄存器中的bit位
         */
        private Integer bit_index;
        /**
         * 16位寄存器中的bit位长度
         */
        private Integer bit_length;
        /**
         * 数据类型int,float类型，还是bool类型
         * int：对数据进行int类型处理，结合magnification放大倍数处理
         * float：对数据进行float类型处理，结合magnification放大倍数处理
         * bool：
         */
        private String value_type;
        /**
         * 倍率：int和float的放大/缩小倍数
         */
        private Double magnification;
        /**
         * bool判定true的条件
         */
        private String determine;
    }
}
