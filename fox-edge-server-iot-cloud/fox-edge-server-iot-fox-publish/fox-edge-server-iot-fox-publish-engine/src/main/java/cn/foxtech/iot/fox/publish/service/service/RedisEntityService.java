package cn.foxtech.iot.fox.publish.service.service;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.redis.AgileMapRedisService;
import cn.foxtech.iot.fox.publish.service.vo.EntityChangedNotifyVO;
import cn.foxtech.utils.common.utils.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RedisEntityService {
    @Autowired
    private RedisService redisService;


    public List<EntityChangedNotifyVO> queryNotify(String entityType) {
        List<EntityChangedNotifyVO> voList = new ArrayList<>();

        try {
            AgileMapRedisService redisService = AgileMapRedisService.getInstanceBySimpleName(entityType, this.redisService);

            // 装载数据：从redis读取数据，并获知变化状态
            Map<String, BaseEntity> addMap = new HashMap<>();
            Set<String> delSet = new HashSet<>();
            Map<String, BaseEntity> mdyMap = new HashMap<>();
            redisService.loadChangeEntities(addMap, delSet, mdyMap);

            // 检测：数据
            if (addMap.isEmpty() && delSet.isEmpty() && mdyMap.isEmpty()) {
                return voList;
            }

            for (String key : addMap.keySet()) {
                EntityChangedNotifyVO vo = new EntityChangedNotifyVO();
                vo.setMethod("insert");
                vo.setEntity(addMap.get(key));

                voList.add(vo);
            }
            for (String key : mdyMap.keySet()) {
                EntityChangedNotifyVO vo = new EntityChangedNotifyVO();
                vo.setMethod("update");
                vo.setEntity(mdyMap.get(key));

                voList.add(vo);
            }
            for (String key : delSet) {
                EntityChangedNotifyVO vo = new EntityChangedNotifyVO();
                vo.setMethod("delete");

                voList.add(vo);
            }
        } catch (Exception e) {
            e.getMessage();
        }

        return voList;
    }
}
