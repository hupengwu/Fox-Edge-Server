package cn.foxtech.common.entity.service.redis;

import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.utils.common.utils.redis.service.RedisService;
import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseAgileMapRedisService {
    /**
     * 数据表的敏捷状态：轻量级数据
     */
    private final Map<String, Long> agileMap = new ConcurrentHashMap<>();

    /**
     * redis的最近刷新时间
     */
    private Long updateTime = 0L;

    /**
     * 是否已经完成初始化
     */
    private boolean inited = false;


    /**
     * 从派生类中，获得redisService
     *
     * @return
     */
    public abstract RedisService getRedisService();

    /**
     * 从派生类中，获得处理的实体类型
     *
     * @return
     */
    public abstract String getEntityType();

    /**
     * 是否已经完成初始化
     */
    public boolean isInited() {
        return this.inited;
    }

    /**
     * 标识已经完成初始化
     */
    public void setInited() {
        this.inited = true;
    }

    /**
     * 获取时间戳
     *
     * @return 时间戳
     */
    public long getUpdateTime() {
        return updateTime;
    }


    protected String getHead() {
        return "fox.edge.entity." + this.getEntityType() + ".";
    }


    private void makeLong(Map<String, Object> jsonMap) {
        jsonMap.put("id", this.makeLong(jsonMap.get("id")));
        jsonMap.put("createTime", this.makeLong(jsonMap.get("createTime")));
        jsonMap.put("updateTime", this.makeLong(jsonMap.get("updateTime")));
    }

    private Long makeLong(Object object) {
        if (object instanceof Long) {
            return (Long) object;
        }
        if (object instanceof Integer) {
            return ((Integer) object).longValue();
        }
        if (object instanceof Short) {
            return ((Short) object).longValue();
        }

        return null;
    }

    /**
     * 消费者：初始化从redis敏捷装载数据
     */
    public void loadAgileEntities() {
        // 读取时间戳
        Long sync = this.getRedisService().getCacheObject(this.getHead() + "sync");
        if (sync == null) {
            return;
        } else {
            this.updateTime = sync;
        }

        // 读取敏捷数据：带有每个Entity的UpdateTime
        Map<String, Long> newUpdateTimes = this.getRedisService().getCacheMap(this.getHead() + "agile");
        if (newUpdateTimes == null) {
            newUpdateTimes = new HashMap<>();
        }

        this.agileMap.clear();
        this.agileMap.putAll(newUpdateTimes);
    }

    public List<Map<String, Object>> loadAllEntities() {
        // 装载sync和agile
        this.loadAgileEntities();

        // 读取data，并返回给外部
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> jsonMap = this.getRedisService().getCacheMap(this.getHead() + "data");
        for (Object data : jsonMap.values()) {
            if (data != null && data instanceof Map){
                resultList.add((Map<String, Object>)data);
            }
        }

        return resultList;
    }


    /**
     * 装载变化的数据
     *
     * @param addMap 新增
     * @param delSet 删除
     * @param mdyMap 修改
     * @throws IOException 异常
     */
    public void loadChangeEntities(Map<String, Object> addMap, Set<String> delSet, Map<String, Object> mdyMap) throws IOException {
        // 读取时间戳
        Long sync = this.getRedisService().getCacheObject(this.getHead() + "sync");
        if (sync == null) {
            return;
        } else {
            this.updateTime = sync;
        }

        // 读取敏捷数据：带有每个Entity的UpdateTime
        Map<String, Long> newUpdateTimes = this.getRedisService().getCacheMap(this.getHead() + "agile");
        if (newUpdateTimes == null) {
            newUpdateTimes = new HashMap<>();
        }

        // 根据时间戳，判定变化的数据
        Set<String> addList = new HashSet<>();
        Set<String> delList = new HashSet<>();
        Set<String> eqlList = new HashSet<>();
        DifferUtils.differByValue(this.agileMap.keySet(), newUpdateTimes.keySet(), addList, delList, eqlList);

        Set<String> mdfList = new HashSet<>();
        for (String key : eqlList) {
            Long newUpdateTime = newUpdateTimes.get(key);
            Long oldUpdateTime = this.agileMap.get(key);
            if (newUpdateTime.equals(oldUpdateTime)) {
                continue;
            }

            mdfList.add(key);
        }

        if (addList.isEmpty() && delList.isEmpty() && mdfList.isEmpty()) {
            return;
        }

        // 读取全量的date：redis基础组件，没有读取指定数据的Map接口
        Map<String, Object> jsonMap = this.getRedisService().getCacheMap(this.getHead() + "data");
        Map<String, Map<String, Object>> dataMap = this.makeEntityMap(jsonMap);

        // 新增的数据
        for (String key : addList) {
            Map<String, Object> data = dataMap.get(key);
            if (data == null) {
                continue;
            }

            Long newUpdateTime = newUpdateTimes.get(key);
            this.agileMap.put(key, newUpdateTime);

            addMap.put(key, data);
        }

        // 修改的数据
        for (String key : mdfList) {
            Map<String, Object> data = dataMap.get(key);
            if (data == null) {
                continue;
            }

            Long newUpdateTime = newUpdateTimes.get(key);
            this.agileMap.put(key, newUpdateTime);

            mdyMap.put(key, data);
        }

        // 删除的数据
        delSet.addAll(delList);
    }

    /**
     * 将JSON MAP转换回Entity对象
     *
     * @param jsonMap
     * @return
     */
    private Map<String, Map<String, Object>> makeEntityMap(Map<String, Object> jsonMap) throws JsonParseException {

        Map<String, Map<String, Object>> result = new ConcurrentHashMap<>();
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String key = entry.getKey();
            Map<String, Object> value = (Map<String, Object>) entry.getValue();

            this.makeLong(value);

            result.put(key, value);
        }

        return result;
    }


    public boolean isNeedLoad() {
        // 读取时间戳
        Long updateTime = this.getRedisService().getCacheObject(this.getHead() + "sync");
        if (updateTime == null) {
            return false;
        }

        return !this.updateTime.equals(updateTime);
    }

    /**
     * 检查：是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return this.agileMap.isEmpty();
    }
}
