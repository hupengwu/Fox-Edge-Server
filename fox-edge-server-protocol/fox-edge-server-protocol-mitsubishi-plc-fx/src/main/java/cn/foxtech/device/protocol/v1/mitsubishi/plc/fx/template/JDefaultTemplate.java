package cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.template;

import cn.foxtech.device.protocol.v1.core.context.ApplicationContext;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.ITemplate;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.entity.MitsubishiPlcFxDeviceReadEntity;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusWriteRegistersRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认格式的模板
 * 列名称格式：value_name	value_target	value_index	bit_index	bit_length	value_type	magnification	determine	remark
 */
@Data
public class JDefaultTemplate implements ITemplate {
    public static final String FORMAT_NAME = "default";

    private JDecoderParam decoderParam = new JDecoderParam();

    public String getSysTemplateName() {
        return FORMAT_NAME;
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

        // 将文件记录组织到map中
        Map<String, JDecoderValueParam> map = new HashMap<>();
        for (Map<String, Object> row : rows) {
            JDecoderValueParam jDecoderValueParam = new JDecoderValueParam();
            jDecoderValueParam.setValue_name((String) row.get("value_name"));
            jDecoderValueParam.setValue_target((String) row.get("value_target"));
            jDecoderValueParam.setValue_type((String) row.get("value_type"));
            jDecoderValueParam.setDetermine((String) row.get("determine"));
            jDecoderValueParam.setValue_index(Integer.parseInt(row.get("value_index").toString()));
            jDecoderValueParam.setBit_index(Integer.parseInt(row.get("bit_index").toString()));
            jDecoderValueParam.setBit_length(Integer.parseInt(row.get("bit_length").toString()));
            jDecoderValueParam.setMagnification(Float.parseFloat(row.get("magnification").toString()));

            map.put(jDecoderValueParam.getValue_name(), jDecoderValueParam);
        }

        this.decoderParam.valueMap = map;
        this.decoderParam.table = modelName;
        this.decoderParam.updateTime = updateTime;
    }

    /**
     * 对保持寄存器的数据进行处理
     *
     * @param address 地址
     * @param count   数量
     * @param entity  HoldingRegister状态
     * @return 解码得到的数据
     * @throws ProtocolException 异常信息
     */
    public Map<String, Object> decode(int address, int count, MitsubishiPlcFxDeviceReadEntity entity) throws ProtocolException {
        return this.decodeValue(address, count, entity);
    }


    /**
     * 编码
     *
     * @param objectName  对象名称
     * @param objectValue 对象数值
     * @return ModBusWriteRegistersRequest请求对象
     */
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

    private Map<String, Object> decodeValue(int address, int count, MitsubishiPlcFxDeviceReadEntity entity) {
        int offsetStart = address;
        int offsetEnd = address + count - 1;
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, JDecoderValueParam> entry : this.decoderParam.valueMap.entrySet()) {
            String name = entry.getKey();
            JDecoderValueParam jDecoderValueParam = entry.getValue();

            // 检查:target
            if (!entity.getTarget().equals(jDecoderValueParam.value_target)) {
                continue;
            }

            // 检查：csv的下标是否越界
            if (jDecoderValueParam.value_index < offsetStart) {
                continue;
            }
            if (jDecoderValueParam.value_index > offsetEnd + 1) {
                continue;
            }

            int index = jDecoderValueParam.value_index - offsetStart;

            //  entity.getData().

            int status = 0;//statusList[index];


            if (jDecoderValueParam.value_type.equals("int")) {
                result.put(name, (int) (status * jDecoderValueParam.magnification));
            }
            if (jDecoderValueParam.value_type.equals("float")) {
                result.put(name, status * jDecoderValueParam.magnification);
            }
            if (jDecoderValueParam.value_type.equals("string")) {
                result.put(name, status);
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
    static public class JEncoderParam implements Serializable {
        private String reg_addr;
        private Integer reg_cnt;
    }

    @Data
    static public class JDecoderParam implements Serializable {
        private String table;
        private Object updateTime = 0L;
        private Map<String, JDecoderValueParam> valueMap = new HashMap<>();
    }


    @Data
    static public class JDecoderValueParam implements Serializable {
        /**
         * 值的名称
         */
        private String value_name;
        /**
         * target的名称
         */
        private String value_target;
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
         * string：字符串格式
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
