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


    public synchronized Map<String, Object> readHashMap() {
        Map<String, Object> map = this.redisTemplate.opsForHash().entries(this.getHead() + "data");
        return map;
    }

    public List<BaseEntity> readEntityList() throws InstantiationException, IllegalAccessException {
        Class clazz = BaseEntityClassFactory.getInstance(this.getEntityType());

        List<BaseEntity> entityList = new ArrayList<>();

        BaseEntity builder = (BaseEntity) clazz.newInstance();

        List<Map<String, Object>> mapList = this.readHashMapList();
        for (Map<String, Object> map : mapList) {
            BaseEntity entity = builder.build(map);
            if (entity == null) {
                continue;
            }

            entityList.add(entity);
        }

        // 根据ID号排列
        Collections.sort(entityList, (o1, o2) -> {
            Long id2 = o2.getId() == null ? 0 : o2.getId();
            Long id1 = o1.getId() == null ? 0 : o1.getId();
            return id1.compareTo(id2);
        });

        return entityList;
    }

    public List<Map<String, Object>> readHashMapList() {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> dataRds = this.readHashMap();
        for (Object data : dataRds.values()) {
            if (data != null && data instanceof Map) {
                result.add((Map<String, Object>) data);
            }
        }
        return result;
    }

    public synchronized Map<String, BaseEntity> readEntityMap(final Collection hKeys) throws InstantiationException, IllegalAccessException {
        List<Object> mapList = this.redisTemplate.opsForHash().multiGet(this.getHead() + "data", hKeys);
        return this.makeHashMap2EntityList(mapList);
    }

    /**
     * 读指定的一个数据
     *
     * @return
     */
    public synchronized BaseEntity readEntity(String serviceKey) throws IOException {
        Class clazz = BaseEntityClassFactory.getInstance(this.getEntityType());

        Map<String, Object> dataJsn = (Map<String, Object>) this.redisTemplate.opsForHash().get(this.getHead() + "data", serviceKey);
        return this.makeJson2Entity(clazz, dataJsn);
    }

    public synchronized Map<String, Object> readHashMap(String serviceKey) {
        return (Map<String, Object>) this.redisTemplate.opsForHash().get(this.getHead() + "data", serviceKey);
    }


    /**
     * 将JSON MAP转换回Entity对象
     *
     * @param clazz      实体类型
     * @param jsonObject json对象
     * @return 实体类型
     * @throws JsonProcessingException 异常
     */
    private BaseEntity makeJson2Entity(Class clazz, Map<String, Object> jsonObject) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsn = objectMapper.writeValueAsString(jsonObject);
        BaseEntity entity = (BaseEntity) objectMapper.readValue(jsn, clazz);

        return entity;
    }

    /**
     * 将JSON MAP转换回Entity对象
     *
     * @param jsonList
     * @return map的实体
     */
    private Map<String, BaseEntity> makeJson2EntityList(List<Object> jsonList) throws JsonParseException {
        Class clazz = BaseEntityClassFactory.getInstance(this.getEntityType());

        Map<String, BaseEntity> result = new ConcurrentHashMap<>();
        for (Object json : jsonList) {
            Map<String, Object> jsonObject = (Map<String, Object>) json;
            if (jsonObject == null) {
                continue;
            }

            BaseEntity entity = (BaseEntity) JsonUtils.buildObject(jsonObject, clazz);
            result.put(entity.makeServiceKey(), entity);
        }

        return result;
    }

    private Map<String, BaseEntity> makeHashMap2EntityList(List<Object> jsonList) throws InstantiationException, IllegalAccessException {
        Class clazz = BaseEntityClassFactory.getInstance(this.getEntityType());

        BaseEntity builder = (BaseEntity) clazz.newInstance();

        Map<String, BaseEntity> result = new ConcurrentHashMap<>();
        for (Object obj : jsonList) {
            Map<String, Object> map = (Map<String, Object>) obj;
            if (map == null) {
                continue;
            }

            BaseEntity entity = builder.build(map);
            if (entity == null) {
                continue;
            }

            result.put(entity.makeServiceKey(), entity);
        }

        return result;
    }

    public <T> List<BaseEntity> readEntityList(IBaseFinder finder) throws InstantiationException, IllegalAccessException {
        List<BaseEntity> entityList = new ArrayList<>();

        Class clazz = BaseEntityClassFactory.getInstance(this.getEntityType());

        BaseEntity builder = (BaseEntity) clazz.newInstance();

        List<Map<String, Object>> mapList = this.readHashMapList();
        for (Map<String, Object> map : mapList) {
            BaseEntity entity = builder.build(map);

            if (finder.compareValue(entity)) {
                entityList.add(entity);
            }
        }

        // 根据ID号排列
        Collections.sort(entityList, (o1, o2) -> {
            Long id2 = o2.getId() == null ? 0L : o2.getId();
            Long id1 = o1.getId() == null ? 0L : o1.getId();
            return id1.compareTo(id2);
        });

        return entityList;
    }

    public <T> T readEntity(IBaseFinder finder) throws InstantiationException, IllegalAccessException {
        Class clazz = BaseEntityClassFactory.getInstance(this.getEntityType());

        BaseEntity builder = (BaseEntity) clazz.newInstance();

        List<Map<String, Object>> mapList = this.readHashMapList();
        for (Map<String, Object> map : mapList) {
            BaseEntity entity = builder.build(map);

            if (finder.compareValue(entity)) {
                return (T) entity;
            }
        }

        return null;
    }

    public <T> T readEntity(Long id) throws InstantiationException, IllegalAccessException {
        return this.readEntity((Object value) -> {
            BaseEntity entity = (BaseEntity) value;
            return id.equals(entity.getId());
        });
    }

    public <T> T readEntity(String serviceKey, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        Map<String, Object> map = this.readHashMap(serviceKey);
        if (map == null) {
            return null;
        }

        BaseEntity builder = (BaseEntity) clazz.newInstance();
        return (T) builder.build(map);
    }
}
