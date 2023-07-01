package cn.foxtech.common.entity.service.foxsql;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 注意：在启动类中加上这个注解，此时会进行实例化的FoxSqlMapper
 */
public interface FoxSqlMapper {
    @Select("${sql}")
    List<Map<String, Object>> selectMapList(@Param("sql") String sql);

    @Select({"${sql}"})
    Integer selectCount(@Param("sql") String sqlStr);

    @Delete({"${sql}"})
    void delete(@Param("sql") String sql);

    @Insert({"${sql}"})
    void insert(@Param("sql") String sql);
}
