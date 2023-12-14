package cn.foxtech.kernel.gateway.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 动态路由刷新到SpringGateWay中的管理
 */
@Slf4j
@Service
public class RouteRefreshService implements ApplicationEventPublisherAware, ApplicationRunner {

    /**
     * redis中的路由配置
     */
    @Autowired
    private RouteDynamicService repository;


    /**
     * SpringGateWay的路由配置操作
     */
    @Autowired
    private RouteDefinitionWriter writer;


    /**
     * 路由发布者：通知SpringGateWay已经进行了配置更新，请将配置生效
     */
    private ApplicationEventPublisher publisher;


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }


    /**
     * 装载路由
     */
    private void loadRoutes() {
        log.info(">>>>>>>>>> init routes from redis <<<<<<<<<<");
        Flux<RouteDefinition> routeDefinitions = repository.getRouteDefinitions();
        routeDefinitions.subscribe(r -> {
            writer.save(Mono.just(r)).subscribe();
        });
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 查询路由
     *
     * @return
     */
    public List<RouteDefinition> query() {
        return repository.getRouteDefinitionList();
    }

    /**
     * 新增路由
     *
     * @param routeDefinition
     */
    public void add(RouteDefinition routeDefinition) {
        Assert.notNull(routeDefinition.getId(), "routeDefinition is can not be null");
        repository.save(Mono.just(routeDefinition)).subscribe();
        writer.save(Mono.just(routeDefinition)).subscribe();
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }


    /**
     * 更新路由
     *
     * @param routeDefinition
     */
    public void update(RouteDefinition routeDefinition) {
        Assert.notNull(routeDefinition.getId(), "routeDefinition is can not be null");
        repository.delete(Mono.just(routeDefinition.getId())).subscribe();
        writer.delete(Mono.just(routeDefinition.getId())).subscribe();
        repository.save(Mono.just(routeDefinition)).subscribe();
        writer.save(Mono.just(routeDefinition)).subscribe();
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }


    /**
     * 删除路由
     *
     * @param id
     */
    public void delete(String id) {
        Assert.notNull(id, "routeDefinition is can not be null");
        repository.delete(Mono.just(id)).subscribe();
        writer.delete(Mono.just(id)).subscribe();
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadRoutes();
    }
}
