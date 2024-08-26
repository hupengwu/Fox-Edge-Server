/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.mysql.initialize;


import cn.foxtech.persist.common.initialize.PersistCommonInitialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class PersistEngineInitialize {

    @Autowired
    private PersistCommonInitialize persistInitialize;

    public void initialize() {
        this.persistInitialize.initialize();
    }
}
