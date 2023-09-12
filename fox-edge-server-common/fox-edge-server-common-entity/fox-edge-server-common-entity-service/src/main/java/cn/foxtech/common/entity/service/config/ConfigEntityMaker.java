package cn.foxtech.common.entity.service.config;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.entity.ConfigPo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ConfigPo是数据库格式的对象，ConfigEntity是内存格式的对象，两者需要进行转换
 */
public class ConfigEntityMaker {
    /**
     * PO转Entity
     *
     * @param List 实体列表
     * @return 实体列表
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> List) {
        List<BaseEntity> resultList = new ArrayList<>();
        for (BaseEntity entity : List) {
            ConfigPo po = (ConfigPo) entity;


            ConfigEntity config = ConfigEntityMaker.makePo2Entity(po);
            resultList.add(config);
        }

        return resultList;
    }

    public static ConfigPo makeEntity2Po(ConfigEntity entity) {
        ConfigPo result = new ConfigPo();
        result.bind(entity);

        result.setConfigValue(JsonUtils.buildJsonWithoutException(entity.getConfigValue()));
        result.setConfigParam(JsonUtils.buildJsonWithoutException(entity.getConfigParam()));
        return result;
    }

    public static ConfigEntity makePo2Entity(ConfigPo entity) {
        ConfigEntity result = new ConfigEntity();
        result.bind(entity);

        try {
            Map<String, Object> value = JsonUtils.buildObject(entity.getConfigValue(), Map.class);
            if (value != null) {
                result.setConfigValue(value);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getConfigName() + ":" + entity.getConfigValue());
            }

            Map<String, Object> param = JsonUtils.buildObject(entity.getConfigParam(), Map.class);
            if (param != null) {
                result.setConfigParam(param);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getConfigName() + ":" + entity.getConfigParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getConfigName() + ":" + entity.getConfigValue() + ":" + entity.getConfigParam());
            e.printStackTrace();
        }

        return result;
    }
}
