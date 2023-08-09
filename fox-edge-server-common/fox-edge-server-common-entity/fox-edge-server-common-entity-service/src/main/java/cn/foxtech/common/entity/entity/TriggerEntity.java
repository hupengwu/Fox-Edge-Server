package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import java.lang.reflect.Method;

@TableName("tb_trigger")
public class TriggerEntity extends TriggerMethodEntity {
    /**
     * 获得bind方法
     *
     * @return 对象实体
     * @throws NoSuchMethodException 异常信息
     */
    public static Method getInitMethod() throws NoSuchMethodException {
        return TriggerEntity.class.getMethod("init", TriggerMethodEntity.class);
    }

    public void init(TriggerMethodEntity other) {
        this.bind(other);
    }
}
