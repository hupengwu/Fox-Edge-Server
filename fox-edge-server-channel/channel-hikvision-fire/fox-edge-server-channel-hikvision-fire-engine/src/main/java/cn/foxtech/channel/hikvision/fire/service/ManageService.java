package cn.foxtech.channel.hikvision.fire.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.domain.ChannelRestfulConstant;
import cn.foxtech.channel.socket.core.service.ChannelManager;
import cn.foxtech.common.domain.vo.RestFulVO;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ManageService {
    @Autowired
    private ChannelManager channelManager;

    public ChannelRespondVO manageChannel(ChannelRequestVO requestVO) {
        ChannelRespondVO respondVO = new ChannelRespondVO();
        respondVO.bindBaseVO(requestVO);

        RestFulVO restfulVO = JsonUtils.buildObjectWithoutException((Map) requestVO.getSend(), RestFulVO.class);
        // 1、查询全体南向链路
        if (restfulVO.getUri().equals(ChannelRestfulConstant.resource_south_links_query)) {
            Object result = this.querySouthLinks((Map<String, Object>) restfulVO.getData());
            respondVO.setRecv(result);
        }
        // 2、分页查询南向链路
        else if (restfulVO.getUri().equals(ChannelRestfulConstant.resource_south_links_page)) {
            Object result = this.querySouthLinkPage((Map<String, Object>) restfulVO.getData());
            respondVO.setRecv(result);
        }
        // 不支持的操作
        else {
            throw new ServiceException("不支持的操作:" + restfulVO.getUri());
        }

        return respondVO;
    }


    private AjaxResult querySouthLinkPage(Map<String, Object> param) {
        List<Map<String, Object>> resultList = this.querySouthLinks(param);
        return PageUtils.getPageMapList(resultList, param);
    }

    private List<Map<String, Object>> querySouthLinks(Map<String, Object> param) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        Set<String> keys = this.channelManager.getServiceKeys();
        for (String serviceKey : keys) {
            ChannelHandlerContext ctx = this.channelManager.getContext(serviceKey);
            if (ctx == null) {
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("serviceKey", serviceKey);
            map.put("remoteAddress", ctx.channel().remoteAddress().toString());

            resultList.add(map);
        }

        return resultList;
    }
}
