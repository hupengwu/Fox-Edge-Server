package cn.foxtech.common.entity.service.extendconfig;


import cn.foxtech.common.entity.entity.ExtendConfigPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

//在对应的Mapper 接口上 基础基本的 BaseMapper<T> T是对应的pojo类
@Repository   //告诉容器你是持久层的 @Repository是spring提供的注释，能够将该类注册成Bean
public interface ExtendConfigMapper extends BaseMapper<ExtendConfigPo> {
    //所有的crud都编写完成了

}

