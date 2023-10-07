package cn.foxtech.manager.system.service;

import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 仓库管理服务：它简化仓库的管理
 */
@Component
public class RepoProductService {
    @Autowired
    private CloudRemoteService cloudRemoteService;


    public Map<String, Object> queryProductEntity(String uuid) throws IOException {
        Map<String, String> param = new HashMap<>();
        param.put("uuid", uuid);
        Map<String, Object> respond = this.cloudRemoteService.executeGet("/manager/repository/product/entity", param);

        Map<String, Object> data = (Map<String, Object>) respond.get("data");
        if (data == null) {
            throw new ServiceException("云端数据仓库返回的数据为空！");
        }

        return data;
    }

    public Map<String, Object> queryProductPage(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/product/page", body);
    }
}
