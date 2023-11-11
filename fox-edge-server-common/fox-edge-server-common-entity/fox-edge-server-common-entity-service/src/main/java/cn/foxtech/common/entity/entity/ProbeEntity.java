package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ProbeEntity extends BaseEntity {
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * 设备厂商
     */
    private String manufacturer;

    /**
     * 操作命令
     */
    private String operateName;

    /**
     * 配置集合
     */
    private Map<String, Object> params = new HashMap<>();

    /**
     * 操作周期
     */
    private Map<String, Object> period = new HashMap<>();


    /**
     * 业务Key
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
        list.add(this.period);
        return list;
    }

}
