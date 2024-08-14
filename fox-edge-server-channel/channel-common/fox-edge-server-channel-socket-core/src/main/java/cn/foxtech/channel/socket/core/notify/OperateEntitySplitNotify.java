/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.socket.core.notify;

import cn.foxtech.channel.socket.core.handler.ScriptSplitMessageHandler;
import cn.foxtech.channel.socket.core.script.ScriptEngineService;
import cn.foxtech.channel.socket.core.script.ScriptSplitMessage;
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
public class OperateEntitySplitNotify extends BaseConsumerEntityNotify {
    private static final Logger logger = Logger.getLogger(OperateEntitySplitNotify.class);

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
    private ScriptSplitMessageHandler splitMessageHandler = new ScriptSplitMessageHandler();

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
            ScriptSplitMessage scriptSplitMessage = this.scriptEngineService.buildSplitOperate(this.operateEntity);
            scriptSplitMessage.setFormat(this.format);

            this.splitMessageHandler.setScriptSplitMessage(scriptSplitMessage);
            this.splitMessageHandler.resetHeader();
        } catch (Exception e) {
            String message = "reset异常：manufacturer=" + this.operateEntity.getManufacturer() + "， deviceType=" + this.operateEntity.getDeviceType() + "， operateName=" + this.operateEntity.getOperateName() + e.getMessage();
            this.logger.error(message);
            this.console.error(message);
        }
    }
}
