package cn.foxtech.device.protocol.core.template;

import cn.foxtech.device.protocol.core.exception.ProtocolException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemplateContainer {
    /**
     * 系统级模板信息：保存Class模板的实例，对应的是CSV格式的一类文件，即文件格式类型
     */
    private final Map<String, ITemplate> sysTemplate = new ConcurrentHashMap<>();

    /**
     * 用户级模板信息：保存的文件实例，对应的是一个个CSV文件，即文件
     */
    private final Map<String, Object> userTemplate = new ConcurrentHashMap<>();


    /**
     * 实例化一个模板容器对象
     * @return 模板容器对象
     */
    public static TemplateContainer newInstance() {
        return new TemplateContainer();
    }

    /**
     * 获得CLASS模板信息
     *
     * @param clazz CLASS信息
     * @param <T> 数据类型
     * @return 模板
     */
    public <T> ITemplate getSysTemplateInfo(Class<T> clazz) {
        ITemplate templateInstance = this.sysTemplate.get(clazz.getSimpleName());
        if (templateInstance == null) {
            try {
                templateInstance = (ITemplate) clazz.newInstance();
                this.sysTemplate.put(clazz.getSimpleName(), templateInstance);
                return templateInstance;
            } catch (Exception e) {
                throw new ProtocolException("实例化模板对象失败:" + clazz.getSimpleName());
            }
        }

        return templateInstance;
    }

    public <T> ITemplate newTemplateInstance(Class<T> clazz) {
        try {
            return (ITemplate) clazz.newInstance();
        } catch (Exception e) {
            throw new ProtocolException("实例化模板对象失败:" + clazz.getSimpleName());
        }
    }

    /**
     * 获得模板
     *
     * @param templateName 模板名称，比如Read System Measures Table
     * @param defaultTable CSV文件名称，比如101.CETUPS_Read System Measures Table.csv
     * @param clazz        模板对象类型 比如JHoldingRegistersTemplate.class
     * @param <T>          模板对象类型，比如JHoldingRegistersTemplate
     * @return 模板对象 比如jHoldingRegistersTemplate
     */
    public <T> T getTemplate(String templateName, String defaultTable, Class<T> clazz) {
        // 检查：是否为ITemplate的派生类
        if (!ITemplate.class.isAssignableFrom(clazz)) {
            throw new ProtocolException("这不是ITemplate的派生类型:" + clazz.getSimpleName());
        }

        // 获得class的信息
        ITemplate sysTemplateInfo = this.getSysTemplateInfo(clazz);

        // 获得该模板类型的多个用户级模板列表
        Map<String, Object> template4operate = (Map<String, Object>) userTemplate.get(sysTemplateInfo.getSysTemplateName());
        if (template4operate == null) {
            template4operate = new ConcurrentHashMap<>();
            userTemplate.put(sysTemplateInfo.getSysTemplateName(), template4operate);
        }

        // 获得该模板名称的模板
        ITemplate template = (ITemplate) template4operate.get(templateName);
        if (template == null) {
            // 不存在的话，就根据表名称构造模板
            template = this.newTemplateInstance(clazz);
            template.loadCsvFile(defaultTable);

            // 保存模板
            template4operate.put(templateName, template);
        }

        // 转换对象类型
        if (clazz.isInstance(template)) {
            return (T) template;
        }

        return null;
    }
}
