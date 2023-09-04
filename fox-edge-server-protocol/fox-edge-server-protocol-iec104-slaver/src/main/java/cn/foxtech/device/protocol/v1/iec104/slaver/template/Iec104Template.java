package cn.foxtech.device.protocol.v1.iec104.slaver.template;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IEC104解码器模板的管理
 */
public class Iec104Template {
    /**
     * Key: operateName+templateName+TypeId，比如 总召唤，Read All Data Template，1
     * Value: JReadDataTemplate 模板
     * 比如，上述含义是总召唤命令，获得Read All Data Template模板中的typeId=1的子模板，进行后续解码操作
     */
    private static final Map<String, Object> template = new ConcurrentHashMap<>();

    public static Iec104Template newInstance() {
        return new Iec104Template();
    }


    /**
     * 装载从CSV文件中模板格式
     *
     * @param defaultTable 缺省的表名称
     * @return 生成的模板
     */
    private Object loadTemplate(String defaultTable) {
        JReadDataTemplate jReadHoldingRegistersTemplate = new JReadDataTemplate();
        jReadHoldingRegistersTemplate.loadCsvFile(defaultTable);

        return jReadHoldingRegistersTemplate;
    }

    /**
     * 获得模板
     * @param operateName 操作名，比如总召唤
     * @param templateName 模板名，比如Read All Data Template
     * @param typeId，IEC104的TypeId，比如1，单点值
     * @param defaultTable 模板文件，比如102.IEC104_Read Single Point Signal Table.csv
     * @param clazz java模板类，比如JReadDataTemplate
     * @return 比如JReadDataTemplate
     * @param <T> 参数类型
     */
    public <T> T getTemplate(String operateName, String templateName, Integer typeId, String defaultTable, Class<T> clazz) {
        Map<String, Object> templateByOperateName =  this.template;

        // 获得该操作类型的模板列表
        Map<String, Object> templateByTemplageName = (Map<String, Object>) templateByOperateName.get(operateName);
        if (templateByTemplageName == null) {
            templateByTemplageName = new ConcurrentHashMap<>();
            templateByOperateName.put(operateName, templateByTemplageName);
        }

        // 获得该模板名称的模板列表
        Map<Integer, Object> templateByTypeId = (Map<Integer, Object>) templateByTemplageName.get(templateName);
        if (templateByTypeId == null) {
            templateByTypeId = new ConcurrentHashMap<>();
            templateByTemplageName.put(templateName, templateByTypeId);
        }

        // 获得该模板名称的模板
        Object template = templateByTypeId.get(typeId);
        if (template == null) {
            // 不存在的话，就根据表名称构造模板
            template = this.loadTemplate(defaultTable);
            if (template == null) {
                return null;
            }
        }

        if (template instanceof JReadDataTemplate) {
            JReadDataTemplate jReadHoldingRegistersTemplate = (JReadDataTemplate) template;
            jReadHoldingRegistersTemplate.setTemplate_name(templateName);
            jReadHoldingRegistersTemplate.getOperate().setOperate_name(operateName);
            jReadHoldingRegistersTemplate.getOperate().getDecoder_param().setTable(defaultTable);
        }

        // 保存模板
        templateByTypeId.put(typeId, template);


        // 转换对象类型
        if (clazz.isInstance(template)) {
            return (T) template;
        }

        return null;
    }
}
