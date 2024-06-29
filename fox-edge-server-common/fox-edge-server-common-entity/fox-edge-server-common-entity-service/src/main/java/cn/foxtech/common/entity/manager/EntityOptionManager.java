package cn.foxtech.common.entity.manager;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.entity.constant.EntityOptionConstant;
import cn.foxtech.common.entity.service.foxsql.FoxSqlService;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.redis.status.RedisStatusConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 选项信息管理
 * 说明：引入该组件的生产者应用，可以通过该组件将需要对数据库表进行Option操作，写入redis中
 * 那么manage服务，就能够知道是否允许前端进行option操作
 *
 * 注意事项：
 * 1、因为大多数应用是不需要用到这个Option组件的，所以本组件的两个Autowired默认是不进行强制组装的
 */
@Component
public class EntityOptionManager {
    /**
     * 表结构信息：数据库表名称-字段名称-字段类型
     */
    private final Map<String, Map<String, String>> tables = new ConcurrentHashMap<>();
    /**
     * 默认不强制实例化
     */
    @Autowired(required = false)
    private ServiceStatus serviceStatus;

    /**
     * 默认不请强制实例化
     */
    @Autowired(required = false)
    private FoxSqlService foxSqlService;

    /**
     * 填写允许进行option的对象实体和表/字段的关系
     *
     * @param entityType 实体类型
     * @param tableName  表名称
     * @param fieldNames 字段名称列表
     */
    public void setOptionEntity(String entityType, String tableName, String[] fieldNames) {
        List<String> list = Arrays.stream(fieldNames).collect(Collectors.toList());
        this.setOptionEntity(entityType, tableName, list);
    }

    /**
     * 填写允许进行option的对象实体和表/字段的关系
     *
     * @param entityType 实体类型
     * @param tableName  表名称
     * @param fieldNames 字段名称列表
     */
    public void setOptionEntity(String entityType, String tableName, List<String> fieldNames) {
        Map<String, Object> publishEntity = (Map<String, Object>) this.serviceStatus.getProducerData().computeIfAbsent(RedisStatusConstant.field_option_entity, k -> new HashMap<>());
        Map<String, Object> entity = (Map<String, Object>) publishEntity.computeIfAbsent(entityType, k -> new HashMap<>());
        entity.put(EntityOptionConstant.filed_option_table_name, tableName);
        entity.put(EntityOptionConstant.filed_option_field_name, fieldNames);
    }

    /**
     * 是否允许操作
     *
     * @param entityType 实体类型
     * @param fieldName  字段名称
     * @return 是否允许进行option操作
     */
    public boolean isPermit(String entityType, String fieldName) {
        // 获得实体的数据库字段列表
        List<String> fields = this.getFields(entityType);
        if (fields == null) {
            return false;
        }

        // 是否包含该字段
        return fields.contains(fieldName);
    }

    /**
     * 是否允许
     *
     * @param entityType 实体类型
     * @param fieldName1 字段1
     * @param fieldName2 字段2
     * @return 返回值
     */
    public boolean isPermit(String entityType, String fieldName1, String fieldName2) {
        // 获得允许的字段列表
        List<String> fields = this.getFields(entityType);
        if (fields == null) {
            return false;
        }


        return fields.contains(fieldName1) && fields.contains(fieldName2);
    }

    /**
     * 获得字段列表
     *
     * @param entityType 实体类型
     * @return 允许的字段列表
     */
    private List<String> getFields(String entityType) {
        // 检查：通告的实体信息是否存在
        Map<String, Object> map = this.getOptionEntity(entityType);
        if (map == null) {
            return null;
        }

        // 检查：表名称是否存在
        String tableName = (String) map.get(EntityOptionConstant.filed_option_table_name);
        if (tableName == null || tableName.isEmpty()) {
            return null;
        }

        // 检查：表字段是否存在
        return (List<String>) map.get(EntityOptionConstant.filed_option_field_name);
    }

    /**
     * 获得允许的数据库名称
     *
     * @param entityType 实体类型
     * @return 数据库名称
     */
    public String getTableName(String entityType) {
        // 检查：通告的实体信息是否存在
        Map<String, Object> map = this.getOptionEntity(entityType);
        if (map == null) {
            return null;
        }

        // 提取表名称
        return (String) map.get(EntityOptionConstant.filed_option_table_name);
    }


    /**
     * 获得被允许的实体的配置信息
     *
     * @param entityType 实体名称
     * @return 配置信息
     */
    public Map<String, Object> getOptionEntity(String entityType) {
        for (Object statusValue : this.serviceStatus.getConsumerData().values()) {
            Map<String, Object> value = (Map<String, Object>)  statusValue;
            if (value == null) {
                continue;
            }

            Map<String, Object> optionEntity = (Map<String, Object>) value.get(RedisStatusConstant.field_option_entity);
            if (optionEntity == null) {
                continue;
            }

            Map<String, Object> entity = (Map<String, Object>) optionEntity.get(entityType);
            if (entity == null) {
                continue;
            }

            return entity;
        }

        return null;
    }

    /**
     * 字段是否为数字类型的字段
     *
     * @param entityType 实体类型
     * @param fieldName 字段名
     * @return 是否数字字段
     */
    public boolean isNumberField(String entityType, String fieldName) {
        String fieldType = this.getFiledType(entityType, fieldName);
        if (fieldType == null) {
            return false;
        }

        return fieldType.equals("bigint") || fieldType.equals("int") || fieldType.equals("double") || fieldType.equals("float");
    }

    /**
     * 获得字段信息
     *
     * @param entityType 实体名称
     * @param fieldName  字段名称
     * @return 字段类型
     */
    public String getFiledType(String entityType, String fieldName) {
        // 获得注册实体的数据库表名称
        String tableName = this.getTableName(entityType);
        if (tableName == null || tableName.isEmpty()) {
            return null;
        }

        // 检查：是否已经缓存了表信息，如果缓存了就从缓存中取得字段信息
        Map<String, String> f2t = this.tables.get(tableName);
        if (f2t != null) {
            return f2t.get(fieldName);
        }

        // 从数据库中查询表信息
        List<Map<String, Object>> mapList = this.foxSqlService.selectColumns(tableName);
        if (mapList == null || mapList.isEmpty()) {
            return null;
        }

        // 把表信息重新构造
        f2t = new ConcurrentHashMap<>();
        for (Map<String, Object> map : mapList) {
            String field = (String) map.get("Field");
            String type = (String) map.get("Type");
            f2t.put(field, type);
        }

        // 缓存表信息
        this.tables.put(tableName, f2t);


        // 返回字段信息
        return f2t.get(fieldName);
    }

}
