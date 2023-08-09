package cn.foxtech.common.entity.service.operatetask.channel;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateChannelTaskEntity;
import cn.foxtech.common.entity.entity.OperateChannelTaskPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OperateTemplatePo是数据库格式的对象，OperateTemplateEntity是内存格式的对象，两者需要进行转换
 */
public class OperateChannelTaskMaker {
    /**
     * PO转Entity
     *
     * @param poList PO列表
     * @return 实体列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> poList) {
        List<BaseEntity> operateRecordList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            OperateChannelTaskPo po = (OperateChannelTaskPo) entity;

            OperateChannelTaskEntity config = OperateChannelTaskMaker.makePo2Entity(po);
            operateRecordList.add(config);
        }

        return operateRecordList;
    }

    public static OperateChannelTaskPo makeEntity2Po(OperateChannelTaskEntity entity) {
        OperateChannelTaskPo result = new OperateChannelTaskPo();
        result.bind(entity);

        result.setTaskParam(JsonUtils.buildJsonWithoutException(entity.getTaskParam()));
        return result;
    }

    public static OperateChannelTaskEntity makePo2Entity(OperateChannelTaskPo entity) {
        OperateChannelTaskEntity result = new OperateChannelTaskEntity();
        result.bind(entity);

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getTaskParam(), Map.class);
            if (params != null) {
                result.setTaskParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getTaskParam() + ":" + entity.getTaskParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getTaskParam() + ":" + entity.getTaskParam());
            e.printStackTrace();
        }

        return result;
    }
}
