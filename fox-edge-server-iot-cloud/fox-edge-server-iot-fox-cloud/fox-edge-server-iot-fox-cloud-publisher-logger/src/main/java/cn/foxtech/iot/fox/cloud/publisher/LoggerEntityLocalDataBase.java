package cn.foxtech.iot.fox.cloud.publisher;

import cn.foxtech.common.entity.constant.EntityPublishConstant;
import cn.foxtech.common.entity.service.foxsql.FoxSqlService;
import cn.foxtech.iot.fox.cloud.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地数据库
 */
@Component
public class LoggerEntityLocalDataBase {
    /**
     * 实体和数据库表的对应关系
     */
    private final Map<String, String> entityTableMap = new ConcurrentHashMap<>();
    /**
     * 实体和数据库的json字段关系
     */
    private final Map<String, Set<String>> tableJ2M = new ConcurrentHashMap<>();
    @Autowired
    private FoxSqlService foxSqlService;


    @Autowired
    private EntityManageService entityManageService;


    public Set<String> getEntityTypeList() {
        Map<String, Map<String, Object>> entityPublishMap = this.entityManageService.getPublishEntityMap(EntityPublishConstant.value_mode_logger);
        for (String entityType : entityPublishMap.keySet()) {
            Map<String, Object> map = entityPublishMap.get(entityType);
            String sourceName = (String) map.get(EntityPublishConstant.field_source_name);
            this.entityTableMap.put(entityType, sourceName);
        }

        return this.entityTableMap.keySet();
    }

    /**
     * 获得表对应的json字段，并建立映射关系
     *
     * @param tableName 数据库表名称
     * @return json字段大的映射关系
     */
    private Set<String> getTableJsonColumns(String tableName) {
        Set<String> result = this.tableJ2M.get(tableName);
        if (result != null) {
            return result;
        }
        // 检查：表结构是否包含json字段，那么进行转换json对象
        List<String> jsonColumns = this.foxSqlService.selectJsonColumns(tableName);
        Set<String> jsn2obj = new HashSet<>(jsonColumns);

        this.tableJ2M.put(tableName, jsn2obj);
        return jsn2obj;
    }


    /**
     * 分页查询实体列表
     *
     * @param pageId   起始ID
     * @param pageSize 分页大小
     * @return 分页数据
     */
    public List<Map<String, Object>> selectEntityListByPage(String entityType, long pageId, int pageSize) {
        String tableName = entityTableMap.get(entityType);
        if (tableName == null) {
            return null;
        }

        // 获得该表所包含的json字段
        Set<String> jsn2obj = this.getTableJsonColumns(tableName);

        // 分页查询数据
        return this.foxSqlService.selectMapList("SELECT * FROM " + tableName + " WHERE id > " + pageId + " ORDER  BY id ASC LIMIT " + pageSize, true, jsn2obj);
    }


    /**
     * 查询最近ID的记录：这个操作MySQl非常快，不到1毫秒，MYSQl实际上是缓存中执行的
     *
     * @return 最近的一条记录
     */
    public List<Map<String, Object>> selectLastId(String entityType) {
        String tableName = entityTableMap.get(entityType);
        if (tableName == null) {
            return null;
        }

        // 获得该表所包含的json字段
        Set<String> jsn2obj = this.getTableJsonColumns(tableName);

        // 分页查询数据
        return this.foxSqlService.selectMapList("SELECT * FROM " + tableName + " ORDER  BY id DESC LIMIT 1", true, jsn2obj);
    }
}
