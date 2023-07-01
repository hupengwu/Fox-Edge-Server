package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 基本实体：ID/创建时间/修改时间
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Long createTime;


    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public String makeServiceKey() {
        return this.makeServiceKeyList().toString();
    }

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public abstract List<Object> makeServiceKeyList();

    /**
     * 是否有NULL的非法key
     *
     * @return 是否合法
     */
    public boolean hasNullServiceKey() {
        List<Object> keys = this.makeServiceKeyList();
        for (Object key : keys) {
            if (key == null) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取业务值
     *
     * @return
     */
    public String makeServiceValue() {
        return makeServiceValueList().toString();
    }

    /**
     * 获取业务值
     *
     * @return
     */
    public abstract List<Object> makeServiceValueList();

    /**
     * 获取查询过滤器
     *
     * @return
     */
    public abstract Object makeWrapperKey();

    /**
     * 比较数值部分
     *
     * @param other
     * @return
     */
    public boolean equalsValue(BaseEntity other) {
        if (other == null) {
            return false;
        }

        if (!this.equals(this.id, other.id)) {
            return false;
        }
        if (!this.equals(this.createTime, other.createTime)) {
            return false;
        }
        if (!this.equals(this.updateTime, other.updateTime)) {
            return false;
        }

        String thisKey = this.makeServiceValue();
        String otherKey = other.makeServiceValue();
        return otherKey.equals(thisKey);
    }

    private boolean equals(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }

        return a != null && a.equals(b);
    }
}
