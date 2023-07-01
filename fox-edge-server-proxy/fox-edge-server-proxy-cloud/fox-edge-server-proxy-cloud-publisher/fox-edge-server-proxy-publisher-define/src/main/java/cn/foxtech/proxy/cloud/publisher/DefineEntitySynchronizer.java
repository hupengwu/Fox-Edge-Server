package cn.foxtech.proxy.cloud.publisher;

import cn.foxtech.common.entity.manager.EntityPublishManager;
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
 * Object只记录DeviceObject和TriggerObject的数据记录，它的重要性很高，它在云端只是镜像副本数据
 * 工作过程：每次进行全量同步
 * 1、比对本地和云端的时间戳，判定是否需要进行同步，如果需要同步，就进行后面的流程
 * 2、向云端发出重置操作，云端接收到这个请求后，会清空自己的表数据
 * 3、向云端循环的分页提交本地mysql的全部数据，云端会将数据逐个的插入到自己的表总
 * 4、向云端发出完成操作，云端接收到这个操作后，会标识同步状态为完成
 * 5、至此，两边数据同步结束，重新等待本地的数据和时间戳发生变化，然后重新进行上述流程
 */
@Component
public class DefineEntitySynchronizer {
    private static final Logger logger = Logger.getLogger(DefineEntitySynchronizer.class);

    /**
     * 发布注册服务
     */
    @Autowired
    protected EntityPublishManager entityPublishManager;

    /**
     * 实体管理者
     */
    @Autowired
    private DefineEntityLocalDataBase entityLocalDataBase;

    /**
     * 云端发布者
     */
    @Autowired
    private DefineEntityRemoteCloud entityRemoteCloud;


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

    /**
     * 检查是否需要进行两边的同步
     *
     * @param edgeId         边缘服务器的CPUID
     * @param entityType     实体类型
     * @param localTimeStamp 本地时间戳
     * @return 是否需要进行同步
     * @throws IOException 异常
     */
    public boolean needSync(String edgeId, String entityType, Object localTimeStamp) throws IOException {
        // 查询云端时间戳
        Map<String, Object> timestampMap = this.entityRemoteCloud.queryTimestamp(edgeId, entityType);
        Map<String, Object> cloudData = (Map<String, Object>) timestampMap.get(entityType);
        if (cloudData == null) {
            return false;
        }
        String cloudTimeStamp = (String) cloudData.get("timeStamp");
        String cloudStatus = (String) cloudData.get("status");

        // 场景1：两边时间戳不一致，需要同步
        if (!localTimeStamp.toString().equals(cloudTimeStamp)) {
            return true;
        }

        // 场景2：时间戳一致，但是非complete状态，说明是上次异常中断的同步流程，重新发起同步
        return !"complete".equals(cloudStatus);
    }

    private void syncEntity(String edgeId, String entityType) {
        try {
            // 获得本地redis中的时间戳
            Object localTimeStamp = this.entityPublishManager.getPublishEntityUpdateTime(entityType);
            if (localTimeStamp == null) {
                return;
            }

            // 检查：是否需要发起同步
            if (!this.needSync(edgeId, entityType, localTimeStamp)) {
                return;
            }

            // 阶段1：发出重置请求：此时云端会清空自己的表数据
            this.entityRemoteCloud.publishReset(edgeId, entityType, localTimeStamp.toString());

            // 阶段2：分批上传数据
            int pageSize = 1000;
            long pageId = 0;

            List<Map<String, Object>> entityList;

            do {
                // 分页查询数据
                entityList = this.entityLocalDataBase.selectEntityListByPage(entityType, pageId + 1, pageSize);

                // 发布数据到云端
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("insert", entityList);
                this.entityRemoteCloud.publishEntity(edgeId, entityType, localTimeStamp.toString(), dataMap);

                // 重置下一页的起始pageId
                if (!entityList.isEmpty()) {
                    Map<String, Object> row = entityList.get(entityList.size() - 1);
                    Object id = row.get("id");
                    pageId = NumberUtils.makeLong(id);
                }
            } while (entityList.size() == pageSize);

            // 阶段3：发出完成标记：此时云端会标记已经完成
            this.entityRemoteCloud.publishComplete(edgeId, entityType, localTimeStamp.toString());
        } catch (Exception e) {
            logger.error("同步错误：" + e);
        }
    }
}
