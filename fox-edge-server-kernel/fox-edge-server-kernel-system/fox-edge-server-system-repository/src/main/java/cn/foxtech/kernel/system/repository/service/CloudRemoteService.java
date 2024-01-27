package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.http.HttpClientUtil;
import cn.foxtech.common.utils.http.UploadUtil;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.constants.RepoConfigConstant;
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

@Component
@Getter(value = AccessLevel.PUBLIC)
public class CloudRemoteService {
    private final Logger logger = Logger.getLogger(this.getClass());

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
     * 云端服务
     */
    private String host = "http://localhost:8080";

    /**
     * 锁定时间：默认60秒
     * 登录失败后，等60秒再重新登录，避免云端认为是恶意攻击，而锁定账号
     */
    private Integer lockdown = 60;

    @Autowired
    private InitialConfigService configService;

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
     * @throws IOException 异常信息
     */
    public synchronized void login() throws IOException {
        // 获得账号/密码
        Map<String, Object> valueConfig = this.configService.getConfigParam("repositoryConfig");
        if (valueConfig.isEmpty()) {
            throw new RuntimeException("登录云端服务器：从Redis配置中获得账号/密码失败！");
        }

        // 取出信息
        this.host = (String) valueConfig.getOrDefault(RepoConfigConstant.filed_config_host, "http://localhost:8080");
        this.username = (String) valueConfig.getOrDefault(RepoConfigConstant.filed_config_username, "username");
        this.password = (String) valueConfig.getOrDefault(RepoConfigConstant.filed_config_password, "");
        this.lockdown = (Integer) valueConfig.getOrDefault(RepoConfigConstant.filed_config_lockdown, 60);

        Map<String, Object> request = new HashMap<>();
        request.put("username", this.username);
        request.put("password", this.password);

        // 不用产生后台日志：这个自动操作，会产生太多的垃圾数据
        this.logger.info("登录云端服务器：开始登录！");

        // 发送请求
        Map<String, Object> respond = HttpClientUtil.executePost(this.host + "/auth/login", request, Map.class);
        if (respond == null) {
            this.loginLastTime = System.currentTimeMillis();
            throw new RuntimeException("登录云端服务器：登录失敗！ " + this.host);
        }

        // 检查：是否登录成功
        Object code = respond.get(AjaxResult.CODE_TAG);
        Object message = respond.get(AjaxResult.MSG_TAG);
        if (!HttpStatus.SUCCESS.equals(code)) {
            this.loginLastTime = System.currentTimeMillis();
            throw new RuntimeException("登录云端服务器：登录失敗！ " + this.host + ",code=" + code + ",message=" + message);
        }

        // 提取token
        String token = (String) Maps.getValue(respond, AjaxResult.DATA_TAG, "access_token");
        if (token == null || token.isEmpty()) {
            this.loginLastTime = System.currentTimeMillis();
            throw new RuntimeException("云端返回的token异常");
        }

        // 保存token，方便后面反复使用
        this.header.put("Authorization", "Bearer " + token);

        String logMessage = "登录云端服务器：登录成功！";
        this.consoleService.info(logMessage);
        this.logger.info(logMessage);
    }

    public <REQ> Map<String, Object> executePost(String res, REQ requestVO) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(requestVO);
        return this.executePost(res, json);
    }

    public Map<String, Object> executePost(String res, String requestJson) throws IOException {
        // 先尝试执行：可能账号过期而被拒绝
        Map<String, Object> respondVO = this.executePostTry(res, requestJson);

        // 检查：是否登录成功
        Object code = respondVO.get(AjaxResult.CODE_TAG);
        Object message = respondVO.get(AjaxResult.MSG_TAG);

        // 检查：登录是否已经失效
        if (HttpStatus.NOT_LOGIN.equals(code)) {
            // 清空上次登录的信息
            this.header.clear();

            // 此时，再尝试自动登录并执行一次，
            respondVO = this.executePostTry(res, requestJson);

            code = respondVO.get(AjaxResult.CODE_TAG);
            message = respondVO.get(AjaxResult.MSG_TAG);

            // 如果失败，那就没啥好说的了，返回异常
            if (!HttpStatus.SUCCESS.equals(code)) {
                throw new RuntimeException("发送数据到云端：" + this.host + "失败:code=" + code + "失败:message=" + message);
            }
        }

        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new RuntimeException("发送数据到云端：" + this.host + "失败:" + respondVO);
        }

        return respondVO;
    }

    private Map<String, Object> executePostTry(String res, String requestJson) throws IOException {
        // 检查：是否未登录
        if (this.header.isEmpty()) {
            this.login();
        }

        // 检查有没有获得登录成功后的云端令牌
        if (this.header.isEmpty()) {
            throw new RuntimeException("未登录到云端!");
        }

        String respondJson = HttpClientUtil.executePost(this.host + res, requestJson, this.header);

        // 转换成json
        ObjectMapper objectMapper = new ObjectMapper();        // 转换JSON结构
        return objectMapper.readValue(respondJson, Map.class);
    }

    public Map<String, Object> executeGet(String res, Map<String, String> param) throws IOException {
        // 先尝试执行：可能账号过期而被拒绝
        Map<String, Object> respondVO = this.executeGetTry(res, param);

        // 检查：是否登录成功
        Object code = respondVO.get(AjaxResult.CODE_TAG);

        // 检查：登录是否已经失效
        if (HttpStatus.NOT_LOGIN.equals(code)) {
            // 清空上次登录的信息
            this.header.clear();

            // 此时，再尝试自动登录并执行一次，
            respondVO = this.executeGet(res, param);

            code = respondVO.get(AjaxResult.CODE_TAG);

            // 如果失败，那就没啥好说的了，返回异常
            if (!HttpStatus.SUCCESS.equals(code)) {
                throw new RuntimeException("发送数据到云端：" + this.host + "失败:" + respondVO);
            }
        }

        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new RuntimeException("发送数据到云端：" + this.host + "失败:" + respondVO);
        }

        return respondVO;
    }

    private Map<String, Object> executeGetTry(String res, Map<String, String> param) throws IOException {
        // 检查：是否未登录
        if (this.header.isEmpty()) {
            this.login();
        }

        // 检查有没有获得登录成功后的云端令牌
        if (this.header.isEmpty()) {
            throw new RuntimeException("未登录到云端!");
        }

        String respondJson = HttpClientUtil.executeGet(this.host + res, param, this.header);

        // 转换成json
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(respondJson, Map.class);
    }

    /**
     * 上传文件
     *
     * @param res      上传的URL
     * @param formData 表单数据
     * @return 操作结果
     * @throws IOException 异常
     */
    public Map<String, Object> executeUpload(String res, Map<String, Object> formData) throws IOException {
        // 先尝试执行：可能账号过期而被拒绝
        Map<String, Object> respondVO = this.executeUploadTry(res, formData);

        // 检查：是否登录成功
        Object code = respondVO.get(AjaxResult.CODE_TAG);

        // 检查：登录是否已经失效
        if (HttpStatus.NOT_LOGIN.equals(code)) {
            // 清空上次登录的信息
            this.header.clear();

            // 此时，再尝试自动登录并执行一次，
            respondVO = this.executeUploadTry(res, formData);

            code = respondVO.get(AjaxResult.CODE_TAG);

            // 如果失败，那就没啥好说的了，返回异常
            if (!HttpStatus.SUCCESS.equals(code)) {
                throw new RuntimeException("发送数据到云端：" + this.host + "失败:" + respondVO);
            }
        }

        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new RuntimeException("发送数据到云端：" + this.host + "失败:code=" + respondVO);
        }

        return respondVO;
    }

    private Map<String, Object> executeUploadTry(String res, Map<String, Object> formData) throws IOException {
        // 检查：是否未登录
        if (this.header.isEmpty()) {
            this.login();
        }

        // 检查有没有获得登录成功后的云端令牌
        if (this.header.isEmpty()) {
            throw new RuntimeException("未登录到云端!");
        }


        String respondJson = UploadUtil.multipartPost(this.host + res, this.header, formData);

        // 转换成json
        ObjectMapper objectMapper = new ObjectMapper();        // 转换JSON结构
        return objectMapper.readValue(respondJson, Map.class);
    }
}
