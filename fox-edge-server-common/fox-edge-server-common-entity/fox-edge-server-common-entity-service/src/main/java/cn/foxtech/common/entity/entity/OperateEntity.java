package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;

import java.lang.reflect.Method;

@TableName("tb_operate")
public class OperateEntity extends OperateMethodEntity {
    /**
     * 获得bind方法
     *
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getInitMethod() throws NoSuchMethodException {
        return OperateEntity.class.getMethod("init", OperateMethodEntity.class);
    }

    public void init(OperateMethodEntity other) {
        this.bind(other);
    }
}
