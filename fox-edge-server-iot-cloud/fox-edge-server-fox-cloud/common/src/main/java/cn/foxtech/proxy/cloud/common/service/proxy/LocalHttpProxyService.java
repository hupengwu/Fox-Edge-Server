package cn.foxtech.proxy.cloud.common.service.proxy;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.manager.ConfigManageService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.http.HttpClientUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地HTTP访问组件：访问本地gateway需要使用的http组件
 */
@Component
@Getter(value = AccessLevel.PUBLIC)
public class LocalHttpProxyService {
    private static final Logger logger = Logger.getLogger(LocalHttpProxyService.class);

    /**
     * header
     */
    private final Map<String, String> header = new ConcurrentHashMap<>();
    /**
     * 本地gateway的服务位置
     */
    private final String uri = "http://localhost:9000";
    /**
     * 用户名
     */
    private String username = "";
    /**
     * 密码
     */
    private String password = "";
    /**
     * 锁定时间：默认60秒
     * 登录失败后，等60秒再重新登录，避免云端认为是恶意攻击，而锁定账号
     */
    private Integer lockdown = 60;

    @Autowired
    private ConfigManageService configManageService;

    @Autowired
    private RedisConsoleService consoleService;

    /**
     * 最近登录时间：避免重复的登录
     */
    private Long loginLastTime = 0L;


    public boolean isLockdown() {
        return this.loginLastTime + this.lockdown * 1000 > System.currentTimeMillis();
    }

    public boolean isLogin() {
        return !this.header.isEmpty();
    }

    /**
     * 登录
     *
     * @throws IOException
     */
    public synchronized void login() throws IOException {
        // 获得账号密码
        this.configManageService.initialize("serverConfig", "serverConfig.json");
        Map<String, Object> configs = this.configManageService.getConfigParam("serverConfig");


        Map<String, Object> localConfig = (Map<String, Object>) configs.getOrDefault("local", new HashMap<>());

        // 取出信息
        this.username = (String) localConfig.getOrDefault("username", "username");
        this.password = (String) localConfig.getOrDefault("password", "");
        this.lockdown = (Integer) localConfig.getOrDefault("lockdown", 60);


        // 不用产生后台日志：这个自动操作，会产生太多的垃圾数据
        logger.info("登录本地gateway服务：开始登录！");

        // 发送请求
        HttpResponse response = HttpClientUtils.executeRestful(this.uri + "/auth/login?username=" + this.username + "&password=" + this.password, "get");
        Map<String, Object> respond = JsonUtils.buildObjectWithoutException(response.body(), Map.class);
        if (respond == null) {
            this.loginLastTime = System.currentTimeMillis();
            throw new RuntimeException("登录本地gateway服务：登录失敗！ " + this.uri);
        }

        // 检查：是否登录成功
        Object code = respond.get(AjaxResult.CODE_TAG);
        Object message = respond.get(AjaxResult.MSG_TAG);
        if (!HttpStatus.SUCCESS.equals(code)) {
            this.loginLastTime = System.currentTimeMillis();
            throw new RuntimeException("登录本地gateway服务：登录失敗！ " + this.uri + ",code=" + code + ",message=" + message);
        }

        // 提取token
        String token = (String) Maps.getValue(respond, AjaxResult.DATA_TAG, "accessToken");
        if (token == null || token.isEmpty()) {
            this.loginLastTime = System.currentTimeMillis();
            throw new RuntimeException("登录本地gateway服务：返回的token异常");
        }

        // 保存token，方便后面反复使用：SaToken必须填写在Cookie之中
        this.header.put("Connection", "keep-alive");
        this.header.put("Content-Type", "application/json");
        this.header.put("Cookie", "satoken=" + token);

        String logMessage = "登录本地gateway服务：登录成功！";
        this.consoleService.info(logMessage);
        logger.info(logMessage);
    }

    public Map<String, Object> executeRestful(String res, String method, String requestJson) throws IOException {
        // 检查：是否未登录
        if (this.header.isEmpty()) {
            this.login();
        }

        // 检查有没有获得登录成功后的云端令牌
        if (this.header.isEmpty()) {
            throw new RuntimeException("未登录到本地gateway服务!");
        }

        HttpResponse response = HttpClientUtils.executeRestful(this.uri + res, method, this.header, requestJson);

        // 转换成json
        ObjectMapper objectMapper = new ObjectMapper();        // 转换JSON结构
        Map<String, Object> respondVO = objectMapper.readValue(response.body(), Map.class);

        // 检查：是否登录成功
        Object code = respondVO.get(AjaxResult.CODE_TAG);
        Object message = respondVO.get(AjaxResult.MSG_TAG);

        // 检查：登录是否已经失效
        if (HttpStatus.NOT_LOGIN.equals(code)) {
            // 清空上次登录的信息
            this.header.clear();
            throw new RuntimeException("发送数据本地gateway服务：" + this.uri + "失败:code=" + code + "失败:message=" + message);
        }

        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new RuntimeException("发送数据本地gateway服务：" + this.uri + "失败:code=" + code + "失败:message=" + message);
        }

        return respondVO;
    }
}
