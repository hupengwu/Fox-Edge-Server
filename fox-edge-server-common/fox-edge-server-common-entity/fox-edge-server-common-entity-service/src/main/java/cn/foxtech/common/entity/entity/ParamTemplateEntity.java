package cn.foxtech.common.entity.entity;

import lombok.*;

import java.util.HashMap;
import java.util.List;

/**
 * 将离散的设备参数聚合成复合的设备参数
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ParamTemplateEntity extends ParamTemplateBase {
    /**
     * 配置集合
     */
    private Object templateParam = new HashMap<>();

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.templateParam);

        return list;
    }
}
