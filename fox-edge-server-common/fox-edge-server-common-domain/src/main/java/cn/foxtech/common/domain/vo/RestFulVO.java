/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * RestFul风格的VO
 * 说明：
 * RestFulVO这是面向Fox-Edge的内部各服务之间的【东西向接口】，只有新增了UUID这个额外的特性
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RestFulVO {
    /**
     * UUID
     */
    private String uuid;

    /**
     * 方法名稱：例如，POST/PUT/GET/DELETE
     */
    private String method;

    /**
     * URI：能夠提供HTTP服务的资源名称
     */
    private String uri;

    /**
     * 数据：类似HTTP的BODY
     */
    private Object data;

    public void bindResVO(RestFulVO vo) {
        this.method = vo.method;
        this.uuid = vo.uuid;
        this.uri = vo.uri;
        this.data = vo.data;
    }

    public void bindResVO(Map<String, Object> map) {
        this.method = (String) map.get("method");
        this.uuid = (String) map.get("uuid");
        this.uri = (String) map.get("uri");
        this.data = map.get("data");
    }
}
