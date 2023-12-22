package cn.foxtech.channel.serialport.notify;

import cn.foxtech.channel.serialport.entity.SerialChannelEntity;
import cn.foxtech.channel.serialport.service.ChannelService;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.entity.service.redis.BaseConsumerTypeNotify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Component
public class OperateEntityTypeNotify implements BaseConsumerTypeNotify {
    @Autowired
    private ChannelService channelService;

    @Autowired
    private RedisConsoleService logger;

    /**
     * 通知变更
     *
     * @param addMap 增加
     * @param delSet 删除
     * @param mdyMap 修改
     */
    @Override
    public void notify(String entityType, long updateTime, Map<String, BaseEntity> addMap, Set<String> delSet, Map<String, BaseEntity> mdyMap) {
        for (String key : addMap.keySet()) {
            this.rebindScriptEngine(addMap.get(key));
        }
        for (String key : mdyMap.keySet()) {
            this.rebindScriptEngine(mdyMap.get(key));
        }
        for (String key : delSet) {

        }
    }

    private void rebindScriptEngine(BaseEntity entity) {
        OperateEntity operateEntity = (OperateEntity) entity;

        for (String channelName : this.channelService.getChannelEntityMap().keySet()) {
            try {
                SerialChannelEntity channelEntity = this.channelService.getChannelEntityMap().get(channelName);

                if (channelEntity.getSplitOperate() != null && channelEntity.getSplitOperate().makeServiceKey().equals(operateEntity.makeServiceKey())) {
                    this.channelService.rebindScriptEngine(channelEntity, channelEntity.getChannelParam());
                }

                if (channelEntity.getKeyOperate() != null && channelEntity.getKeyOperate().makeServiceKey().equals(operateEntity.makeServiceKey())) {
                    this.channelService.rebindScriptEngine(channelEntity, channelEntity.getChannelParam());
                }

            } catch (Exception e) {
                this.logger.error("脚本引擎绑定出错：(" + channelName + "):" + e.getMessage());
            }
        }
    }
}
