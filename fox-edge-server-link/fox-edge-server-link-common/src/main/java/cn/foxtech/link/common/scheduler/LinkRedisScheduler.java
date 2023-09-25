package cn.foxtech.link.common.scheduler;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.LinkEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.link.common.api.LinkClientAPI;
import cn.foxtech.link.common.properties.LinkProperties;
import cn.foxtech.link.common.service.EntityManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 进程状态调度器：把进程状态周期性的刷新到redis
 */
@Component
public class LinkRedisScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(LinkRedisScheduler.class);

    /**
     * 实体管理
     */
    @Autowired
    private EntityManageService entityManageService;

    /**
     * 链路服务
     */
    @Autowired
    private LinkClientAPI linkService;

    /**
     * 配置信息
     */
    @Autowired
    private LinkProperties linkProperties;

    /**
     * 链路配置
     */
    private Map<String, LinkEntity> linkEntityMap;

    @Autowired
    private RedisConsoleService console;

    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        // 同步实体数据
        this.entityManageService.syncEntity();

        // 重置链路的配置信息
        this.syncLinkConfig();
    }

    /**
     * 同步链路配置
     */
    private void syncLinkConfig() {
        // 检查：是否有重新状态的配置到达
        String linkType = this.linkProperties.getLinkType();
        Long updateTime = this.entityManageService.removeReloadedFlag(LinkEntity.class.getSimpleName());
        if (updateTime == null && this.linkEntityMap != null) {
            return;
        }

        // 取出重新状态的配置
        List<BaseEntity> entityList = this.entityManageService.getLinkEntity(linkType);
        Map<String, LinkEntity> map = new HashMap<>();
        for (BaseEntity entity : entityList) {
            LinkEntity linkEntity = (LinkEntity) entity;
            map.put(linkEntity.getLinkName(), linkEntity);
        }

        // 检查：是否为初始化状态
        if (this.linkEntityMap == null) {
            this.linkEntityMap = new HashMap<>();
        }

        // 比较差异
        Set<String> addList = new HashSet<>();
        Set<String> delList = new HashSet<>();
        Set<String> eqlList = new HashSet<>();
        DifferUtils.differByValue(this.linkEntityMap.keySet(), map.keySet(), addList, delList, eqlList);

        // 打开链路
        for (String key : addList) {
            try {
                LinkEntity linkEntity = map.get(key);
                this.linkService.openLink(linkEntity.getLinkName(), linkEntity.getLinkParam());
                this.linkEntityMap.put(key, linkEntity);

                String message = "链路打开成功:" + key;
                this.console.info(message);
                logger.info(message);
            } catch (Exception e) {
                String message = "链路打开失败:" + key + ":" + e.getMessage();
                this.console.error(message);
                logger.error(message);
            }
        }

        // 关闭链路
        for (String key : delList) {
            try {
                LinkEntity linkEntity = this.linkEntityMap.get(key);

                this.linkService.closeLink(linkEntity.getLinkName(), linkEntity.getLinkParam());
                this.linkEntityMap.remove(key);

                String message = "链路关闭成功:" + key;
                this.console.info(message);
                logger.info(message);
            } catch (Exception e) {
                String message = "链路关闭失败:" + key + ":" + e.getMessage();
                this.console.error(message);
                logger.error(message);
            }
        }

        // 重新打开链路
        for (String key : eqlList) {
            try {
                LinkEntity oldEntity = this.linkEntityMap.get(key);
                LinkEntity newEntity = map.get(key);

                // 检测：配置内容是否发生了变化
                String newValue = newEntity.makeServiceValue();
                String oldValue = oldEntity.makeServiceValue();
                if (newValue.equals(oldValue)) {
                    continue;
                }

                // 关闭链路
                this.linkService.closeLink(oldEntity.getLinkName(), oldEntity.getLinkParam());
                this.linkEntityMap.remove(key);

                // 重新打开链路
                this.linkService.openLink(newEntity.getLinkName(), newEntity.getLinkParam());
                this.linkEntityMap.put(key, newEntity);

                String message = "链路重置成功:" + key;
                this.console.info(message);
                logger.info(message);
            } catch (Exception e) {
                String message = "链路重置失败:" + key + ":" + e.getMessage();
                this.console.error(message);
                logger.error(message);
            }
        }
    }
}
