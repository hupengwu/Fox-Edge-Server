package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_trigger_config")
public class TriggerConfigPo extends TriggerConfigBase {
    /**
     * 对象列表
     */
    private String objectsName = "[]";

    /**
     * 配置参数
     */
    private String triggerParam = "{}";

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.objectsName);
        list.add(this.triggerParam);

        return list;
    }
}
