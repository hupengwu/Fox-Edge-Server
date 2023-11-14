package cn.foxtech.common.entity.service.redis;

import cn.foxtech.common.entity.entity.BaseEntity;

/**
 * 对象级别的通知
 */
public abstract class BaseConsumerEntityNotify {
    public abstract String getServiceKey();

    public void notifyInsert(BaseEntity entity){

    }

    public void notifyUpdate(BaseEntity entity){

    }
    public void notifyDelete(String serviceKey){

    }
}
