package cn.foxtech.common.entity.entity;

import cn.foxtech.common.entity.constant.Constants;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 设备的方法
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateMethodBase extends BaseEntity {
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
     * 引擎类型：两种引擎，一种是Java的Jar，一种是JavaScript的jsp，默认是JAVA
     */
    private String engineType;
    /**
     * 业务类型：device、channel
     */
    private String serviceType;
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
        list.add(this.manufacturer);
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
        queryWrapper.eq("manufacturer", this.manufacturer);
        queryWrapper.eq("operate_name", this.operateName);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.engineType);
        list.add(this.operateMode);
        list.add(this.timeout);
        list.add(this.dataType);
        list.add(this.serviceType);
        list.add(this.polling);
        return list;
    }

    public void bind(OperateMethodBase other) {
        this.manufacturer = other.manufacturer;
        this.deviceType = other.deviceType;
        this.operateName = other.operateName;
        this.engineType = other.engineType;
        this.operateMode = other.operateMode;
        this.serviceType = other.serviceType;
        this.dataType = other.dataType;
        this.timeout = other.timeout;
        this.polling = other.polling;

        super.bind(other);
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.manufacturer = (String) map.get("manufacturer");
        this.deviceType = (String) map.get("deviceType");
        this.operateName = (String) map.get("operateName");
        this.engineType = (String) map.get("engineType");
        this.operateMode = (String) map.get("operateMode");
        this.serviceType = (String) map.get("serviceType");
        this.dataType = (String) map.get("dataType");
        this.timeout = (Integer) map.getOrDefault("timeout", 2000);
        this.polling = (Boolean) map.getOrDefault("polling", false);
    }
}
