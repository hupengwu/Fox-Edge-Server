package cn.foxtech.period.service.mapper.periodrecord;


import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.entity.constant.PeriodRecordVOFieldConstant;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import cn.foxtech.period.service.entity.PeriodRecordEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PeriodRecordEntityService extends BaseEntityService {
    @Autowired(required = false)
    private PeriodRecordEntityMapper mapper;


    /**
     * 子类将自己的mapper绑定到父类上
     */
    public void bindMapper() {
        super.mapper = this.mapper;
    }

    public List<PeriodRecordEntity> selectPoListByPage(Map<String, Object> param) {
        QueryWrapper queryWrapper = new QueryWrapper<>();

        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            BaseVOFieldConstant.makeQueryWrapper(queryWrapper, key, value);

            // 各字段条件
            if (key.equals(PeriodRecordVOFieldConstant.field_object_name)) {
                queryWrapper.eq("object_name", value);
            }
            if (key.equals(PeriodRecordVOFieldConstant.field_device_id)) {
                queryWrapper.eq("device_id", value);
            }
            if (key.equals(PeriodRecordVOFieldConstant.field_record_batch)) {
                queryWrapper.eq("record_batch", value);
            }
            if (key.equals(PeriodRecordVOFieldConstant.field_task_id)) {
                queryWrapper.eq("task_id", value);
            }
            if (key.equals(PeriodRecordVOFieldConstant.field_order_by_desc)) {
                queryWrapper.orderByDesc(value);
            }
            if (key.equals(PeriodRecordVOFieldConstant.field_order_by_asc)) {
                queryWrapper.orderByAsc(value);
            }

        }

        return mapper.selectList(queryWrapper);
    }

    /**
     * 删除旧数据，只保留少数的最新的部分数据
     *
     * @param retainCount 需要保留的数据数量
     */
    public void delete(Long taskId, int retainCount) {
        Integer sumCount = mapper.executeSelectCount("SELECT COUNT(1) FROM  tb_period_record WHERE task_id = " + taskId);
        if (sumCount <= retainCount) {
            return;
        }

        // 删除旧记录
        String sql = String.format("DELETE FROM  tb_period_record t WHERE task_name = '%s' order BY t.id LIMIT  %d", sumCount - retainCount);
        mapper.executeDelete(sql);
    }
}
