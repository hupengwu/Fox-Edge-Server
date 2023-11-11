package cn.foxtech.common.entity.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 这是管理服务使用的操作方式
 * 说明：OperateMethodEntity 和 OperateEntity的作用 是区分设备服务和管理服务，两种结构类似，但是管理者/使用者不同的实体，
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateEntity extends OperateMethodBase {
    /**
     * 脚本引擎
     */
    private Map<String, Object> engineParam = new HashMap<>();

    /**
     * 获得bind方法
     *
     * @return 对象列表
     * @throws NoSuchMethodException 异常信息
     */
    public static Method getInitMethod() throws NoSuchMethodException {
        return OperateEntity.class.getMethod("init", OperateMethodEntity.class);
    }

    public void init(OperateMethodEntity other) {
        this.bind(other);
    }
}
