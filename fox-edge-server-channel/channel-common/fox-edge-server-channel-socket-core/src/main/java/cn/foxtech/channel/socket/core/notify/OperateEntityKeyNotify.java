package cn.foxtech.channel.socket.core.notify;

import cn.foxtech.channel.socket.core.handler.ScriptServiceKeyHandler;
import cn.foxtech.channel.socket.core.script.ScriptEngineService;
import cn.foxtech.channel.socket.core.script.ScriptServiceKey;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.entity.service.redis.BaseConsumerEntityNotify;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

/**
 * 变化动态通知
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateEntityKeyNotify extends BaseConsumerEntityNotify {
    private static final Logger logger = Logger.getLogger(OperateEntityKeyNotify.class);

    private RedisConsoleService console;

    /**
     * 脚本引擎
     */
    private ScriptEngineService scriptEngineService;

    /**
     * 操作实体
     */
    private OperateEntity operateEntity;
    /**
     * 包格式
     */
    private String format;

    /**
     * handler
     */
    private ScriptServiceKeyHandler serviceKeyHandler = new ScriptServiceKeyHandler();

    public String getServiceKey() {
        return this.operateEntity.makeServiceKey();
    }

    public void notifyInsert(BaseEntity entity) {
        this.reset();
    }

    public void notifyUpdate(BaseEntity entity) {
        this.operateEntity = (OperateEntity) entity;
        this.reset();
    }

    public void reset() {
        try {
            ScriptServiceKey scriptServiceKey = this.scriptEngineService.buildServiceKeyOperate(this.operateEntity);
            scriptServiceKey.setFormat(this.format);

            this.serviceKeyHandler.setScriptServiceKey(scriptServiceKey);
        } catch (Exception e) {
            String message = "reset异常：manufacturer=" + this.operateEntity.getManufacturer() + "， deviceType=" + this.operateEntity.getDeviceType() + "， operateName=" + this.operateEntity.getOperateName() + e.getMessage();
            this.logger.error(message);
            this.console.error(message);
        }
    }
}
