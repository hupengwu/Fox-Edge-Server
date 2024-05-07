package cn.foxtech.device.protocol.v1.core.template;

/**
 * 配置模板接口
 */
public interface ITemplate {
    /**
     * 系统级模板：代表的是文件格式
     *
     * @return 模板名称
     */
    String getSysTemplateName();

    /**
     * 装载JSON数据模型
     *
     * @param table 表名称
     */
    void loadJsnModel(String table);
}
