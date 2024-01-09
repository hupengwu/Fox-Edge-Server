package cn.foxtech.device.protocol.v1.hj212_2017.entity;

import java.util.HashMap;
import java.util.Map;

public class DefineMap {
    public static final DefineMap inst = new DefineMap();
    private final Map<Integer, String> st = new HashMap<>();

    public String getStName(Integer key) {
        return this.getStInst().get(key);
    }

    public Integer getStValue(String name) {
        for (Integer key : this.getStInst().keySet()) {
            if (this.getStInst().get(key).equals(name)) {
                return key;
            }
        }

        return 0;
    }

    private Map<Integer, String> getStInst() {
        if (!this.st.isEmpty()) {
            return this.st;
        }

        this.st.put(21, "地表水质量监测");
        this.st.put(22, "空气质量监测");
        this.st.put(23, "声环境质量监测");
        this.st.put(24, "地下水质量监测");
        this.st.put(25, "土壤质量监测");
        this.st.put(26, "海水质量监测");
        this.st.put(27, "挥发性有机物监测");
        this.st.put(31, "大气环境污染源");
        this.st.put(32, "地表水体环境污染源");
        this.st.put(33, "地下水体环境污染源");
        this.st.put(34, "海洋环境污染源");
        this.st.put(35, "土壤环境污染源");
        this.st.put(36, "声环境污染源");
        this.st.put(37, "振动环境污染源");
        this.st.put(38, "放射性环境污染源");
        this.st.put(39, "工地扬尘污染源");
        this.st.put(41, "电磁环境污染源");
        this.st.put(51, "烟气排放过程监控");
        this.st.put(52, "污水排放过程监控");
        this.st.put(91, "系统交互");

        return this.st;
    }
}
