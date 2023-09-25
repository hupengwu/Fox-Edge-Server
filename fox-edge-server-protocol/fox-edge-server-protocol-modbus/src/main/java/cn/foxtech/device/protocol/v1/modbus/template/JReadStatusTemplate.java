package cn.foxtech.device.protocol.v1.modbus.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.ITemplate;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusWriteStatusRequest;
import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CoilStatus数据实体的数据模板
 */
@Data
public class JReadStatusTemplate implements ITemplate {
    public static final String READ_COIL_STATUS = "Read Coil Status";
    public static final String READ_DISCRETE_INPUT_STATUS = "Read Discrete Input Status";
    public static final String WRITE_SINGLE_STATUS = "Write Single Status";

    private String template_name = "";
    private JOperate operate = new JOperate();

    /**
     * 缺省的状态格式
     *
     * @return 系统模板名称
     */
    public String getSysTemplateName() {
        return "status default";
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
     * @param address 地址
     * @param coilCount 数量
     * @param statusList HoldingRegister状态
     * @return 数据表
     * @throws ProtocolException 异常信息
     */
    public Map<String, Object> decode(int address, int coilCount, boolean[] statusList) throws ProtocolException {
        return this.decodeValue(address, coilCount, statusList);
    }

    public ModBusWriteStatusRequest encode(String objectName, Object objectValue) {
        ModBusWriteStatusRequest request = new ModBusWriteStatusRequest();

        JDecoderValueParam jDecoderValueParam = this.operate.decoder_param.valueMap.get(objectName);
        if (jDecoderValueParam == null) {
            throw new ProtocolException("csv中未定义该对象的信息");
        }

        request.setMemAddr(jDecoderValueParam.value_index);
        request.setStatus(Boolean.valueOf(objectValue.toString()));

        return request;
    }

    /**
     * 解码
     *
     * @param address    起始地址
     * @param statusList 起始地址之后的连续数据
     * @return
     */
    private Map<String, Object> decodeValue(int address, int count, boolean[] statusList) {
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

            boolean status = statusList[index];
            result.put(name, status);
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
    }
}
