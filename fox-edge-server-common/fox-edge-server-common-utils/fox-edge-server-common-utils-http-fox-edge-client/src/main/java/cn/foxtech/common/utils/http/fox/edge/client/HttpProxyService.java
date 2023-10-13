package cn.foxtech.proxy.cloud.forwarder.service.proxy;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.http.HttpClientUtil;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.proxy.cloud.common.service.ConfigManageService;
import cn.foxtech.proxy.cloud.common.service.EntityManageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter(value = AccessLevel.PUBLIC)
public class HttpProxyService {
    private static final Logger logger = Logger.getLogger(HttpProxyService.class);

    /**
     * header
     */
    private final Map<String, String> header = new ConcurrentHashMap<>();
    /**
     * 用户名
     */
    private String username = "";
    /**
     * 密码
     */
    private String password = "";
    /**
     * 本地gateway服务
     */
    private final String uri = "http://localhost:9000";

    /**
     * 锁定时间：默认60秒
     * 登录失败后，等60秒再重新登录，避免云端认为是恶意攻击，而锁定账号
     */
    private Integer lockdown = 60;


    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    @Autowired
    private ConfigManageService configManageService;

    @Autowired
    private EntityManageService manageService;

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
        Map<String, Object> configs = this.configManageService.loadInitConfig("localConfig", "localConfig.json");

        // 取出信息
        this.username = (String) configs.getOrDefault("username", "username");
        this.password = (String) configs.getOrDefault("password", "");
        this.lockdown = (Integer) configs.getOrDefault("lockdown", 60);

        Map<String, String> request = new HashMap<>();
        request.put("username", this.username);
        request.put("password", this.password);

        // 不用产生后台日志：这个自动操作，会产生太多的垃圾数据
        logger.info("登录本地gateway：开始登录！");

        // 发送请求
        String respondJson = HttpClientUtil.executeGet(this.uri + "/auth/login", request);
        Map<String, Object> respond = JsonUtils.buildObject(respondJson, Map.class);
        if (respond == null) {
            this.loginLastTime = System.currentTimeMillis();
            throw new RuntimeException("登录云端服务器：登录失敗！ " + this.uri);
        }

        // 检查：是否登录成功
        Object code = respond.get(AjaxResult.CODE_TAG);
        Object message = respond.get(AjaxResult.MSG_TAG);
        if (!HttpStatus.SUCCESS.equals(code)) {
            this.loginLastTime = System.currentTimeMillis();
            throw new RuntimeException("登录云端服务器：登录失敗！ " + this.uri + ",code=" + code + ",message=" + message);
        }

        // 提取token
        String token = (String) Maps.getValue(respond, AjaxResult.DATA_TAG, "accessToken");
        if (token == null || token.isEmpty()) {
            this.loginLastTime = System.currentTimeMillis();
            throw new RuntimeException("云端返回的token异常");
        }

        // 保存token，方便后面反复使用
        this.header.put("Authorization", "Bearer " + token);

        String logMessage = "登录云端服务器：登录成功！";
        this.consoleService.info(logMessage);
        logger.info(logMessage);
    }

    public <REQ> Map<String, Object> executePost(String res, REQ requestVO) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(requestVO);
        return this.executePost(res, json);
    }

    public Map<String, Object> executePost(String res, String requestJson) throws IOException {
        // 检查：是否未登录
        if (this.header.isEmpty()) {
            this.login();
        }

        // 检查有没有获得登录成功后的云端令牌
        if (this.header.isEmpty()) {
            throw new RuntimeException("未登录到云端!");
        }

        String respondJson = HttpClientUtil.executePost(this.uri + res, requestJson, this.header);

        // 转换成json
        ObjectMapper objectMapper = new ObjectMapper();        // 转换JSON结构
        Map<String, Object> respondVO = objectMapper.readValue(respondJson, Map.class);

        // 检查：是否登录成功
        Object code = respondVO.get(AjaxResult.CODE_TAG);
        Object message = respondVO.get(AjaxResult.MSG_TAG);

        // 检查：登录是否已经失效
        if (HttpStatus.NOT_LOGIN.equals(code)) {
            // 清空上次登录的信息
            this.header.clear();
            throw new RuntimeException("发送数据到云端：" + this.uri + "失败:code=" + code + "失败:message=" + message);
        }

        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new RuntimeException("发送数据到云端：" + this.uri + "失败:code=" + code + "失败:message=" + message);
        }

        return respondVO;
    }
}
