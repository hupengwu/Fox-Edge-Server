package cn.foxtech.device.protocol.v1.dlt645.core;

import cn.foxtech.device.protocol.v1.utils.ContainerUtils;
import cn.foxtech.device.protocol.v1.dlt645.core.loader.DLT645v1997CsvLoader;
import cn.foxtech.device.protocol.v1.dlt645.core.loader.DLT645v2007CsvLoader;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645DataEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645v1997DataEntity;
import cn.foxtech.device.protocol.v1.dlt645.core.entity.DLT645v2007DataEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 来自CSV文件的模板信息
 */
public class DLT645Template {
    /**
     * 静态实例
     */
    private static final DLT645Template template = new DLT645Template();

    /**
     * 索引表
     */
    private Map<String, DLT645DataEntity> name2entity;
    private Map<String, DLT645DataEntity> din2entity;

    public static DLT645Template inst() {
        return template;
    }

    /**
     * 获得对象信息
     *
     * @param defaultTable
     * @return 对象副本
     */
    public synchronized Map<String, DLT645DataEntity> getTemplateByName(String version, String defaultTable) {
        if (this.name2entity == null) {
            if (DLT645Define.PRO_VER_1997.equals(version)) {
                DLT645v1997CsvLoader loader = new DLT645v1997CsvLoader();
                List<DLT645DataEntity> entityList = loader.loadCsvFile(defaultTable);
                Map<String, DLT645DataEntity> nameMap = ContainerUtils.buildMapByKey(entityList, DLT645v1997DataEntity::getName);
                this.name2entity = new ConcurrentHashMap<>();
                this.name2entity.putAll(nameMap);
            }
            if (DLT645Define.PRO_VER_2007.equals(version)) {
                DLT645v2007CsvLoader loader = new DLT645v2007CsvLoader();
                List<DLT645DataEntity> entityList = loader.loadCsvFile(defaultTable);
                Map<String, DLT645DataEntity> nameMap = ContainerUtils.buildMapByKey(entityList, DLT645v2007DataEntity::getName);
                this.name2entity = new ConcurrentHashMap<>();
                this.name2entity.putAll(nameMap);
            }
        }

        return this.name2entity;
    }

    public synchronized Map<String, DLT645DataEntity> getTemplateByDIn(String version, String defaultTable) {
        if (this.din2entity == null) {
            if (DLT645Define.PRO_VER_1997.equals(version)) {
                DLT645v1997CsvLoader loader = new DLT645v1997CsvLoader();
                List<DLT645DataEntity> entityList = loader.loadCsvFile(defaultTable);
                Map<String, DLT645DataEntity> keyMap = ContainerUtils.buildMapByKey(entityList, DLT645v1997DataEntity::getKey);
                this.din2entity = new ConcurrentHashMap<>();
                this.din2entity.putAll(keyMap);
            }
            if (DLT645Define.PRO_VER_2007.equals(version)) {
                DLT645v2007CsvLoader loader = new DLT645v2007CsvLoader();
                List<DLT645DataEntity> entityList = loader.loadCsvFile(defaultTable);
                Map<String, DLT645DataEntity> keyMap = ContainerUtils.buildMapByKey(entityList, DLT645v2007DataEntity::getKey);
                this.din2entity = new ConcurrentHashMap<>();
                this.din2entity.putAll(keyMap);
            }
        }

        return this.din2entity;
    }
}
