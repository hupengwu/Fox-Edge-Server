package cn.foxtech.common.entity.service.redis;

import cn.foxtech.common.entity.entity.BaseEntity;
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
     * 更新通知
     */
    private AgileMapRedisNotify notify = null;


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

    /**
     * 绑定通知
     *
     * @param notify
     */
    protected void bind(AgileMapRedisNotify notify) {
        this.notify = notify;
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
     * 注意：触发器模式下，该函数才会工作
     */
    public void loadAgileEntities() {
        if (this.notify == null) {
            return;
        }

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

    /**
     * 消费者：初始化从redis敏捷装载数据
     * 注意：独立线程轮询模式，该函数才会工作
     */
    public List<Map<String, Object>> loadAllEntities() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (this.notify != null) {
            return resultList;
        }

        // 读取时间戳
        Long sync = this.getRedisService().getCacheObject(this.getHead() + "sync");
        if (sync == null) {
            return resultList;
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

        // 读取data，并返回给外部
        Map<String, Object> jsonMap = this.getRedisService().getCacheMap(this.getHead() + "data");
        for (Object data : jsonMap.values()) {
            if (data != null && data instanceof Map) {
                resultList.add((Map<String, Object>) data);
            }
        }

        return resultList;
    }


    /**
     * 装载变化的数据
     * 注意：独立线程轮询模式，该函数才会工作
     *
     * @param addMap 新增
     * @param delSet 删除
     * @param mdyMap 修改
     * @throws IOException 异常
     */
    public void loadChangeEntities(Map<String, Object> addMap, Set<String> delSet, Map<String, Object> mdyMap) throws IOException {
        if (this.notify != null) {
            return;
        }

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

    public void loadChangeEntities(Map<String, BaseEntity> addMap, Set<String> delSet, Map<String, BaseEntity> mdyMap, BaseEntity builder) throws IOException {
        if (this.notify != null) {
            return;
        }
        
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

        Set<String> loads = new HashSet<>();
        loads.addAll(addList);
        loads.addAll(mdfList);

        // 读取全量的date：redis基础组件，没有读取指定数据的Map接口
        List<Map<String, Object>> mapList = this.getRedisService().getCacheMap(this.getHead() + "data", loads);

        // 转换数据
        Map<String, BaseEntity> dataMap = this.makeEntityMap(mapList, builder);

        // 新增的数据
        for (String key : addList) {
            BaseEntity data = dataMap.get(key);
            if (data == null) {
                continue;
            }

            Long newUpdateTime = newUpdateTimes.get(key);
            this.agileMap.put(key, newUpdateTime);

            addMap.put(key, data);
        }

        // 修改的数据
        for (String key : mdfList) {
            BaseEntity data = dataMap.get(key);
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

    private Map<String, BaseEntity> makeEntityMap(List<Map<String, Object>> mapList, BaseEntity builder) throws JsonParseException {
        Map<String, BaseEntity> result = new ConcurrentHashMap<>();
        for (Map<String, Object> map : mapList) {
            BaseEntity entity = builder.build(map);
            if (entity == null) {
                continue;
            }

            result.put(entity.makeServiceKey(), entity);
        }

        return result;
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
