package cn.foxtech.common.entity.service.deviceobject;


import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceObjectEntityService extends BaseEntityService {
    @Autowired(required = false)
    private DeviceObjectEntityMapper mapper;

    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }
}
