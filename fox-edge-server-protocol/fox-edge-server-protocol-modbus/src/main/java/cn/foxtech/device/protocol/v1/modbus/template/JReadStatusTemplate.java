/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.modbus.template;

import cn.foxtech.device.protocol.v1.core.context.ApplicationContext;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.ITemplate;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusWriteStatusRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
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

    private JDecoderParam decoderParam = new JDecoderParam();

    /**
     * 缺省的状态格式
     *
     * @return 系统模板名称
     */
    public String getSysTemplateName() {
        return "status default";
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
            if (row.size() < 2) {
                continue;
            }

            JDecoderValueParam jDecoderValueParam = new JDecoderValueParam();
            jDecoderValueParam.value_name = (String) row.get("value_name");
            jDecoderValueParam.value_index = getInteger(row.get("value_index"));


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

    /**
     * 对保持寄存器的数据进行处理
     *
     * @param address    地址
     * @param coilCount  数量
     * @param statusList HoldingRegister状态
     * @return 数据表
     * @throws ProtocolException 异常信息
     */
    public Map<String, Object> decode(int address, int coilCount, boolean[] statusList) throws ProtocolException {
        return this.decodeValue(address, coilCount, statusList);
    }

    public ModBusWriteStatusRequest encode(String objectName, Object objectValue) {
        ModBusWriteStatusRequest request = new ModBusWriteStatusRequest();

        JDecoderValueParam jDecoderValueParam = this.decoderParam.valueMap.get(objectName);
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
    }
}
