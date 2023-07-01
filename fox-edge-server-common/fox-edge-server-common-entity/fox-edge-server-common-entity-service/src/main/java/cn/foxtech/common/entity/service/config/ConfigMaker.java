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
public class ConfigMaker {
    /**
     * PO转Entity
     *
     * @param List
     * @return
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> List) {
        List<BaseEntity> ConfigList = new ArrayList<>();
        for (BaseEntity entity : List) {
            ConfigPo po = (ConfigPo) entity;


            ConfigEntity config = ConfigMaker.makePo2Entity(po);
            ConfigList.add(config);
        }

        return ConfigList;
    }

    public static ConfigPo makeEntity2Po(ConfigEntity entity) {
        ConfigPo result = new ConfigPo();
        result.bind(entity);

        result.setConfigParam(JsonUtils.buildJsonWithoutException(entity.getConfigParam()));
        return result;
    }

    public static ConfigEntity makePo2Entity(ConfigPo entity) {
        ConfigEntity result = new ConfigEntity();
        result.bind(entity);

        try {
            Map<String, Object> params = JsonUtils.buildObject(entity.getConfigParam(), Map.class);
            if (params != null) {
                result.setConfigParam(params);
            } else {
                System.out.println("设备配置参数转换Json对象失败：" + entity.getConfigName() + ":" + entity.getConfigParam());
            }
        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getConfigName() + ":" + entity.getConfigParam());
            e.printStackTrace();
        }

        return result;
    }
}
