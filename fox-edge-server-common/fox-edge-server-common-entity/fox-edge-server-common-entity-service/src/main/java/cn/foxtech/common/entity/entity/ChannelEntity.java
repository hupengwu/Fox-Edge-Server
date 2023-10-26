package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_channel")
public class ChannelEntity extends ChannelBase {
    /**
     * 参数信息
     */
    private Map<String, Object> channelParam = new HashMap<>();
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


    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.channelParam);
        list.add(this.extendParam);

        return list;
    }

    public void bind(ChannelEntity other) {
        super.bind(other);

        this.channelParam = other.channelParam;
        this.extendParam = other.extendParam;
    }
}
