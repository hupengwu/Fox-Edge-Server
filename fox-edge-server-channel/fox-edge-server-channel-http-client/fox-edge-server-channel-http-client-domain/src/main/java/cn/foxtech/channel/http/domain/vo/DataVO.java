package cn.foxtech.channel.http.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DataVO {
    /**
     * 资源名称
     */
    private String url;

    /**
     * 操作方法
     */
    private String method;

    /**
     * 请求参数
     */
    /**
     * 请求参数
     */
    private Object body;

    /**
     * 转换格式
     */
    @SuppressWarnings("unchecked")
    public static DataVO buildVO(Object request) {
        DataVO dataVO = new DataVO();
        Map<String, Object> sendData = (Map<String, Object>) request;
        dataVO.setUrl((String) sendData.get("url"));
        dataVO.setMethod((String) sendData.get("method"));
        dataVO.setBody(sendData.get("body"));
        return dataVO;
    }
}
