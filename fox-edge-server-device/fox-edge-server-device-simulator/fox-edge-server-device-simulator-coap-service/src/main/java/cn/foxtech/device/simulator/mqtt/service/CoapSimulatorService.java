package cn.foxtech.device.simulator.mqtt.service;

import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.device.simulator.mqtt.entity.CoapConfigEntity;
import cn.foxtech.device.simulator.mqtt.entity.CoapConfigRes;
import cn.foxtech.device.simulator.mqtt.entity.CoapDataEntity;
import lombok.AccessLevel;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CoapSimulatorService {
    private static final Logger logger = Logger.getLogger(CoapSimulatorService.class);

    /**
     * 服务器对象
     */
    private final CoapServer server = new CoapServer();

    /**
     * 参数表
     */
    private final CoapDataEntity dataEntity = new CoapDataEntity();

    /**
     * 配置
     */
    @Setter(value = AccessLevel.PUBLIC)
    private CoapConfigEntity config;

    private static void exchange(CoapConfigRes res, String method, CoapDataEntity dataEntity, CoapExchange exchange) {
        Map<String, Object> params = dataEntity.getParams(res.getResource());
        if (params == null) {
            exchange.respond(CoAP.ResponseCode.CONTENT, "");
            return;
        }

        // 根据路径参数，查询数据
        if (method.equals("get")) {
            try {
                List<Map<String, Object>> paramList = new ArrayList<>();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String name = entry.getKey();
                    Object value = exchange.getQueryParameter(name);
                    if (value != null) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("name", entry.getKey());
                        param.put("value", entry.getValue());
                        paramList.add(param);
                    }
                }

                exchange.respond(CoAP.ResponseCode.CONTENT, JsonUtils.buildJson(paramList));
            } catch (Exception e) {
                logger.warn(e.toString());
            }

            return;
        }

        // 根据body参数，插入数据
        if (method.equals("post") || method.equals("put")) {
            try {
                String body = new String(exchange.getRequestPayload());
                List<Map<String, Object>> paramList = JsonUtils.buildObject(body, List.class);
                for (Map<String, Object> item : paramList) {
                    params.put((String) item.get("name"), item.get("value"));
                }

                String json = JsonUtils.buildJson(paramList);
                exchange.respond(CoAP.ResponseCode.CONTENT, json);
            } catch (Exception e) {
                logger.error(e.toString());
            }

            return;
        }

        // 根据路径参数，查询数据
        if (method.equals("delete")) {
            try {
                List<Map<String, Object>> paramList = new ArrayList<>();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    String name = entry.getKey();
                    Object value = exchange.getQueryParameter(name);
                    if (value != null) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("name", entry.getKey());
                        param.put("value", entry.getValue());
                        paramList.add(param);
                    }
                }

                for (Map<String, Object> param : paramList) {
                    String name = (String) param.get("name");
                    params.remove(name);
                }

                exchange.respond(CoAP.ResponseCode.CONTENT, JsonUtils.buildJson(paramList));
            } catch (Exception e) {
                logger.warn(e.toString());
            }

            return;
        }

    }

    /**
     * 重新打开串口
     */
    public void reload() {
        try {
            // 读取JSON格式的配置文件
            File file = new File("");
            String confFileName = file.getAbsolutePath() + "/conf/fox-edge-server-device-simulator-coap.conf";

            String jsonData = new String(Files.readAllBytes(Paths.get(confFileName)));

            // 构造数据
            dataEntity.build(jsonData);
        } catch (IOException e) {
            logger.info(e);
        }
    }

    /**
     * 注册资源
     */
    public void register() {
        List<CoapConfigRes> resources = config.getResources();
        for (CoapConfigRes res : resources) {
            server.add(new CoapResource(res.getResource()) {
                @Override
                public void handleGET(CoapExchange exchange) {
                    CoapSimulatorService.exchange(res, "get", dataEntity, exchange);
                }

                @Override
                public void handlePOST(CoapExchange exchange) {
                    CoapSimulatorService.exchange(res, "post", dataEntity, exchange);
                }

                @Override
                public void handlePUT(CoapExchange exchange) {
                    CoapSimulatorService.exchange(res, "put", dataEntity, exchange);
                }

                @Override
                public void handleDELETE(CoapExchange exchange) {
                    CoapSimulatorService.exchange(res, "delete", dataEntity, exchange);
                }
            });
        }
    }

    /**
     * 启动服务
     */
    public void start() {
        server.getConfig().set(CoapConfig.COAP_PORT, this.config.getCoapPort());
        server.getConfig().set(CoapConfig.MAX_ACTIVE_PEERS, this.config.getMaxActivePeers());
        server.getConfig().set(CoapConfig.PROTOCOL_STAGE_THREAD_COUNT, 20);

        server.start();
    }

}
