package cn.foxtech.common.entity.service.operate;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.entity.OperatePo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OperatePo是数据库格式的对象，OperateEntity是内存格式的对象，两者需要进行转换
 */
public class OperateEntityMaker {
    /**
     * PO转Entity
     *
     * @param poList po列表
     * @return 实体列表
     */
    public static List<OperateEntity> makePoList2EntityList(List<OperatePo> poList) {
        List<OperateEntity> operateList = new ArrayList<>();
        for (BaseEntity entity : poList) {
            OperatePo po = (OperatePo) entity;

            OperateEntity config = OperateEntityMaker.makePo2Entity(po);
            operateList.add(config);
        }

        return operateList;
    }

    public static OperatePo makeEntity2Po(OperateEntity entity) {
        OperatePo result = new OperatePo();
        result.bind(entity);

        result.setEngineParam(JsonUtils.buildJsonWithoutException(entity.getEngineParam()));
        result.setExtendParam(JsonUtils.buildJsonWithoutException(entity.getExtendParam()));
        return result;
    }

    public static OperateEntity makePo2Entity(OperatePo entity) {
        OperateEntity result = new OperateEntity();
        result.bind(entity);

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getEngineParam(), Map.class);
            if (params != null) {
                result.setEngineParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getOperateName() + ":" + entity.getEngineParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getOperateName() + ":" + entity.getEngineParam());
            e.printStackTrace();
        }

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getExtendParam(), Map.class);
            if (params != null) {
                result.setExtendParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getOperateName() + ":" + entity.getExtendParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getOperateName() + ":" + entity.getExtendParam());
            e.printStackTrace();
        }


        return result;
    }
}
