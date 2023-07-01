package cn.foxtech.common.entity.entity;

import cn.foxtech.common.entity.constant.Constants;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备的方法
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateMethodEntity extends BaseEntity {
    /**
     * 制造厂商
     */
    private String manufacturer;
    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * 操作命令
     */
    private String operateName;
    /**
     * 操作模式
     */
    private String operateMode = Constants.OPERATE_MODE_EXCHANGE;
    /**
     * 返回的数据类型：状态/记录
     */
    private String dataType = "status";
    /**
     * 通信超时
     */
    private Integer timeout;
    /**
     * 该操作是否需要被轮询调度
     */
    private Boolean polling = false;


    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.deviceType);
        list.add(this.operateName);


        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_type", this.deviceType);
        queryWrapper.eq("operate_name", this.operateName);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.operateMode);
        list.add(this.manufacturer);
        list.add(this.timeout);
        list.add(this.dataType);
        return list;
    }

    public void bind(OperateMethodEntity other) {
        this.manufacturer = other.manufacturer;
        this.deviceType = other.deviceType;
        this.operateName = other.operateName;
        this.operateMode = other.operateMode;
        this.dataType = other.dataType;
        this.timeout = other.timeout;
        this.polling = other.polling;

        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateTime(other.getUpdateTime());
    }
}
