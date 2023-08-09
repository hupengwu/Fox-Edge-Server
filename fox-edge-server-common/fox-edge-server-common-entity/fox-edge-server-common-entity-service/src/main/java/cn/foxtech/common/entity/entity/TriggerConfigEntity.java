package cn.foxtech.common.entity.entity;


import lombok.*;

import java.util.*;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class TriggerConfigEntity extends TriggerConfigBase {
    /**
     * 对象列表
     */
    private Set<String> ObjectList = new HashSet<>();

    /**
     * 配置参数
     */
    private Map<String, Object> params = new HashMap<>();


    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.ObjectList);
        list.add(this.params);

        return list;
    }
}
