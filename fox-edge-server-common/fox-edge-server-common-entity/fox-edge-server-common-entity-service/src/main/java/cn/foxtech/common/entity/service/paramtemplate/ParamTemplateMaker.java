package cn.foxtech.common.entity.service.paramtemplate;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ParamTemplateEntity;
import cn.foxtech.common.entity.entity.ParamTemplatePo;
import cn.foxtech.common.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DeviceConfigPo是数据库格式的对象，DeviceConfigEntity是内存格式的对象，两者需要进行转换
 */
public class ParamTemplateMaker {
    /**
     * PO转Entity
     *
     * @param deviceList
     * @return
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> deviceList) {
        List<BaseEntity> deviceConfigList = new ArrayList<>();
        for (BaseEntity entity : deviceList) {
            ParamTemplatePo po = (ParamTemplatePo) entity;


            ParamTemplateEntity config = ParamTemplateMaker.makePo2Entity(po);
            deviceConfigList.add(config);
        }

        return deviceConfigList;
    }

    public static ParamTemplatePo makeEntity2Po(ParamTemplateEntity entity) {
        ParamTemplatePo result = new ParamTemplatePo();
        result.bind(entity);

        result.setTemplateParam(JsonUtils.buildJsonWithoutException(entity.getTemplateParam()));
        return result;
    }

    public static ParamTemplateEntity makePo2Entity(ParamTemplatePo entity) {
        ParamTemplateEntity result = new ParamTemplateEntity();
        result.bind(entity);

        try {
            if (entity.getTemplateParam().startsWith("[") && entity.getTemplateParam().endsWith("]")) {
                List<Object> params = JsonUtils.buildObject(entity.getTemplateParam(), List.class);
                if (params != null) {
                    result.setTemplateParam(params);
                } else {
                    System.out.println("设备配置参数转换Json对象失败：" + entity.getTemplateName() + ":" + entity.getTemplateParam());
                }
            }
            if (entity.getTemplateParam().startsWith("{") && entity.getTemplateParam().endsWith("}")) {
                Map<String, Object> params = JsonUtils.buildObject(entity.getTemplateParam(), Map.class);
                if (params != null) {
                    result.setTemplateParam(params);
                } else {
                    System.out.println("设备配置参数转换Json对象失败：" + entity.getTemplateName() + ":" + entity.getTemplateParam());
                }
            }


        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getTemplateName() + ":" + entity.getTemplateParam());
            e.printStackTrace();
        }

        return result;
    }
}
