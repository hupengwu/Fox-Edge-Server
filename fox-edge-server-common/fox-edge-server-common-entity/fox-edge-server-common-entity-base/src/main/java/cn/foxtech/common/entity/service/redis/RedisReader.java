package cn.foxtech.common.entity.service.redis;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis直读：它是直接读取redis中的数据，不需要缓存开销，但是会有延迟
 * redis直读适合数量较大，对延迟不敏感的应用场景
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RedisReader {
    /**
     * redis
     */
    private RedisTemplate redisTemplate;

    /**
     * 边缘服务器：数据库表的名称
     */
    private String entityType;

    private String getHead() {
        return "fox.edge.entity." + this.getEntityType() + ".";
    }

    /**
     * 读取敏捷数据：敏捷数据是感知变化的时间戳数据
     *
     * @return
     */
    public synchronized Map<String, Object> readAgileMap() {
        return this.redisTemplate.opsForHash().entries(this.getHead() + "agile");
    }

    /**
     * 读取同步标记
     *
     * @return
     */
    public synchronized Object readSync() {
        return this.redisTemplate.opsForValue().get(this.getHead() + "sync");
    }

    /**
     * 读全部数据
     *
     * @return
     */
    public synchronized Map<String, BaseEntity> readEntityMap() throws JsonParseException {
        Map<String, Object> dataJsn = this.redisTemplate.opsForHash().entries(this.getHead() + "data");
        Map<String, BaseEntity> dataRds = this.makeJson2EntityMap(dataJsn);

        return dataRds;
    }

    public synchronized Map<String, Object> readHashMap() {
        Map<String, Object> dataJsn = this.redisTemplate.opsForHash().entries(this.getHead() + "data");
        return dataJsn;
    }

    public List<BaseEntity> readEntityList() throws JsonParseException {
        List<BaseEntity> result = new ArrayList<>();
        Map<String, BaseEntity> dataRds = this.readEntityMap();
        result.addAll(dataRds.values());
        return result;
    }

    public List<Map<String, Object>> readHashMapList() throws JsonParseException {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> dataRds = this.readHashMap();
        for (Object data : dataRds.values()){
            if (data !=null && data instanceof Map){
                result.add((Map<String, Object>)data);
            }
        }
        return result;
    }

    /**
     * 读取指定数据
     *
     * @param hKeys
     * @return
     */
    public synchronized Map<String, BaseEntity> readEntityMap(final Collection hKeys) throws JsonParseException {
        List<Object> jsonList = this.redisTemplate.opsForHash().multiGet(this.getHead() + "data", hKeys);
        return this.makeJson2EntityList(jsonList);
    }

    /**
     * 读指定的一个数据
     *
     * @return
     */
    public synchronized BaseEntity readEntity(String serviceKey) throws IOException {
        Class entityClass = BaseEntityClassFactory.getInstance(this.getEntityType());

        Map<String, Object> dataJsn = (Map<String, Object>) this.redisTemplate.opsForHash().get(this.getHead() + "data", serviceKey);
        return this.makeJson2Entity(entityClass, dataJsn);
    }

    public synchronized Map<String, Object> readHashMap(String serviceKey) {
        return  (Map<String, Object>) this.redisTemplate.opsForHash().get(this.getHead() + "data", serviceKey);
    }


    /**
     * 将JSON MAP转换回Entity对象
     *
     * @param entityClass 实体类型
     * @param jsonObject json对象
     * @return 实体类型
     * @throws JsonProcessingException 异常
     */
    private BaseEntity makeJson2Entity(Class entityClass, Map<String, Object> jsonObject) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsn = objectMapper.writeValueAsString(jsonObject);
        BaseEntity entity = (BaseEntity) objectMapper.readValue(jsn, entityClass);

        return entity;
    }

    /**
     * 将JSON MAP转换回Entity对象
     *
     * @param jsonList
     * @return map的实体
     */
    private Map<String, BaseEntity> makeJson2EntityList(List<Object> jsonList) throws JsonParseException {
        Class entityClass = BaseEntityClassFactory.getInstance(this.getEntityType());

        Map<String, BaseEntity> result = new ConcurrentHashMap<>();
        for (Object json : jsonList) {
            Map<String, Object> jsonObject = (Map<String, Object>) json;
            if (jsonObject == null) {
                continue;
            }

            BaseEntity entity = (BaseEntity) JsonUtils.buildObject(jsonObject, entityClass);
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
    /**
     * 将JSON MAP转换回Entity对象
     *
     * @param jsonMap
     * @return map的实体
     */
    private Map<String, BaseEntity> makeJson2EntityMap(Map<String, Object> jsonMap) throws JsonParseException {
        Class entityClass = BaseEntityClassFactory.getInstance(this.getEntityType());

        Map<String, BaseEntity> result = new ConcurrentHashMap<>();
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            Map<String, Object> jsonObject = (Map<String, Object>) entry.getValue();
            if (jsonObject == null) {
                continue;
            }

            BaseEntity entity = (BaseEntity) JsonUtils.buildObject(jsonObject, entityClass);
            result.put(entry.getKey(), entity);
        }

        return result;
    }
}
