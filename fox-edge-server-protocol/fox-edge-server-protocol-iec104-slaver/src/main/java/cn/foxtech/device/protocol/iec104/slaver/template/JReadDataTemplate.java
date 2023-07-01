package cn.foxtech.device.protocol.iec104.slaver.template;

import cn.foxtech.device.protocol.core.exception.ProtocolException;
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
 * 读取IEC104的单点遥信模板
 */
@Data
public class JReadDataTemplate {
    private String template_name = "";
    private JOperate operate = new JOperate();

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
     * @param statusList HoldingRegister状态
     * @return 数据表
     * @throws ProtocolException 异常信息
     */
    public Map<String, Object> decode(int address, Map<Integer, Integer> statusList) throws ProtocolException {
        return this.decodeValue(address, statusList);
    }


    private Map<String, Object> decodeValue(int address, Map<Integer, Integer> statusList) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, JDecoderValueParam> entry : this.operate.decoder_param.valueMap.entrySet()) {
            String name = entry.getKey();
            JDecoderValueParam jDecoderValueParam = entry.getValue();

            // 是否为需要处理的数据
            Integer status = statusList.get(jDecoderValueParam.value_index);
            if (status == null) {
                continue;
            }

            if (jDecoderValueParam.value_type.equals("int")) {
                result.put(name, (int) (status * jDecoderValueParam.magnification));
            }
            if (jDecoderValueParam.value_type.equals("float")) {
                result.put(name, status * jDecoderValueParam.magnification);
            }
            if (jDecoderValueParam.value_type.equals("bool")) {
                int value = status;
                if (jDecoderValueParam.determine.equals(">0")) {
                    result.put(name, value > 0);
                } else {
                    result.put(name, value == 0);
                }
            }
        }


        return result;
    }

    @Data
    static public class JOperate implements Serializable {
        private String operate_name = "";
        private JEncoderParam encoder_param = new JEncoderParam();
        private JDecoderParam decoder_param = new JDecoderParam();
    }

    @Data
    static public class JEncoderParam implements Serializable {
        // TYPE ID
        private String type_id;
        // VSQ
        private Boolean vsq_sq;
        private Integer vsq_num;
        // COT
        private Integer cot_reason;
        private Boolean cot_test;
        private Boolean cot_pn;
        private Integer cot_address;
        // 公共地址
        private Integer common_address;
        // 信息体
        private String data;
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
         * 值的编码格式：byte_bool
         */
        private String value_format;
        /**
         * 值的编码长度：1 字节
         */
        private Integer value_length;
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
         * bool判定true的条件：比如大于0
         */
        private String determine;
    }
}
