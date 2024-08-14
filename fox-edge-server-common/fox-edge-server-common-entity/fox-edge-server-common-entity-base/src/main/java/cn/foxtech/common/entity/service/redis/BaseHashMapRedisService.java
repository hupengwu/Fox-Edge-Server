/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.common.entity.service.redis;

import com.fasterxml.jackson.core.JsonParseException;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.utils.common.utils.redis.service.RedisService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseHashMapRedisService {
    /**
     * 数据表的记录数据：重量级数据
     */
    private Map<String, Map<String, Object>> dataMap = new ConcurrentHashMap<>();

    /**
     * 数据表的敏捷状态：轻量级数据
     */
    private Map<String, Long> agileMap = new ConcurrentHashMap<>();

    /**
     * redis的最近刷新时间
     */
    private Long updateTime = 0L;


    /**
     * 生产者：是否需要更新缓存到redis
     */
    private boolean needSave = false;

    /**
     * 是否已经完成初始化
     */
    private boolean inited = false;

    /**
     * 更新通知
     */
    private HashMapRedisNotify notify = null;


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
    protected void bind(HashMapRedisNotify notify) {
        this.notify = notify;
    }

    protected String getHead() {
        return "fox.edge.entity." + this.getEntityType() + ".";
    }

    /**
     * 生产者/消费者：从redis全量装载数据
     */
    protected void loadAllEntities() throws JsonParseException {
        // 读取时间戳
        this.updateTime = this.getRedisService().getCacheObject(this.getHead() + "sync");
        if (this.updateTime == null) {
            this.updateTime = 0L;
        }

        // 读取记录状态
        this.agileMap = this.getRedisService().getCacheMap(this.getHead() + "agile");
        // 读取全量数据
        Map<String, Object> jsonMap = this.getRedisService().getCacheMap(this.getHead() + "data");
        this.dataMap = this.makeEntityMap(jsonMap);
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

    private void makeLong(Map<String, Object> jsonMap){
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
     * 消费者：从redis敏捷装载数据
     */
    protected void loadAgileEntities() throws IOException {
        // 读取时间戳
        this.updateTime = this.getRedisService().getCacheObject(this.getHead() + "sync");

        // 读取敏捷数据：带有每个Entity的UpdateTime
        Map<String, Long> newUpdateTimes = this.getRedisService().getCacheMap(this.getHead() + "agile");


        // 根据时间戳，判定变化的数据
        Set<String> addList = new HashSet<>();
        Set<String> delList = new HashSet<>();
        Set<String> eqlList = new HashSet<>();
        DifferUtils.differByValue(this.agileMap.keySet(), newUpdateTimes.keySet(), addList, delList, eqlList);

        // 检查：记录结构是否不同（发生了增加和删除）
        if (!addList.isEmpty() || !delList.isEmpty()) {
            // 结构不同，全量刷新数据
            this.loadAllEntities();
        } else {
            // 数据变化程度：
            Map<String, Long> diff = this.compare(this.agileMap, newUpdateTimes);
            if (diff.size() * 10 > this.agileMap.size() || diff.size() > 64) {
                // 变化幅度很大：1/10的数据发生变化，或者变化的数据超过10条，一刷新10次，还不如全部更新
                this.loadAllEntities();
            } else {
                for (Map.Entry<String, Long> entry : diff.entrySet()) {
                    // 从redis读取Json格式的数据
                    Map<String, Object> jsonObject = this.getRedisService().getCacheMapValue(this.getHead() + "data", entry.getKey());

                    // 将Integer转换为Long
                    this.makeLong(jsonObject);;

                    Long agile = newUpdateTimes.get(entry.getKey());

                    this.dataMap.put(entry.getKey(), jsonObject);
                    this.agileMap.put(entry.getKey(), agile);
                }
            }

            if (this.notify == null) {
                return;
            }

            // 保存具体数据：删除部分的数据，在从redis读取之前，存在于缓存中，此时要提前取出
            Map<String, Map<String, Object>> addMap = new HashMap<>();
            Map<String, Map<String, Object>> mdyMap = new HashMap<>();
            for (String key : addList) {
                addMap.put(key, this.dataMap.get(key));
            }
            for (String key : diff.keySet()) {
                mdyMap.put(key, this.dataMap.get(key));
            }

            // 检查：是否有变更的数据
            if (addMap.isEmpty() && delList.isEmpty() && mdyMap.isEmpty()) {
                return;
            }

            // 通知变更
            this.notify.notify(this.getEntityType(), this.updateTime, addMap, delList, mdyMap);
        }
    }

    /**
     * 保存全部数据
     *
     * @param mainKey  主键
     * @param cacheMap 缓存数据
     * @param <T>
     */
    private <T> void saveAllEntities(String mainKey, Map<String, T> cacheMap) {
        // 写入数据
        this.getRedisService().setCacheMap(mainKey, cacheMap);

        // 重新读取出来
        Map<String, T> redisRedis = this.getRedisService().getCacheMap(mainKey);

        // 检查：是否有旧的垃圾数据
        Set<String> add = new HashSet<>();
        Set<String> del = new HashSet<>();
        Set<String> eql = new HashSet<>();
        DifferUtils.differByValue(redisRedis.keySet(), cacheMap.keySet(), add, del, eql);

        // 删除旧的垃圾数据
        for (String hashKey : del) {
            this.getRedisService().deleteCacheMap(mainKey, hashKey);
        }
    }

    /**
     * 生产者：向redis全量保存数据
     */
    protected void saveAllEntities() {
        // 保存敏捷数据
        this.saveAllEntities(this.getHead() + "agile", this.agileMap);

        // 将数据保存到redis
        this.saveAllEntities(this.getHead() + "data", this.dataMap);

        // 将时间戳保存到redis
        this.updateTime = System.currentTimeMillis();
        this.getRedisService().setCacheObject(this.getHead() + "sync", this.updateTime);

        this.needSave = false;
    }

    /**
     * 比较数值的差异
     *
     * @param newDatas
     * @param oldDatas
     * @return
     */
    private Map<String, Long> compare(Map<String, Long> newDatas, Map<String, Long> oldDatas) {
        Map<String, Long> diff = new ConcurrentHashMap<>();
        if (newDatas.size() != oldDatas.size()) {
            return diff;
        }

        for (Map.Entry<String, Long> entry : newDatas.entrySet()) {
            long newValue = entry.getValue();
            long oldValue = oldDatas.get(entry.getKey());

            if (newValue != oldValue) {
                diff.put(entry.getKey(), entry.getValue());
            }
        }

        return diff;
    }

    /**
     * 生产者：向redis敏捷保存数据
     * 步骤1.先读取redis的敏捷数据 <br>
     * 步骤2.检查先后敏捷数据是否结构不一致，不一致就全量更新，一致就进行后面的敏捷更新<br>
     * 步骤3.检查先后敏捷数据是否变化太大，变化太大就全量更新（敏捷更新没有意义），变化幅度小才进行后面的敏捷更新<br>
     * 步骤4.敏捷更新时，对发生变化的数据进行记录级更新，逐条写入redis<br>
     */
    protected void saveAgileEntities() {
        Long time = System.currentTimeMillis();

        // 先读取redis里的记录状态
        Map<String, Long> oldUpdateTimes = this.getRedisService().getCacheMap(this.getHead() + "agile");

        // 检查：记录结构是否不同（发生了增加和删除）
        if (DifferUtils.differByValue(oldUpdateTimes.keySet(), this.agileMap.keySet())) {
            // 结构不同，全量刷新数据
            this.saveAllEntities();
        } else {
            // 数据变化程度：
            Map<String, Long> diff = this.compare(this.agileMap, oldUpdateTimes);
            if (diff.size() * 10 > this.agileMap.size() || diff.size() > 64) {
                // 变化幅度很大：1/10的数据发生变化，或者变化的数据超过64条，一刷新64次，还不如全部更新
                this.saveAllEntities();
            } else {
                for (Map.Entry<String, Long> entry : diff.entrySet()) {
                    // 将数据保存到redis
                    this.getRedisService().setCacheMapValue(this.getHead() + "data", entry.getKey(), this.dataMap.get(entry.getKey()));

                    // 把敏捷数据也写入redis和缓存
                    this.getRedisService().setCacheMapValue(this.getHead() + "agile", entry.getKey(), time);
                    this.agileMap.put(entry.getKey(), time);
                }

                // 将时间戳保存到redis
                String entitiesSyncKey = this.getHead() + "sync";
                this.updateTime = time;
                this.getRedisService().setCacheObject(entitiesSyncKey, this.updateTime);

                this.needSave = false;
            }
        }
    }

    /**
     * 清空数据
     */
    protected void cleanAgileEntities() {
        this.getRedisService().deleteObject(this.getHead() + "agile");
        this.getRedisService().deleteObject(this.getHead() + "data");
        this.getRedisService().deleteObject(this.getHead() + "sync");
    }

    protected boolean isNeedLoad() {
        // 读取时间戳
        Long updateTime = this.getRedisService().getCacheObject(this.getHead() + "sync");
        if (updateTime == null) {
            return false;
        }

        return !this.updateTime.equals(updateTime);
    }

    protected boolean isNeedSave() {
        return this.needSave;
    }

    /**
     * 获得一个副本
     *
     * @return
     */
    protected Map<String, Map<String, Object>> getEntitys() {
        return new ConcurrentHashMap<>(this.dataMap);
    }

    /**
     * 获取实体
     *
     * @return 实体
     */
    protected Map<String, Object> getEntity(String entityKey) {
        if (!this.dataMap.containsKey(entityKey)) {
            return null;
        }

        return this.dataMap.get(entityKey);
    }

    /**
     * 获取全局实体列表
     *
     * @return 实体列表
     */
    public List<Map<String, Object>> getEntityList() {
        List<Map<String, Object>> entityList = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> operateEntry : this.dataMap.entrySet()) {
            entityList.add(operateEntry.getValue());
        }

        return entityList;
    }

    public void foreachFinder(IBaseFinder finder) {
        List<Map<String, Object>> entityList = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> operateEntry : this.dataMap.entrySet()) {
            if (finder.compareValue(operateEntry.getValue())) {
                entityList.add(operateEntry.getValue());
            }
        }
    }

    public List<Map<String, Object>> getEntityList(IBaseFinder finder) {
        List<Map<String, Object>> entityList = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> operateEntry : this.dataMap.entrySet()) {
            if (finder.compareValue(operateEntry.getValue())) {
                entityList.add(operateEntry.getValue());
            }
        }

        return entityList;
    }

    public int getEntityCount(IBaseFinder finder) {
        int count = 0;
        for (Map.Entry<String, Map<String, Object>> operateEntry : this.dataMap.entrySet()) {
            if (finder.compareValue(operateEntry.getValue())) {
                count++;
            }
        }

        return count;
    }

    public Map<String, Object> getEntity(Long id) {
        for (Map.Entry<String, Map<String, Object>> operateEntry : this.dataMap.entrySet()) {
            Map<String, Object> value = operateEntry.getValue();
            Object vid = value.get("id");
            if (id.equals(vid)) {
                return operateEntry.getValue();
            }
        }

        return null;
    }

    /**
     * 检查：是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return this.dataMap.isEmpty();
    }
}
