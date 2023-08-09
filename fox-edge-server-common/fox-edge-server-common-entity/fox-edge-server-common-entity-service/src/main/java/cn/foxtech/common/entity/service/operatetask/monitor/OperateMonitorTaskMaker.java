package cn.foxtech.common.entity.service.operatetask.monitor;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateMonitorTaskEntity;
import cn.foxtech.common.entity.entity.OperateMonitorTaskPo;
import cn.foxtech.common.utils.json.JsonUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OperateTemplatePo是数据库格式的对象，OperateTemplateEntity是内存格式的对象，两者需要进行转换
 */
public class OperateMonitorTaskMaker {
    private static final Logger logger = Logger.getLogger(OperateMonitorTaskMaker.class);

    /**
     * PO转Entity
     *
     * @param poList PO列表
     * @return 实体列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> poList) {
        List<BaseEntity> operateRecordList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            OperateMonitorTaskPo po = (OperateMonitorTaskPo) entity;

            OperateMonitorTaskEntity config = OperateMonitorTaskMaker.makePo2Entity(po);
            operateRecordList.add(config);
        }

        return operateRecordList;
    }

    public static OperateMonitorTaskPo makeEntity2Po(OperateMonitorTaskEntity entity) {
        OperateMonitorTaskPo result = new OperateMonitorTaskPo();
        result.bind(entity);

        result.setDeviceIds(JsonUtils.buildJsonWithoutException(entity.getDeviceIds()));
        result.setTemplateParam(JsonUtils.buildJsonWithoutException(entity.getTemplateParam()));
        return result;
    }

    public static OperateMonitorTaskEntity makePo2Entity(OperateMonitorTaskPo entity) {
        OperateMonitorTaskEntity result = new OperateMonitorTaskEntity();
        result.bind(entity);

        try {
            List<Map<String, Object>> params = JsonUtils.buildObject(entity.getTemplateParam(), List.class);
            if (params != null) {
                result.setTemplateParam(params);
            } else {
                logger.error("设备配置参数转换Json对象失败：" + entity.getTemplateName() + ":" + entity.getTemplateParam());
            }
        } catch (Exception e) {
            logger.error("设备配置参数转换Json对象失败：" + entity.getTemplateName() + ":" + entity.getTemplateParam());
        }

        try {
            List<Long> deviceIds = JsonUtils.buildObject(entity.getDeviceIds(), List.class);
            if (deviceIds != null) {
                result.getDeviceIds().addAll(deviceIds);
            } else {
                logger.error("设备配置参数转换Json对象失败：" + entity.getDeviceIds() + ":" + entity.getDeviceIds());
            }
        } catch (Exception e) {
            logger.error("设备配置参数转换Json对象失败：" + entity.getDeviceIds() + ":" + entity.getDeviceIds());
        }

        return result;
    }
}
