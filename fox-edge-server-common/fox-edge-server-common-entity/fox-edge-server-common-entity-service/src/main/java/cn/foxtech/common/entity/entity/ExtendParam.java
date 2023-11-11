package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ExtendParam {
    /**
     * 绑定的对象列表
     */
    private Set<Object> binds = new HashSet<>();
    /**
     * 扩展的字段列表
     */
    private List<ExtendField> fields = new ArrayList<>();
}
