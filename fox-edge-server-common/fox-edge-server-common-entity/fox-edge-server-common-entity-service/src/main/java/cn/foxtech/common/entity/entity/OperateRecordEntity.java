package cn.foxtech.common.entity.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateRecordEntity extends OperateRecordBase {
    /**
     * JSON参数
     */
    private Map<String, Object> recordParam = new HashMap<>();

    /**
     * JSON数据
     */
    private Map<String, Object> recordData = new HashMap<>();

    /**
     * 业务Key：这个可能不是唯一的，不要用它查找唯一性数据，可以用它来筛选数据
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        return super.makeServiceKeyList();
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        return super.makeWrapperKey();
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.recordData);
        list.add(this.recordParam);

        return list;
    }
}
