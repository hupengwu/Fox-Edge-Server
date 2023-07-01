package cn.foxtech.common.entity.service.mybatis;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface BaseEntityMapper<T> extends BaseMapper<T> {
    @Insert({"${sql}"})
    void executeInsert(@Param("sql") String sql);

    //所有的crud都编写完成了
    @Select({"${sql}"})
    List<T> executeSelectData(@Param("sql") String sql);

    @Select({"${sql}"})
    Integer executeSelectCount(@Param("sql") String sql);

    @Delete({"${sql}"})
    void executeDelete(@Param("sql") String sql);
}
