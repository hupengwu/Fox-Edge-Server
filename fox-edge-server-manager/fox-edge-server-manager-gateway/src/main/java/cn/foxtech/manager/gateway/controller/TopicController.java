package cn.foxtech.manager.gateway.controller;

import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import cn.foxtech.manager.gateway.service.RouteRefreshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TopicController extends RedisTopicSubscriber {
    @Autowired
    private RouteRefreshService refreshService;

    @Override
    public String topic1st() {
        return "topic_gateway_request";
    }


    @Override
    public void receiveTopic1st(String message) {
        try {
            RestFulRequestVO restFulRequestVO = JsonUtils.buildObject(message, RestFulRequestVO.class);
            if (restFulRequestVO == null) {
                return;
            }

            String root = "/gateway/route";
            // 添加路由
            if ((root + "/add").equals(restFulRequestVO.getUri()) && "post".equals(restFulRequestVO.getMethod())) {
                Map<String, Object> body = (Map<String, Object>) restFulRequestVO.getData();
                RouteDefinition routeDefinition = JsonUtils.buildObject(body, RouteDefinition.class);
                if (routeDefinition == null) {
                    return;
                }

                this.refreshService.add(routeDefinition);
            }
            
            // 更新路由
            if ((root + "/update").equals(restFulRequestVO.getUri()) && "post".equals(restFulRequestVO.getMethod())) {
                Map<String, Object> body = (Map<String, Object>) restFulRequestVO.getData();
                RouteDefinition routeDefinition = JsonUtils.buildObject(body, RouteDefinition.class);
                if (routeDefinition == null) {
                    return;
                }

                this.refreshService.update(routeDefinition);
            }

            // 删除路由
            if (restFulRequestVO.getUri().startsWith(root + "/delete/") && "delete".equals(restFulRequestVO.getMethod())) {
                String id = restFulRequestVO.getUri().substring((root + "/delete/").length());
                this.refreshService.delete(id);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
