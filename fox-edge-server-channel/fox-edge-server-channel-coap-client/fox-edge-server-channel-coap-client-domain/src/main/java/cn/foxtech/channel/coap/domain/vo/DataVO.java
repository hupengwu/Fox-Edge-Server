package cn.foxtech.channel.coap.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DataVO {
    /**
     * IP地址
     */
    private String ip;

    /**
     * 端口号
     */
    private int port;

    /**
     * 资源名称
     */
    private String resource;

    /**
     * 操作方法
     */
    private String method;

    /**
     * 请求参数
     */
    private List<Map<String, Object>> param = new ArrayList<>();

    /**
     * 转换格式
     */
    @SuppressWarnings("unchecked")
    public static DataVO buildVO(Object request) {
        DataVO dataVO = new DataVO();
        Map<String, Object> sendData = (Map<String, Object>) request;
        dataVO.setIp((String) sendData.get("ip"));
        dataVO.setPort((Integer) sendData.get("port"));
        dataVO.setResource((String) sendData.get("resource"));
        dataVO.setMethod((String) sendData.get("method"));
        List<Map<String, Object>> param = (List<Map<String, Object>>) sendData.get("param");
        dataVO.setParam(param);
        return dataVO;
    }
}
