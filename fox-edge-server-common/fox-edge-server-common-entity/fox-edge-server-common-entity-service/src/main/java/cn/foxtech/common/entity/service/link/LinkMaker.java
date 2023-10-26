package cn.foxtech.common.entity.service.link;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.LinkEntity;
import cn.foxtech.common.entity.entity.LinkPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * LinkPo是数据库格式的对象，LinkEntity是内存格式的对象，两者需要进行转换
 */
public class LinkMaker {
    /**
     * PO转Entity
     *
     * @param List 实体列表
     * @return 实体列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> List) {
        List<BaseEntity> entityList = new ArrayList<>();
        for (BaseEntity entity : List) {
            LinkPo po = (LinkPo) entity;


            LinkEntity link = LinkMaker.makePo2Entity(po);
            entityList.add(link);
        }

        return entityList;
    }

    public static LinkPo makeEntity2Po(LinkEntity entity) {
        LinkPo result = new LinkPo();
        result.bind(entity);

        result.setLinkParam(JsonUtils.buildJsonWithoutException(entity.getLinkParam()));
        result.setExtendParam(JsonUtils.buildJsonWithoutException(entity.getExtendParam()));
        return result;
    }

    public static LinkEntity makePo2Entity(LinkPo entity) {
        LinkEntity result = new LinkEntity();
        result.bind(entity);

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getLinkParam(), Map.class);
            if (params != null) {
                result.setLinkParam(params);
            } else {
                System.out.println("链路配置参数转换Json对象失败：" + entity.getLinkName() + ":" + entity.getLinkParam());
            }
        } catch (Exception e) {
            System.out.println("链路配置参数转换Json对象失败：" + entity.getLinkName() + ":" + entity.getLinkParam());
            e.printStackTrace();
        }

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getExtendParam(), Map.class);
            if (params != null) {
                result.setExtendParam(params);
            } else {
                System.out.println("链路扩展参数转换Json对象失败：" + entity.getLinkName() + ":" + entity.getExtendParam());
            }
        } catch (Exception e) {
            System.out.println("链路扩展参数转换Json对象失败：" + entity.getLinkName() + ":" + entity.getExtendParam());
            e.printStackTrace();
        }

        return result;
    }
}
