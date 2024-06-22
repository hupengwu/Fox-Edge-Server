package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateRecordBase extends BaseEntity {
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型名
     */
    private String deviceType;
    /**
     * 设备厂商
     */
    private String manufacturer;

    /**
     * 事件名称
     */
    private String recordName;

    /**
     * 客户端模块
     */
    private String clientModel;

    /**
     * 操作的UUID
     */
    private String operateUuid;

    /**
     * 业务Key：这个可能不是唯一的，不要用它查找唯一性数据，可以用它来筛选数据
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.getId());

        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", this.getId());

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        return list;
    }

    public void bind(OperateRecordBase other) {
        this.deviceName = other.deviceName;
        this.manufacturer = other.manufacturer;
        this.deviceType = other.deviceType;
        this.recordName = other.recordName;
        this.clientModel = other.clientModel;
        this.operateUuid = other.operateUuid;

        super.bind(other);
    }
}
