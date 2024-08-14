/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.service.common.service;

import cn.foxtech.common.entity.manager.EntityServiceManager;

/**
 * 实体管理操作：提供设备信息查询和构造服务
 */
public abstract class ServiceEntityManageService extends EntityServiceManager {
    public abstract void instance();
}
