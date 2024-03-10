package cn.foxtech.iot.fox.cloud.publisher;

import cn.foxtech.common.utils.number.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 增量同步MySQl表的数据到云端数据库
 * Logger是只需要不断追加的日志记录，它的重要性并不高，只需要在云端保持备份，本地并不需要保存多久
 */
@Component
public class LoggerEntitySynchronizer {
    private static final Logger logger = Logger.getLogger(LoggerEntitySynchronizer.class);

    /**
     * 实体管理者
     */
    @Autowired
    private LoggerEntityLocalDataBase entityLocalDataBase;

    /**
     * 云端发布者
     */
    @Autowired
    private LoggerEntityRemoteCloud entityRemoteCloud;


    public void syncEntity(String edgeId) {
        try {
            Set<String> entityTypeList = this.entityLocalDataBase.getEntityTypeList();

            for (String entityType : entityTypeList) {
                this.syncEntity(edgeId, entityType);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void syncEntity(String edgeId, String entityType) {
        try {
            // 分批处理
            int pageSize = 1000;
            int count;
            do {
                count = this.syncEntity(edgeId, entityType, pageSize);
            } while (count == pageSize);

        } catch (Exception e) {
            logger.error(entityType + ":-->" + e.getMessage());
        }
    }

    public int syncEntity(String edgeId, String entityType, int pageSize) throws IOException {
        // 本地数据库
        List<Map<String, Object>> entityList = this.entityLocalDataBase.selectLastId(entityType);


        Map<String, Object> timestampMap = this.entityRemoteCloud.queryTimestamp(edgeId, entityType);
        Object timestamp = timestampMap.get(entityType);

        // 场景1：本地没有，远端却有：本地被定期清理，云端属于永久备份
        if (entityList.isEmpty() && timestamp != null) {
            return 0;
        }

        // 场景2：本地有，远端没有：以本地为基准，发布数据到远端
        if (!entityList.isEmpty() && timestamp == null) {
            //重新取得数据
            entityList = this.entityLocalDataBase.selectEntityListByPage(entityType, 0, pageSize);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("insert", entityList);
            this.entityRemoteCloud.publishEntity(edgeId, entityType, "", dataMap);
            return entityList.size();
        }

        // 场景3：本地有，远端有：那么进行比对，确认差额的数据
        if (!entityList.isEmpty() && timestamp != null) {
            Object id = entityList.get(0).get("id");
            Long localId = NumberUtils.makeLong(id);
            Long remoteId = Long.parseLong(timestamp.toString());
            if (localId <= remoteId) {
                return 0;
            }

            // 重新查询数据
            entityList = this.entityLocalDataBase.selectEntityListByPage(entityType, remoteId + 1, pageSize);

            // 发布数据到云端
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("insert", entityList);
            this.entityRemoteCloud.publishEntity(edgeId, entityType, "", dataMap);
            return entityList.size();
        }

        return 0;
    }
}
