/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
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

    public String buildUri(String appName, String appType, Integer port) {
        // 构造uri
        String uri = "http://localhost:" + port + "/" + appType + "/" + appName;
        if (uri.endsWith("-service")) {
            uri = uri.substring(0, uri.length() - "-service".length());
        }

        return uri;
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

        Map<String, Object> predicateArgs = new HashMap<>();
        predicateArgs.put("_genkey_0", "/" + appType + "/" + shortAppName + "/**");

        Map<String, Object> predicate = new HashMap<>();
        predicate.put("name", "Path");
        predicate.put("args", predicateArgs);

        List<Map<String, Object>> predicates = new ArrayList<>();
        predicates.add(predicate);

        // 构造uri
        String uri = this.buildUri(appName, appType, port);

        // 构造过滤器
        Map<String, Object> filterArgs = new HashMap<>();
        filterArgs.put("_genkey_0", "2");

        Map<String, Object> filter = new HashMap<>();
        filter.put("name", "StripPrefix");
        filter.put("args", filterArgs);

        List<Map<String, Object>> filters = new ArrayList<>();
        filters.add(filter);

        Map<String, Object> body = new HashMap<>();
        body.put("id", this.buildId(appName, appType));
        body.put("predicates", predicates);
        body.put("uri", uri);
        body.put("filters", filters);
        body.put("order", 0);
        body.put("metadata", new HashMap<>());


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
