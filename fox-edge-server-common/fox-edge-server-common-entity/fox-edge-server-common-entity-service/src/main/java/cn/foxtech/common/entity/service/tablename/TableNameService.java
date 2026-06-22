package cn.foxtech.common.entity.service.tablename;

import cn.foxtech.common.entity.entity.TableNamePo;
import cn.foxtech.common.utils.number.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TableNameService {
    @Autowired(required = false)
    private TableNameMapper mapper;

    public List<String> queryTableNames(String filter) {
        // 查询数据库表名称
        String sql = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES" + filter;
        List<TableNamePo> list = this.mapper.executeSelectData(sql);

        List<String> nameList = new ArrayList<>();
        for (TableNamePo po : list) {
            if (po.getTableName() == null) {
                continue;
            }
            nameList.add(po.getTableName());
        }


        return nameList;
    }

    /**
     * 查询数据库分表的名称
     *
     * @param prefix 前缀，例如：tb_sequence_xxx格式的名称中tb_sequence_
     * @return xxx的列表
     */
    public List<Long> queryTableSequence(String prefix) {
        // 查询数据库表名称
        String sql = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_name LIKE '"+prefix+"%' ";
        List<TableNamePo> tableNamelist = this.mapper.executeSelectData(sql);

        // 从表名称，取出时间部分
        List<Long> sequenceList = new ArrayList<>();
        for (TableNamePo po : tableNamelist) {
            if (po.getTableName() == null || !po.getTableName().startsWith(prefix)) {
                continue;
            }
            Long time = NumberUtils.parseLong(po.getTableName().substring((prefix).length()));
            if (time == null) {
                continue;
            }

            sequenceList.add(time);
        }

        // 排序
        Collections.sort(sequenceList, Long::compareTo);

        return sequenceList;
    }
}

