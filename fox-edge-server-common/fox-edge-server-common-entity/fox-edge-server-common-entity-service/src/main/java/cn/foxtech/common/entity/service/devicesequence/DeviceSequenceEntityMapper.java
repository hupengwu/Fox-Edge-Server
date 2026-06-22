/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.devicesequence;


import cn.foxtech.common.entity.entity.DeviceSequencePo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

//在对应的Mapper 接口上 基础基本的 BaseMapper<T> T是对应的pojo类
@Repository   //告诉容器你是持久层的 @Repository是spring提供的注释，能够将该类注册成Bean
public interface DeviceSequenceEntityMapper extends BaseEntityMapper<DeviceSequencePo> {
    @Insert({"INSERT INTO ${tableName} ( `device_name`, `metric_name`, `timestamp`, `device_value`, `create_time`) VALUES ( #{deviceName}, #{metricName}, #{timestamp}, #{deviceValue}, #{createTime})"})
    void insertRecord(@Param("tableName") String tableName, @Param("deviceName") String deviceName, @Param("metricName") String metricName, @Param("timestamp") Long timestamp,  @Param("deviceValue") String deviceValue,@Param("createTime") Long createTime);

    @Select({"SELECT * FROM ${tableName} WHERE create_time > #{createTime} ORDER BY create_time LIMIT #{pageSize}"})
    List<DeviceSequencePo> select(@Param("tableName") String tableName, @Param("createTime") Long createTime, @Param("pageSize") Integer pageSize);

    @Select({"SELECT * FROM ${tableName} WHERE create_time > #{createTime} AND device_name = #{deviceName} ORDER BY create_time LIMIT #{pageSize}"})
    List<DeviceSequencePo> selectByDeviceName(@Param("tableName") String tableName, @Param("deviceName") String deviceName, @Param("createTime") Long createTime, @Param("pageSize") Integer pageSize);

    @Select({"SELECT * FROM ${tableName} WHERE create_time > #{createTime} AND metric_name = #{metricName} ORDER BY create_time LIMIT #{pageSize}"})
    List<DeviceSequencePo> selectByMetricName(@Param("tableName") String tableName, @Param("metricName") String metricName, @Param("createTime") Long createTime, @Param("pageSize") Integer pageSize);

    @Select({"SELECT * FROM ${tableName} WHERE create_time > #{createTime} AND device_name = #{deviceName}  AND metric_name = #{metricName} ORDER BY create_time LIMIT #{pageSize}"})
    List<DeviceSequencePo> selectByDeviceNameAndMetricName(@Param("tableName") String tableName, @Param("deviceName") String deviceName, @Param("metricName") String metricName, @Param("createTime") Long createTime, @Param("pageSize") Integer pageSize);

    @Delete({"DROP TABLE ${tableName}"})
    void dropTable(@Param("tableName") String tableName);
}

