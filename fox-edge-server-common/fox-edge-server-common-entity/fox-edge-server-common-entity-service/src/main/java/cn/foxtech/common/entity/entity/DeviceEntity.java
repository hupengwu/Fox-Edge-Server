package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_device")
public class DeviceEntity extends DeviceBase {
    /**
     * 设备的配置参数
     */
    private Map<String, Object> deviceParam = new HashMap<>();
    /**
     * 扩展参数（非工作参数）：主要是一些备注信息，它并不参与fox-edge本身的工作
     */
    private Map<String, Object> extendParam = new HashMap<>();

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = super.makeServiceKeyList();
        return list;
    }

    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.deviceParam);
        list.add(this.extendParam);

        return list;
    }

    public void bind(DeviceEntity other) {
        super.bind(other);

        this.deviceParam = other.deviceParam;
        this.extendParam = other.extendParam;
    }
}
