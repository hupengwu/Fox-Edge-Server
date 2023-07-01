package cn.foxtech.proxy.cloud.publisher.service;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 对远端云中的数据，进行访问操作
 */
public abstract class EntityRemoteCloudService {
    @Autowired
    private CloudEntityRemoteService remoteService;

    public abstract String getUrlTimestamp();

    public abstract String getUrlReset();

    public String getUrlComplete() {
        return "";
    }

    public abstract String getUrlEntity();

    /**
     * 查询时间戳
     *
     * @param edgeId         边缘服务器的CPUID
     * @param entityTypeList 实体类型列表
     * @return 时间戳
     * @throws IOException 异常
     */
    public Map<String, Object> queryTimestamp(String edgeId, Set<String> entityTypeList) throws IOException, ServiceException {
        Map<String, Object> body = new HashMap<>();
        body.put("edgeId", edgeId);
        body.put("entityTypeList", entityTypeList);

        Map<String, Object> respond = this.remoteService.executePost(this.getUrlTimestamp(), body);

        // 检查：是否返回了异常
        Integer code = (Integer) respond.get("code");
        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new ServiceException("未返回成功代码，这是一个异常的返回:" + respond.get("msg"));
        }

        // 检查：是否有返回了数据
        Map<String, Object> data = (Map<String, Object>) respond.get("data");
        if (data == null) {
            throw new ServiceException("返回的data为空");
        }

        return data;
    }

    public Map<String, Object> queryTimestamp(String edgeId, String entityType) throws IOException, ServiceException {
        Set<String> entityTypeList = new HashSet<>();
        entityTypeList.add(entityType);

        return this.queryTimestamp(edgeId, entityTypeList);
    }

    /**
     * 查询复位标记
     *
     * @param edgeId         边缘服务器的CPUID
     * @param entityTypeList 实体类型列表
     * @return 重置标记
     * @throws IOException 异常信息
     */
    public Set<String> queryResetFlag(String edgeId, Set<String> entityTypeList) throws IOException, ServiceException {
        Map<String, Object> body = new HashMap<>();
        body.put("edgeId", edgeId);
        body.put("entityTypeList", entityTypeList);
        body.put("operate", "get");

        Map<String, Object> respond = this.remoteService.executePost(this.getUrlReset(), body);

        // 检查：是否返回了异常
        Integer code = (Integer) respond.get("code");
        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new ServiceException("未返回成功代码，这是一个异常的返回:" + respond.get("msg"));
        }

        // 检查：是否有返回了数据
        Map<String, Object> data = (Map<String, Object>) respond.get("data");
        if (data == null) {
            throw new ServiceException("返回的data为空");
        }

        Set<String> result = new HashSet<>();
        for (String key : data.keySet()) {
            if (Boolean.TRUE.equals(data.get(key))) {
                result.add(key);
            }
        }

        return result;
    }


    /**
     * 发布操作
     *
     * @param edgeId     边缘服务器的CPUID
     * @param entityType 实体类型
     * @param timeStamp  时间戳
     * @param dataList   数据
     * @return 请求返回的结果
     * @throws IOException 异常信息
     */
    public Map<String, Object> publishEntity(String edgeId, String entityType, String timeStamp, Map<String, Object> dataList) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("edgeId", edgeId);
        body.put("entityType", entityType);
        body.put("data", new HashMap<>());

        if (timeStamp != null && !timeStamp.isEmpty()) {
            body.put("timeStamp", timeStamp);
        }
        if (dataList != null) {
            body.put("data", dataList);
        }

        Map<String, Object> respond = this.remoteService.executePost(this.getUrlEntity(), body);

        // 检查：是否返回了异常
        Integer code = (Integer) respond.get("code");
        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new ServiceException("未返回成功代码，这是一个异常的返回:" + respond.get("msg"));
        }

        return respond;
    }

    /**
     * 发布重置操作
     *
     * @param edgeId     边缘服务器的CPUID
     * @param entityType 实体类型
     * @param timeStamp  时间戳
     * @return 返回的结果
     * @throws IOException 异常信息
     */
    public Map<String, Object> publishReset(String edgeId, String entityType, String timeStamp) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("edgeId", edgeId);
        body.put("entityType", entityType);
        body.put("timeStamp", timeStamp);

        Map<String, Object> respond = this.remoteService.executePost(this.getUrlReset(), body);

        // 检查：是否返回了异常
        Integer code = (Integer) respond.get("code");
        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new ServiceException("未返回成功代码，这是一个异常的返回:" + respond.get("msg"));
        }

        return respond;
    }

    public Map<String, Object> publishComplete(String edgeId, String entityType, String timeStamp) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("edgeId", edgeId);
        body.put("entityType", entityType);
        body.put("timeStamp", timeStamp);

        Map<String, Object> respond = this.remoteService.executePost(this.getUrlComplete(), body);

        // 检查：是否返回了异常
        Integer code = (Integer) respond.get("code");
        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new ServiceException("未返回成功代码，这是一个异常的返回:" + respond.get("msg"));
        }

        return respond;
    }
}
