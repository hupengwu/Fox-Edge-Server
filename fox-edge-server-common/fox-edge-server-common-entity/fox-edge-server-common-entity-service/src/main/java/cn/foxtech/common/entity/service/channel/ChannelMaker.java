package cn.foxtech.common.entity.service.channel;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.ChannelPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ChannelPo是数据库格式的对象，ChannelEntity是内存格式的对象，两者需要进行转换
 */
public class ChannelMaker {
    /**
     * PO转Entity
     *
     * @param List 实体列表
     * @return 实体列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> List) {
        List<BaseEntity> ChannelList = new ArrayList<>();
        for (BaseEntity entity : List) {
            ChannelPo po = (ChannelPo) entity;


            ChannelEntity channel = ChannelMaker.makePo2Entity(po);
            ChannelList.add(channel);
        }

        return ChannelList;
    }

    public static ChannelPo makeEntity2Po(ChannelEntity entity) {
        ChannelPo result = new ChannelPo();
        result.bind(entity);

        result.setChannelParam(JsonUtils.buildJsonWithoutException(entity.getChannelParam()));
        result.setExtendParam(JsonUtils.buildJsonWithoutException(entity.getExtendParam()));
        return result;
    }

    public static ChannelEntity makePo2Entity(ChannelPo entity) {
        ChannelEntity result = new ChannelEntity();
        result.bind(entity);

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getChannelParam(), Map.class);
            if (params != null) {
                result.setChannelParam(params);
            } else {
                System.out.println("通道配置参数转换Json对象失败：" + entity.getChannelName() + ":" + entity.getChannelParam());
            }
        } catch (Exception e) {
            System.out.println("通道配置参数转换Json对象失败：" + entity.getChannelName() + ":" + entity.getChannelParam());
            e.printStackTrace();
        }

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getExtendParam(), Map.class);
            if (params != null) {
                result.setExtendParam(params);
            } else {
                System.out.println("通道扩展参数转换Json对象失败：" + entity.getChannelName() + ":" + entity.getExtendParam());
            }
        } catch (Exception e) {
            System.out.println("通道扩展参数转换Json对象失败：" + entity.getChannelName() + ":" + entity.getExtendParam());
            e.printStackTrace();
        }

        return result;
    }
}
