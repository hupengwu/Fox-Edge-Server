package cn.foxtech.manager.system.task;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.manager.system.service.GateWayRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class RouteUpdateTask extends PeriodTask {
    public static final String GATEWAY_ROUTES_PREFIX = "fox.edge:gateway.dynamic.route:";
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GateWayRouteService gateWayRouteService;

    @Autowired
    private ServiceStatus serviceStatus;

    @Override
    public int getTaskType() {
        return PeriodTaskType.task_type_share;
    }

    /**
     * 获得调度周期
     *
     * @return 调度周期，单位秒
     */
    public int getSchedulePeriod() {
        return 10;
    }

    /**
     * 待周期性执行的操作
     */
    @Override
    public void execute() {
        try {
            // 获得gateway的动态路由
            Map<String, Object> routes = this.getRoutes();
            // OSinfo.isLinux()
            // 通过PS命令获得进程列表
            List<Map<String, Object>> processList = this.getProcess(10 * 60 * 1000);

            // 注册路由
            this.registerRout(routes, processList);

            // 注销路由
            this.unregisterRout(routes, processList);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 获得带服务端口的进程信息
     * 从redis中读取关键信息，这个方法在linux/windows都可用
     *
     * @return
     */
    private List<Map<String, Object>> getProcess(Integer timeout) {
        // windows版本：不能执行linux的命令，但是redis中也有这边需要的关键信息
        List<Map<String, Object>> resultList = new ArrayList<>();


        List<Map<String, Object>> dataList = this.serviceStatus.getDataList();
        for (Map<String, Object> map : dataList) {
            Object serviceName = map.get(RedisStatusConstant.field_service_name);
            Object serviceType = map.get(RedisStatusConstant.field_service_type);
            Object activeTime = map.get(RedisStatusConstant.field_active_time);
            Object appPort = map.get(ServiceVOFieldConstant.field_app_port);

            // 检查参数信息
            if (MethodUtils.hasEmpty(serviceName, serviceType, appPort, activeTime)) {
                continue;
            }

            if (System.currentTimeMillis() - NumberUtils.makeLong(activeTime) > timeout) {
                continue;
            }

            Map<String, Object> result = new HashMap<>();
            result.put(ServiceVOFieldConstant.field_app_name, serviceName);
            result.put(ServiceVOFieldConstant.field_app_type, serviceType);
            result.put(RedisStatusConstant.field_active_time, activeTime);
            result.put(ServiceVOFieldConstant.field_app_port, appPort);

            resultList.add(result);
        }

        return resultList;
    }

    private void registerRout(Map<String, Object> routes, List<Map<String, Object>> processList) {
        for (Map<String, Object> map : processList) {
            try {
                String appName = (String) map.get(ServiceVOFieldConstant.field_app_name);
                String appType = (String) map.get(ServiceVOFieldConstant.field_app_type);
                Integer appPort = (Integer) map.get(ServiceVOFieldConstant.field_app_port);

                // 检查参数信息
                if (MethodUtils.hasEmpty(appName, appType, appPort)) {
                    continue;
                }

                // 检查：是否为gateway进程，它本身管路由，不需要配置路由
                if (ServiceVOFieldConstant.field_type_kernel.equals(appType) && ServiceVOFieldConstant.field_app_gateway.equals(appName)) {
                    continue;
                }

                // 检查：该进程是否在gateway这边进行了注册动态路由
                String id = this.gateWayRouteService.buildId(appName, appType);
                if (routes.containsKey(id)) {
                    // 场景1：路由内容一致，不需要处理
                    if (appPort.equals(routes.get(id))) {
                        continue;
                    } else {
                        // 场景2：路由内容不一致，就重新刷新
                        this.gateWayRouteService.updateRouter(appName, appType, appPort);
                        continue;
                    }
                } else {
                    // 路由不存在，向网关注册动态路由：这样，启动的服务才能够从网关进行转发
                    this.gateWayRouteService.registerRouter(appName, appType, appPort);
                    continue;
                }

            } catch (Exception e) {
                logger.error("注册路由失败：" + e.getMessage());
            }
        }
    }

    private void unregisterRout(Map<String, Object> routes, List<Map<String, Object>> processList) {
        Set<String> serviceIds = new HashSet<>();
        for (Map<String, Object> map : processList) {
            String appName = (String) map.get(ServiceVOFieldConstant.field_app_name);
            String appType = (String) map.get(ServiceVOFieldConstant.field_app_type);

            // 检查：是否为gateway进程，它本身管路由，不需要配置路由
            if (ServiceVOFieldConstant.field_type_kernel.equals(appType) && ServiceVOFieldConstant.field_app_gateway.equals(appName)) {
                continue;
            }

            // 检查参数信息
            if (MethodUtils.hasEmpty(appName, appType)) {
                continue;
            }

            String id = this.gateWayRouteService.buildId(appName, appType);
            serviceIds.add(id);
        }

        for (String id : routes.keySet()) {
            try {
                if (serviceIds.contains(id)) {
                    continue;
                }

                // 向网关注册动态路由：这样，启动的服务才能够从网关进行转发
                this.gateWayRouteService.unregisterRouter(id);
            } catch (Exception e) {
                logger.error("注销路由失败：" + e.getMessage());
            }
        }
    }


    /**
     * 从redis中读取gateway分享的动态路由信息
     *
     * @return 已经注册的动态路由
     */
    private Map<String, Object> getRoutes() {
        Map<String, Object> result = new HashMap<>();
        this.redisTemplate.keys(GATEWAY_ROUTES_PREFIX + "*").stream().forEach(key -> {
            String json = redisTemplate.opsForValue().get(key);
            try {
                Map<String, Object> route = JsonUtils.buildObject(json, Map.class);
                String id = (String) route.get("id");
                String uri = (String) route.get("uri");
                if (id == null || id.isEmpty()) {
                    return;
                }
                result.put(id, null);

                if (uri == null || !uri.startsWith("http://localhost:")) {
                    return;
                }
                String port = uri.substring("http://localhost:".length());

                result.put(id, Integer.valueOf(port));
            } catch (IOException e) {
                return;
            }
        });
        return result;
    }
}
