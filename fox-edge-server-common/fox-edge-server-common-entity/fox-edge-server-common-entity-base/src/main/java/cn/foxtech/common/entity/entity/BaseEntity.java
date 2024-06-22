package cn.foxtech.common.entity.entity;

import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.core.exception.ServiceException;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

    public BaseEntity build(Map<String, Object> map) {
        throw new ServiceException(this.getClass().getSimpleName() + "未重载BaseEntity build(Map<String,Object> map)函数");
    }

    public void bind(Map<String, Object> map) {
        this.setId(NumberUtils.makeLong(map.get("id")));
        this.setCreateTime(NumberUtils.makeLong(map.get("createTime")));
        this.setUpdateTime(NumberUtils.makeLong(map.get("updateTime")));
    }

    public void bind(BaseEntity other) {
        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateTime(other.getUpdateTime());
    }

    private boolean equals(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }

        return a != null && a.equals(b);
    }
}
