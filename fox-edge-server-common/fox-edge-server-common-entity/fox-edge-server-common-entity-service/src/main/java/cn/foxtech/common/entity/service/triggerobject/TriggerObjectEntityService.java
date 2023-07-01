package cn.foxtech.common.entity.service.triggerobject;


import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TriggerObjectEntityService extends BaseEntityService {
    @Autowired(required = false)
    private TriggerObjectEntityMapper mapper;

    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }
}
