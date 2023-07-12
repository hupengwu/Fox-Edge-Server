package cn.foxtech.device.protocol.modbus.template;

import cn.foxtech.device.protocol.core.exception.ProtocolException;
import cn.foxtech.device.protocol.core.template.ITemplate;
import cn.foxtech.device.protocol.core.utils.BitsUtils;
import cn.foxtech.device.protocol.core.utils.ByteUtils;
import cn.foxtech.device.protocol.modbus.core.ModBusWriteRegistersRequest;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import lombok.Data;

import java.io.File;
import java.io.Serializable;
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

    private String template_name = "";
    private JOperate operate = new JOperate();

    /**
     * 缺省的寄存器格式
     *
     * @return 系统级别的模板名称
     */
    public String getSysTemplateName() {
        return "register default";
    }

    /**
     * 从CSV文件中装载映射表
     *
     * @param table csv表名称
     */
    public void loadCsvFile(String table) {
        File dir = new File("");

        File file = new File(dir.getAbsolutePath() + "/template/" + table);
        CsvReader csvReader = CsvUtil.getReader();
        List<JDecoderValueParam> rows = csvReader.read(ResourceUtil.getReader(file.getPath(), CharsetUtil.CHARSET_GBK), JDecoderValueParam.class);

        // 将文件记录组织到map中
        Map<String, JDecoderValueParam> map = new HashMap<>();
        for (JDecoderValueParam jDecoderValueParam : rows) {
            map.put(jDecoderValueParam.getValue_name(), jDecoderValueParam);
        }

        this.operate.decoder_param.valueMap = map;
        this.operate.decoder_param.table = table;
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

        JDecoderValueParam jDecoderValueParam = this.operate.decoder_param.valueMap.get(objectName);
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

    private Map<String, Object> decodeValueTest(int address, int count, int[] statusList) {


        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < count; i++) {
            for (Map.Entry<String, JDecoderValueParam> entry : this.operate.decoder_param.valueMap.entrySet()) {
                String name = entry.getKey();
                JDecoderValueParam jDecoderValueParam = entry.getValue();

                // 寄存器地址的位置
                int offsetStart = address + i;

                // 检测：该寄存器地址
                if (jDecoderValueParam.value_index != offsetStart) {
                    continue;
                }

                int bitLength = jDecoderValueParam.bit_index + jDecoderValueParam.bit_length;
                // 该变量占用1个寄存器
                if ((bitLength > 1 && bitLength <= 16) && (i + 0 < statusList.length)) {
                    byte[] data = new byte[2];
                    data[0] = (byte) ((statusList[i] >> 8) & 0xff);
                    data[1] = (byte) (statusList[i] & 0xff);

                    // 解码
                    this.decodeValue(jDecoderValueParam,data,result);
                    continue;
                }
                // 该变量占用2个寄存器
                if ((bitLength > 16 && bitLength <= 32) && (i + 1 < statusList.length)) {
                    byte[] data = new byte[4];
                    data[0] = (byte) ((statusList[i] >> 8) & 0xff);
                    data[1] = (byte) (statusList[i] & 0xff);
                    data[2] = (byte) ((statusList[i + 1] >> 8) & 0xff);
                    data[3] = (byte) (statusList[i + 1] & 0xff);

                    // 解码
                    this.decodeValue(jDecoderValueParam,data,result);
                    continue;
                }
                // 该变量占用3个寄存器
                if ((bitLength > 32 && bitLength <= 48) && (i + 2 < statusList.length)) {
                    byte[] data = new byte[6];
                    data[0] = (byte) ((statusList[i] >> 8) & 0xff);
                    data[1] = (byte) (statusList[i] & 0xff);
                    data[2] = (byte) ((statusList[i + 1] >> 8) & 0xff);
                    data[3] = (byte) (statusList[i + 1] & 0xff);
                    data[4] = (byte) ((statusList[i + 2] >> 8) & 0xff);
                    data[5] = (byte) (statusList[i + 2] & 0xff);

                    // 解码
                    this.decodeValue(jDecoderValueParam,data,result);
                    continue;
                }
                // 该变量占用4个寄存器
                if ((bitLength > 32 && bitLength <= 48) && (i + 3 < statusList.length)) {
                    byte[] data = new byte[8];
                    data[0] = (byte) ((statusList[i] >> 8) & 0xff);
                    data[1] = (byte) (statusList[i] & 0xff);
                    data[2] = (byte) ((statusList[i + 1] >> 8) & 0xff);
                    data[3] = (byte) (statusList[i + 1] & 0xff);
                    data[4] = (byte) ((statusList[i + 2] >> 8) & 0xff);
                    data[5] = (byte) (statusList[i + 2] & 0xff);
                    data[6] = (byte) ((statusList[i + 3] >> 8) & 0xff);
                    data[7] = (byte) (statusList[i + 3] & 0xff);

                    // 解码
                    this.decodeValue(jDecoderValueParam,data,result);
                    continue;
                }

            }
        }

        return result;
    }

    private void decodeValue(JDecoderValueParam jDecoderValueParam, byte[] data, Map<String, Object> result) {
        // 整数：比如284，编码格式为int16，但实际上是放大10倍，变为2840
        if (jDecoderValueParam.value_type.equals("int")) {
            if (data.length == 2) {
                int value = ByteUtils.decodeInt16(data);
                result.put(jDecoderValueParam.value_name, (int) (value * jDecoderValueParam.magnification));
            }
            if (data.length == 4) {
                long value = ByteUtils.decodeInt32(data);
                result.put(jDecoderValueParam.value_name, (int) (value * jDecoderValueParam.magnification));
            }

            return;
        }
        // 定点小数：比如284，编码格式为int16，但实际上是缩小10倍，变为实28.4
        if (jDecoderValueParam.value_type.equals("fix-float")) {
            if (data.length == 2) {
                int value = ByteUtils.decodeInt16(data);
                result.put(jDecoderValueParam.value_name, value * jDecoderValueParam.magnification);
            }
            if (data.length == 4) {
                long value = ByteUtils.decodeInt32(data);
                result.put(jDecoderValueParam.value_name, value * jDecoderValueParam.magnification);
            }
            return;
        }
        // 浮点数：比如28.4，编码格式为float32，实际上是28.4
        if (jDecoderValueParam.value_type.equals("float")) {
            if (data.length == 4) {
                float value = BitsUtils.bitsToFloat(data);
                result.put(jDecoderValueParam.value_name, value * jDecoderValueParam.magnification);
            }

            return;
        }
    }

    private Map<String, Object> decodeValue(int address, int count, int[] statusList) {
        int offsetStart = address;
        int offsetEnd = address + count - 1;
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, JDecoderValueParam> entry : this.operate.decoder_param.valueMap.entrySet()) {
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
            // 浮点数：比如28.4，编码格式为float32，实际上是28.4
            if (jDecoderValueParam.value_type.equals("float")) {
                result.put(name, status * jDecoderValueParam.magnification);
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
    static public class JOperate implements Serializable {
        private String name = "";
        private String operate_name = "";
        private String modbus_mode = "";
        private JEncoderParam encoder_param = new JEncoderParam();
        private JDecoderParam decoder_param = new JDecoderParam();
    }

    @Data
    static public class JEncoderParam implements Serializable {
        private String reg_addr;
        private Integer reg_cnt;
    }

    @Data
    static public class JDecoderParam implements Serializable {
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
        private Float magnification;
        /**
         * bool判定true的条件
         */
        private String determine;
    }
}
