package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_probe")
public class ProbePo extends BaseEntity {
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 操作命令
     */
    private String operateName;

    /**
     * 操作参数
     */
    private String operateParam;

    /**
     * 操作周期
     */
    private String operatePeriod;


    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.deviceName);
        list.add(this.deviceType);
        list.add(this.operateName);
        list.add(this.operateParam);


        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_name", this.deviceName);
        queryWrapper.eq("device_type", this.deviceType);
        queryWrapper.eq("operate_name", this.operateName);
        queryWrapper.eq("operate_param", this.operateParam);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.operatePeriod);
        return list;
    }
}
