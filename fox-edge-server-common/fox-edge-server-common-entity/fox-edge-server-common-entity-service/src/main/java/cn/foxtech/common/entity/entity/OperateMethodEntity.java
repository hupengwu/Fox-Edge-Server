package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这是设备服务使用的OperateMethodEntity
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateMethodEntity extends OperateMethodBase {
    /**
     * JSP引擎的配置
     */
    private Map<String, Object> engineParam = new HashMap<>();

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.engineParam);
        return list;
    }

    public void bind(OperateMethodEntity other) {
        super.bind(other);
    }
}
