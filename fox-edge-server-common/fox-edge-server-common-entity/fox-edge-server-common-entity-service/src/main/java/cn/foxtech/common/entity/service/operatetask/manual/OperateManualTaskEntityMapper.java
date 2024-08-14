/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.operatetask.manual;


import cn.foxtech.common.entity.entity.OperateManualTaskPo;
import cn.foxtech.common.entity.service.mybatis.BaseEntityMapper;
import org.springframework.stereotype.Repository;

//在对应的Mapper 接口上 基础基本的 BaseMapper<T> T是对应的pojo类
@Repository   //告诉容器你是持久层的 @Repository是spring提供的注释，能够将该类注册成Bean
public interface OperateManualTaskEntityMapper extends BaseEntityMapper<OperateManualTaskPo> {
    //所有的crud都编写完成了

}

