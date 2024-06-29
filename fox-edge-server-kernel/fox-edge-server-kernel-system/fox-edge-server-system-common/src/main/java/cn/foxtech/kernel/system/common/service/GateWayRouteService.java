package cn.foxtech.kernel.system.common.service;

import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.kernel.system.common.redistopic.RedisTopicPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 路由服务
 */
@Component
public class GateWayRouteService {
    @Autowired
    private RedisTopicPublisher publisher;


    public String buildId(String appName, String appType) {
        return appType + ":" + appName;
    }

    /**
     * 构造路由注册路由的数据结构：
     * 注意：xxx-service格式的名称，作为资源名，会被缩短为xxx
     */
    private Map<String, Object> buildRouter(String appName, String appType, Integer port) {
        String shortAppName = appName;
        if (shortAppName.toLowerCase().endsWith("-service")) {
            shortAppName = shortAppName.substring(0, shortAppName.length() - "-service".length());
        }

        Map<String, Object> args = new HashMap<>();
        args.put("_genkey_0", "/" + appType + "/" + shortAppName + "/**");

        Map<String, Object> predicate = new HashMap<>();
        predicate.put("name", "Path");
        predicate.put("args", args);

        List<Map<String, Object>> predicates = new ArrayList<>();
        predicates.add(predicate);

        Map<String, Object> body = new HashMap<>();
        body.put("id", this.buildId(appName, appType));
        body.put("predicates", predicates);
        body.put("uri", "http://localhost:" + port);
        body.put("filters", new ArrayList<>());
        body.put("order", 0);

        return body;
    }

    /**
     * 注册路由
     *
     * @param appName 应用名称
     * @param appType 应用类型
     * @param port    端口号
     */
    public void registerRouter(String appName, String appType, Integer port) {
        // 参数检查
        if (MethodUtils.hasEmpty(appName, appType, port)) {
            return;
        }

        Map<String, Object> body = this.buildRouter(appName, appType, port);

        RestFulRequestVO restFulRequestVO = new RestFulRequestVO();
        restFulRequestVO.setUri("/gateway/route/add");
        restFulRequestVO.setMethod("post");
        restFulRequestVO.setData(body);
        restFulRequestVO.setUuid(UUID.randomUUID().toString());

        this.publisher.sendMessage(RedisTopicConstant.topic_gateway_request, restFulRequestVO);
    }

    public void updateRouter(String appName, String appType, Integer port) {
        // 参数检查
        if (MethodUtils.hasEmpty(appName, appType, port)) {
            return;
        }

        Map<String, Object> body = this.buildRouter(appName, appType, port);

        RestFulRequestVO restFulRequestVO = new RestFulRequestVO();
        restFulRequestVO.setUri("/gateway/route/update");
        restFulRequestVO.setMethod("post");
        restFulRequestVO.setData(body);
        restFulRequestVO.setUuid(UUID.randomUUID().toString());

        this.publisher.sendMessage(RedisTopicConstant.topic_gateway_request, restFulRequestVO);
    }

    public void unregisterRouter(String appName, String appType) {
        String id = this.buildId(appName, appType);
        this.unregisterRouter(id);
    }

    public void unregisterRouter(String id) {
        // 参数检查
        if (MethodUtils.hasEmpty(id)) {
            return;
        }


        Map<String, Object> body = new HashMap<>();

        RestFulRequestVO restFulRequestVO = new RestFulRequestVO();
        restFulRequestVO.setUri("/gateway/route/delete/" + id);
        restFulRequestVO.setMethod("delete");
        restFulRequestVO.setData(body);
        restFulRequestVO.setUuid(UUID.randomUUID().toString());

        this.publisher.sendMessage(RedisTopicConstant.topic_gateway_request, restFulRequestVO);
    }
}
