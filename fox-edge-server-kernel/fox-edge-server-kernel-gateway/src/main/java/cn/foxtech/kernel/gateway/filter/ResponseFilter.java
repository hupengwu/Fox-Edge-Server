package cn.foxtech.kernel.gateway.filter;

import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.kernel.gateway.service.LocalSystemConfService;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ResponseFilter implements GlobalFilter, Ordered {
    private final Map<String, ResponseHandler> handlerMap = new HashMap<>();
    @Autowired
    private LocalSystemConfService systemConfService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获得URL和METHOD信息
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        HttpMethod method = serverHttpRequest.getMethod();
        String path = serverHttpRequest.getURI().getRawPath();

        // 获得处理器
        ResponseHandler responseHandler = this.getHandlerMap().get(path + ":" + method);
        if (responseHandler == null) {
            return chain.filter(exchange);
        }


        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpResponseDecorator decoratedResponse = new ResponseDecorator(originalResponse, responseHandler);

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return -10;
    }

    public Map<String, ResponseHandler> getHandlerMap() {
        if (!this.handlerMap.isEmpty()) {
            return this.handlerMap;
        }

        this.handlerMap.put("/kernel/manager/repository/page" + ":" + HttpMethod.POST, (String value) -> {
            try {
                Map<String, Object> respond = JsonUtils.buildObject(value, Map.class);

                Map<String, Object> data = (Map<String, Object>) respond.get("data");
                List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");

                this.systemConfService.sensitiveWordsString(list, "replace", "description", "description");
                this.systemConfService.sensitiveWordsString(list, "duplicate", "manufacturer", "manufacturerShow");
                this.systemConfService.sensitiveWordsString(list, "duplicate", "modelName", "modelNameShow");


                return JsonUtils.buildJson(respond);
            } catch (Exception e) {
                return value;
            }

        });

        this.handlerMap.put("/kernel/manager/repository/script/version/page" + ":" + HttpMethod.POST, (String value) -> {
            try {
                Map<String, Object> respond = JsonUtils.buildObject(value, Map.class);

                Map<String, Object> data = (Map<String, Object>) respond.get("data");
                List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");

                this.systemConfService.sensitiveWordsString(list, "replace", "author", "author");

                return JsonUtils.buildJson(respond);
            } catch (Exception e) {
                return value;
            }

        });

        this.handlerMap.put("/kernel/manager/repository/local/comp-list/page" + ":" + HttpMethod.POST, (String value) -> {
            try {
                Map<String, Object> respond = JsonUtils.buildObject(value, Map.class);

                Map<String, Object> data = (Map<String, Object>) respond.get("data");
                List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");

                this.systemConfService.sensitiveWordsMap(list, "duplicate", "compParam", "compParamShow");
                this.systemConfService.sensitiveWordsMapString(list, "replace", "compParamShow", "fileName", "fileName");
                this.systemConfService.sensitiveWordsMapString(list, "replace", "compParamShow", "modelName", "modelName");
                this.systemConfService.sensitiveWordsMapString(list, "replace", "compParamShow", "manufacturer", "manufacturer");

                return JsonUtils.buildJson(respond);
            } catch (Exception e) {
                return value;
            }
        });

        this.handlerMap.put("/kernel/manager/repository/local/file-list/page" + ":" + HttpMethod.POST, (String value) -> {
            try {
                Map<String, Object> respond = JsonUtils.buildObject(value, Map.class);

                Map<String, Object> data = (Map<String, Object>) respond.get("data");
                List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");

                // 敏感詞
                this.systemConfService.sensitiveWordsString(list, "duplicate", "manufacturer", "manufacturerShow");
                this.systemConfService.sensitiveWordsString(list, "duplicate", "modelName", "modelNameShow");

                return JsonUtils.buildJson(respond);
            } catch (Exception e) {
                return value;
            }
        });

        this.handlerMap.put("/kernel/manager/repository/local/operate-list/page" + ":" + HttpMethod.POST, (String value) -> {
            try {
                Map<String, Object> respond = JsonUtils.buildObject(value, Map.class);

                Map<String, Object> data = (Map<String, Object>) respond.get("data");
                List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");

                this.systemConfService.sensitiveWordsString(list, "duplicate", "manufacturer", "manufacturerShow");

                return JsonUtils.buildJson(respond);
            } catch (Exception e) {
                return value;
            }
        });

        this.handlerMap.put("/kernel/manager/repository/local/jar-file/jar-info/query" + ":" + HttpMethod.POST, (String value) -> {
            try {
                Map<String, Object> respond = JsonUtils.buildObject(value, Map.class);

                Map<String, Object> data = (Map<String, Object>) respond.get("data");

                // 敏感詞
                this.systemConfService.sensitiveWordsString(data, "duplicate", "fileName", "fileNameShow");
                this.systemConfService.sensitiveWordsString(data, "duplicate", "groupId", "groupIdShow");
                this.systemConfService.sensitiveWordsString(data, "duplicate", "artifactId", "artifactIdShow");
                this.systemConfService.sensitiveWordsStringList(data, "duplicate", "classFileName", "classFileNameShow");
                this.systemConfService.sensitiveWordsString((List<Map<String, Object>>) data.get("dependencies"), "duplicate", "artifactId", "artifactIdShow");

                return JsonUtils.buildJson(respond);
            } catch (Exception e) {
                return value;
            }
        });

        this.handlerMap.put("/kernel/manager/repository/product/entity" + ":" + HttpMethod.GET, (String value) -> {
            try {
                Map<String, Object> respond = JsonUtils.buildObject(value, Map.class);

                Map<String, Object> data = (Map<String, Object>) respond.get("data");

                Object comps = data.get("comps");
                if (comps != null && comps instanceof List) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) comps;

                    // 敏感词的处理
                    this.systemConfService.sensitiveWordsString(list, "duplicate", "modelName", "modelNameShow");
                }
                return JsonUtils.buildJson(respond);
            } catch (Exception e) {
                return value;
            }
        });

        return this.handlerMap;
    }

    public interface ResponseHandler {
        String wrapBody(String value);
    }

    public class ResponseDecorator extends ServerHttpResponseDecorator {
        private final ResponseHandler responseHandler;

        public ResponseDecorator(ServerHttpResponse delegate, ResponseHandler responseHandler) {
            super(delegate);
            this.responseHandler = responseHandler;
        }

        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
            body = fluxBody.buffer().map(dataBuffers -> {
                // 读取缓存
                DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                DataBuffer join = dataBufferFactory.join(dataBuffers);
                byte[] content = new byte[join.readableByteCount()];
                join.read(content);

                //释放掉内存
                DataBufferUtils.release(join);

                // 修改前的数据
                String originalBody = new String(content, StandardCharsets.UTF_8);

                // 修改后的数据
                String finalBody = this.responseHandler.wrapBody(originalBody);

                // 重新打包
                return this.getDelegate().bufferFactory().wrap(finalBody.getBytes());
            });
            return super.writeWith(body);
        }
    }
}
