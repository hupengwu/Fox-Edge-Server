package cn.foxtech.common.entity.service.redis;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.utils.common.utils.redis.service.RedisService;
import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * redis生产者/消费者的实现类，生产者/消费者分别把这些函数暴露给业务代码使用<br>
 * <br>
 * 背景：redis毕竟也是RPC访问，它单次在本地计算机响应时间都要2毫秒，要快速访问缓存10W次，
 * 至少得要200秒，简直就不可接受。<br>
 * <br>
 * 方案：在进程内存中开辟一个内存map，后台线程对内存map跟redis通过时间戳进行数据同步，那么
 * 进程的代码就可以直接访问map，减少往返时间。实测效果是访问10W次，都是微秒级的。<br>
 * <br>
 * 使用：分为生产者和消费者。<br>
 * 生产者负责维护数据更新，当有数据编码的时候，将自己的map数据发布到redis并更新时间戳。<br>
 * 消费者周期性读取redis的时间戳，当发现变化的时候，就将redis数据同步到自己的map<br>
 */
public abstract class BaseRedisService {
    /**
     * 数据表的记录数据：重量级数据
     */
    private Map<String, BaseEntity> dataMap = new ConcurrentHashMap<>();
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
    private BaseConsumerTypeNotify typeNotify = null;
    /**
     * 更新通知
     */
    private final List<BaseConsumerEntityNotify> entityNotify = new CopyOnWriteArrayList<>();

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
    protected void bind(BaseConsumerTypeNotify notify) {
        this.typeNotify = notify;
    }

    protected List<BaseConsumerEntityNotify> getEntityNotify(){
        return this.entityNotify;
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
        this.dataMap = this.makeJson2EntityMap(jsonMap);
    }

    /**
     * 将JSON MAP转换回Entity对象
     *
     * @param jsonMap
     * @return
     */
    private Map<String, BaseEntity> makeJson2EntityMap(Map<String, Object> jsonMap) throws JsonParseException {
        Class entityClass = BaseEntityClassFactory.getInstance(this.getEntityType());

        Map<String, BaseEntity> result = new ConcurrentHashMap<>();
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            Map<String, Object> jsonObject = (Map<String, Object>) entry.getValue();
            BaseEntity entity = (BaseEntity) JsonUtils.buildObject(jsonObject, entityClass);
            result.put(entry.getKey(), entity);
        }

        return result;
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

        Map<String, Long> diff = this.compare(this.agileMap, newUpdateTimes);

        // 检查：记录结构是否不同（发生了增加和删除）
        if (!addList.isEmpty() || !delList.isEmpty()) {
            // 结构不同，全量刷新数据
            this.loadAllEntities();
        } else {
            // 数据变化程度：
            if (diff.size() * 10 > this.agileMap.size() || diff.size() > 64) {
                // 变化幅度很大：1/10的数据发生变化，或者变化的数据超过10条，一刷新10次，还不如全部更新
                this.loadAllEntities();
            } else {
                Class entityClass = BaseEntityClassFactory.getInstance(this.getEntityType());

                for (Map.Entry<String, Long> entry : diff.entrySet()) {
                    // 从redis读取Json格式的数据
                    Map<String, Object> jsonObject = this.getRedisService().getCacheMapValue(this.getHead() + "data", entry.getKey());

                    // 利用Class构造出实际的Entity对象
                    BaseEntity redisEntity = (BaseEntity) JsonUtils.buildObjectWithoutException(jsonObject, entityClass);

                    Long agile = newUpdateTimes.get(entry.getKey());

                    this.dataMap.put(entry.getKey(), redisEntity);
                    this.agileMap.put(entry.getKey(), agile);
                }
            }
        }


        // 类型级别的通知
        this.notifyType(addList, delList, diff);

        // 实体级别的通知
        this.notifyEntity(addList, delList, diff);
    }

    private void notifyEntity(Set<String> addList, Set<String> delList, Map<String, Long> diff) {
        if (this.entityNotify.isEmpty()) {
            return;
        }

        for (BaseConsumerEntityNotify notify : this.entityNotify) {
            String key = notify.getServiceKey();
            BaseEntity entity = this.dataMap.get(key);

            if (addList.contains(key)) {
                notify.notifyInsert(entity);
                continue;
            }
            if (diff.containsKey(key)) {
                notify.notifyUpdate(entity);
                continue;
            }
            if (delList.contains(key)) {
                notify.notifyDelete(key);
                continue;
            }
        }

    }

    private void notifyType(Set<String> addList, Set<String> delList, Map<String, Long> diff) {
        if (this.typeNotify == null) {
            return;
        }
        // 保存具体数据：删除部分的数据，在从redis读取之前，存在于缓存中，此时要提前取出
        Map<String, BaseEntity> addMap = new HashMap<>();
        Map<String, BaseEntity> mdyMap = new HashMap<>();
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
        this.typeNotify.notify(this.getEntityType(), this.updateTime, addMap, delList, mdyMap);
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
    protected Map<String, BaseEntity> getEntitys() {
        return new ConcurrentHashMap<>(this.dataMap);
    }

    /**
     * 获取实体
     *
     * @return 实体
     */
    protected BaseEntity getEntity(String entityKey) {
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
    public List<BaseEntity> getEntityList() {
        List<BaseEntity> entityList = new ArrayList<>();

        for (Map.Entry<String, BaseEntity> operateEntry : this.dataMap.entrySet()) {
            entityList.add(operateEntry.getValue());
        }

        return entityList;
    }

    public void foreachFinder(IBaseFinder finder) {
        List<BaseEntity> entityList = new ArrayList<>();

        for (Map.Entry<String, BaseEntity> operateEntry : this.dataMap.entrySet()) {
            if (finder.compareValue(operateEntry.getValue())) {
                entityList.add(operateEntry.getValue());
            }
        }
    }

    public List<BaseEntity> getEntityList(IBaseFinder finder) {
        List<BaseEntity> entityList = new ArrayList<>();

        for (Map.Entry<String, BaseEntity> entry : this.dataMap.entrySet()) {
            if (finder.compareValue(entry.getValue())) {
                entityList.add(entry.getValue());
            }
        }

        return entityList;
    }

    public BaseEntity getEntity(IBaseFinder finder) {
        for (Map.Entry<String, BaseEntity> entry : this.dataMap.entrySet()) {
            if (finder.compareValue(entry.getValue())) {
                return entry.getValue();
            }
        }

        return null;
    }


    public int getEntityCount(IBaseFinder finder) {
        int count = 0;
        for (Map.Entry<String, BaseEntity> entry : this.dataMap.entrySet()) {
            if (finder.compareValue(entry.getValue())) {
                count++;
            }
        }

        return count;
    }

    public BaseEntity getEntity(Long id) {
        for (Map.Entry<String, BaseEntity> entry : this.dataMap.entrySet()) {
            if (id.equals(entry.getValue().getId())) {
                return entry.getValue();
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


    /**
     * 插入实体
     *
     * @param entity 实体
     */
    protected void insertEntity(BaseEntity entity) {
        this.putEntity(entity);
    }

    /**
     * 根据Key特征，查询实体
     *
     * @param entity 实体
     */
    protected BaseEntity selectEntity(BaseEntity entity) {
        return this.getEntity(entity.makeServiceKey());
    }


    /**
     * 根据Key特征，更新实体
     *
     * @param entity 实体
     */
    protected void updateEntity(BaseEntity entity) {
        String entityKey = entity.makeServiceKey();

        // 检查：缓存中是否存在该对象
        BaseEntity existEntity = this.dataMap.get(entityKey);
        if (existEntity == null) {
            return;
        }


        // 替换缓存对象
        this.putEntity(entity);
    }

    /**
     * 根据Key特征，删除实体
     *
     * @param entityKey 实体
     */
    protected void deleteEntity(String entityKey) {
        // 检查：缓存中是否存在该对象
        BaseEntity existEntity = this.getEntitys().get(entityKey);
        if (existEntity == null) {
            return;
        }


        this.removeEntity(entityKey);
        this.agileMap.remove(entityKey);
        this.needSave = true;
    }

    /**
     * 将整个缓存设置为新的实体列表
     *
     * @param dataMap
     */
    protected void setDataMap(List<BaseEntity> dataMap) {
        this.dataMap = new ConcurrentHashMap<>();
        for (BaseEntity entity : dataMap) {
            this.dataMap.put(entity.makeServiceKey(), entity);
        }

        // 初始化阶段，填写记录更新更新状态和结构有效时间戳
        Long updateTime = System.currentTimeMillis();
        this.agileMap = new ConcurrentHashMap<>();
        for (BaseEntity entity : dataMap) {
            this.agileMap.put(entity.makeServiceKey(), updateTime);
        }

        this.needSave = true;
    }

    private void putEntity(BaseEntity entity) {
        this.dataMap.put(entity.makeServiceKey(), entity);
        this.agileMap.put(entity.makeServiceKey(), System.currentTimeMillis());
        this.needSave = true;
    }

    private void removeEntity(String serviceKey) {
        this.dataMap.remove(serviceKey);
        this.agileMap.remove(serviceKey);
        this.needSave = true;
    }
}
