package cn.foxtech.common.entity.service.operaterecord;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.entity.entity.OperateRecordEntity;
import cn.foxtech.common.entity.entity.OperateRecordPo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OperateRecordPo是数据库格式的对象，OperateRecordEntity是内存格式的对象，两者需要进行转换
 */
public class OperateRecordMaker {
    /**
     * PO转Entity
     *
     * @param recordList
     * @return
     */
    public static List<OperateRecordEntity> makePoList2EntityList(List<OperateRecordPo> recordList) {
        List<OperateRecordEntity> operateRecordList = new ArrayList<>();
        for (BaseEntity entity : recordList) {
            OperateRecordPo po = (OperateRecordPo) entity;

            OperateRecordEntity config = OperateRecordMaker.makePo2Entity(po);
            operateRecordList.add(config);
        }

        return operateRecordList;
    }

    public static OperateRecordPo makeEntity2Po(OperateRecordEntity entity) {
        OperateRecordPo result = new OperateRecordPo();
        result.bind(entity);

        result.setRecordParam(JsonUtils.buildJsonWithoutException(entity.getRecordParam()));
        result.setRecordData(JsonUtils.buildJsonWithoutException(entity.getRecordData()));
        return result;
    }

    public static OperateRecordEntity makePo2Entity(OperateRecordPo entity) {
        OperateRecordEntity result = new OperateRecordEntity();
        result.bind(entity);

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getRecordData(), Map.class);
            if (params != null) {
                result.setRecordData(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getRecordData());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getRecordData());
            e.printStackTrace();
        }

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getRecordParam(), Map.class);
            if (params != null) {
                result.setRecordParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getRecordParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceName() + ":" + entity.getRecordParam());
            e.printStackTrace();
        }

        return result;
    }
}
