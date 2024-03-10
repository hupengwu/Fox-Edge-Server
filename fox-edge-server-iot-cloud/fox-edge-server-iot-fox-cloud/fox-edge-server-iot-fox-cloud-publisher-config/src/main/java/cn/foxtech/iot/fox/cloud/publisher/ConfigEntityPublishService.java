package cn.foxtech.iot.fox.cloud.publisher;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.service.redis.AgileMapRedisService;
import cn.foxtech.common.entity.service.redis.HashMapRedisService;
import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.iot.fox.cloud.common.service.EntityManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 云端数据发布者：把本地数据向云端发布
 */
@Component
public class ConfigEntityPublishService {
    private static final Logger logger = Logger.getLogger(ConfigEntityPublishService.class);

    /**
     * 边缘服务器ID
     */
    private final String edgeId = OSInfoUtils.getCPUID();

    /**
     * 数据实体管理者
     */
    @Autowired
    private EntityManageService publishEntityManageService;

    /**
     * 实体同步到云端
     */
    @Autowired
    private ConfigEntityRemoteCloud configEntityRemoteCloud;

    /**
     * 从云端查询时间戳
     *
     * @param entityTypeList
     * @return
     */
    public Map<String, Object> queryTimestamp(Set<String> entityTypeList) throws IOException {
        return this.configEntityRemoteCloud.queryTimestamp(edgeId, entityTypeList);
    }

    /**
     * 从云端查询Rest标记
     *
     * @param entityTypeList 待操作的实体类型列表
     * @return 需要进行reset的实体类型列表
     */
    public Set<String> queryResetFlag(Set<String> entityTypeList) throws IOException {
        return this.configEntityRemoteCloud.queryResetFlag(edgeId, entityTypeList);
    }

    /**
     * 检查：是否两边时间戳保持一致
     *
     * @param entityType
     * @param timestamp
     * @return
     */
    public boolean operatePublishCheck(String entityType, String timestamp) {
        try {
            // 获得EntityType对应的缓存镜像
            AgileMapRedisService baseRedisService = this.publishEntityManageService.getAgileMapService(entityType);

            // 检查：本地redis是否同步成功
            if (baseRedisService.getUpdateTime() == 0) {
                return true;
            }

            String updateTime = Long.toString(baseRedisService.getUpdateTime());
            return updateTime.equals(timestamp);
        } catch (Exception e) {
            logger.error(entityType + ":-->" + e.getMessage());
            return false;
        }
    }

    /**
     * 对云端进行一次强制性全量reset操作
     * 会将本地数据发布一份全量数据给云端，并打上当前的时间戳，用于后面判定同步状态
     *
     * @param entityType 实体类型
     * @return
     */
    public boolean operatePublishReset(String entityType) {
        try {
            // 获得EntityType对应的缓存镜像
            AgileMapRedisService redisService = this.publishEntityManageService.getAgileMapService(entityType);

            // 检查：本地redis是否同步成功
            if (redisService.getUpdateTime() == 0) {
                return true;
            }

            String updateTime = Long.toString(redisService.getUpdateTime());

            // 构造准备发送到云端的dataList数据
            List<Map<String, Object>> dataList = redisService.loadAllEntities();

            // 发布：把数据发布到云端
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("reset", dataList);
            Map<String, Object> respond = this.configEntityRemoteCloud.publishEntity(edgeId, entityType, updateTime, dataMap);

            // 检查：是否返回了异常
            Integer code = (Integer) respond.get("code");
            if (!HttpStatus.SUCCESS.equals(code)) {
                throw new ServiceException("云端未返回成功代码，这是一个异常的返回:" + respond);
            }

            return true;
        } catch (Exception e) {
            logger.error(entityType + ":-->" + e.getMessage());
            return false;
        }
    }

    /**
     * 对云端发布增量更新操作
     *
     * @param entityType
     * @param timestamp
     * @param updateMap
     * @return
     */
    public boolean operatePublishUpdate(String entityType, String timestamp, Map<String, Object> updateMap) {
        try {
            // 获得EntityType对应的缓存镜像
            HashMapRedisService baseRedisService = this.publishEntityManageService.getHashMapService(entityType);

            // 检查：本地redis是否同步成功
            if (baseRedisService.getUpdateTime() == 0) {
                return true;
            }

            // 检查：本地缓存中的时间戳和云端的时间戳是否一致
            String updateTime = Long.toString(baseRedisService.getUpdateTime());
            if (updateTime.equals(timestamp)) {
                return true;
            }

            // 发布：把数据发布到云端
            Map<String, Object> respond = this.configEntityRemoteCloud.publishEntity(edgeId, entityType, updateTime, updateMap);

            // 检查：是否返回了异常
            Integer code = (Integer) respond.get("code");
            if (!HttpStatus.SUCCESS.equals(code)) {
                throw new ServiceException("云端未返回成功代码，这是一个异常的返回:" + respond);
            }

            return true;
        } catch (Exception e) {
            logger.error(entityType + ":-->" + e.getMessage());
            return false;
        }
    }
}

