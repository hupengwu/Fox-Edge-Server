package cn.foxtech.iot.fox.publish.service.publish;

import cn.foxtech.common.entity.service.foxsql.FoxSqlService;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.common.utils.string.StringUtils;
import cn.foxtech.iot.fox.publish.service.remote.RemoteService;
import cn.foxtech.iot.fox.publish.service.service.TimeIntervalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RecordEntityPublish {
    @Autowired
    private TimeIntervalService timeIntervalService;

    @Autowired
    private RemoteService remoteCloud;
    @Autowired
    private FoxSqlService foxSqlService;


    public void publish(String entityType, String tableName) {
        // 检查：是否到了执行的时间间隔
        if (!this.timeIntervalService.testLastTime(StringUtils.camelName(entityType))) {
            return;
        }

        try {
            // 分批处理
            int pageSize = 100;
            int count;
            do {
                count = this.syncEntity(entityType, tableName, pageSize, 10 * 1000);
            } while (count == pageSize);
        } catch (Exception e) {
            //   logger.error(entityType + ":-->" + e.getMessage());
        }
    }

    public List<Map<String, Object>> selectLastId(String tableName) {
        return this.foxSqlService.selectMapList("SELECT * FROM " + tableName + " ORDER  BY id DESC LIMIT 1", true);
    }

    public List<Map<String, Object>> selectEntityListByPage(String tableName, long pageId, int pageSize) {
        return this.foxSqlService.selectMapList("SELECT * FROM " + tableName + " WHERE id > " + pageId + " ORDER  BY id ASC LIMIT " + pageSize, true);
    }

    private int syncEntity(String entityType, String tableName, int pageSize, long timeOut) throws IOException, InterruptedException {
        // 本地数据库
        List<Map<String, Object>> entityList = this.selectLastId(tableName);
        if (entityList.isEmpty()) {
            return 0;
        }

        // 查询云端的时间戳
        Object timestamp = this.remoteCloud.queryTimestamp(entityType, timeOut);
        if (timestamp == null) {
            return 0;
        }

        // 进行比对，确认差额的数据
        Object id = entityList.get(0).get("id");
        Long localId = NumberUtils.makeLong(id);
        Long remoteId = NumberUtils.makeLong(timestamp);
        if (localId <= remoteId) {
            return 0;
        }

        // 查询数据
        entityList = this.selectEntityListByPage(tableName, remoteId + 1, pageSize);

        // 发布数据到云端
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("insert", entityList);
        this.remoteCloud.publishEntity(entityType.toLowerCase(), dataMap);
        return entityList.size();
    }
}
