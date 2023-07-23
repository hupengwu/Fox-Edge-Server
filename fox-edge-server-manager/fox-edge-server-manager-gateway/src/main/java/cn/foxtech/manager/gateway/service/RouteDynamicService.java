package cn.foxtech.manager.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


/**
 * 动态路由在redis中的存储管理
 */
@Slf4j
@Service
public class RouteDynamicService {

    public static final String GATEWAY_ROUTES_PREFIX = "fox.edge:gateway.dynamic.route:";


    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private ObjectMapper objectMapper;


    public Flux<RouteDefinition> getRouteDefinitions() {
        log.info(">>>>>>>>>> getRouteDefinitions <<<<<<<<<<");
        return Flux.fromIterable(this.getRouteDefinitionList());
    }

    public List<RouteDefinition> getRouteDefinitionList() {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        redisTemplate.keys(GATEWAY_ROUTES_PREFIX + "*").stream().forEach(key -> {
            String rdStr = redisTemplate.opsForValue().get(key);
            RouteDefinition routeDefinition = null;
            try {
                routeDefinition = objectMapper.readValue(rdStr, RouteDefinition.class);
                routeDefinitions.add(routeDefinition);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


        });
        return routeDefinitions;
    }

    public URI getRouteUri(String id) {
        AtomicReference<URI> uri = new AtomicReference<>();
        redisTemplate.keys(GATEWAY_ROUTES_PREFIX + "*").stream().forEach(key -> {
            String rdStr = redisTemplate.opsForValue().get(key);
            try {
                RouteDefinition routeDefinition = objectMapper.readValue(rdStr, RouteDefinition.class);
                if (id.equals(routeDefinition.getId())) {
                    uri.set(routeDefinition.getUri());
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


        });
        return uri.get();
    }

    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(routeDefinition -> {
            String rdStr = null;
            try {
                rdStr = objectMapper.writeValueAsString(routeDefinition);
                redisTemplate.opsForValue().set(GATEWAY_ROUTES_PREFIX + routeDefinition.getId(), rdStr);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


            return Mono.empty();
        });
    }


    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            if (redisTemplate.hasKey(GATEWAY_ROUTES_PREFIX + id)) {
                redisTemplate.delete(GATEWAY_ROUTES_PREFIX + id);
                return Mono.empty();
            }
            return Mono.defer(() -> Mono.error(new NotFoundException("routeDefinition not found, id is: " + id)));
        });
    }


    public Mono<Boolean> get(Mono<String> routeId) {
        return routeId.flatMap(id -> Mono.just(redisTemplate.hasKey(GATEWAY_ROUTES_PREFIX + id)));
    }
}
