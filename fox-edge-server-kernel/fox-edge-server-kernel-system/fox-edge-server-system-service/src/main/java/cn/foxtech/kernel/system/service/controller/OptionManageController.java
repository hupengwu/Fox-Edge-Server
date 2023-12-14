package cn.foxtech.kernel.system.service.controller;

import cn.foxtech.common.entity.manager.EntityOptionManager;
import cn.foxtech.common.entity.service.foxsql.FoxSqlService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/option")
public class OptionManageController {
    @Autowired
    private FoxSqlService foxSqlService;

    @Autowired
    private EntityOptionManager entityOptionManager;


    /**
     * 查询一级列表
     * <p>
     * 场景1：查询整个entityType的field1，并聚合成唯一性
     * entityType, field1
     * 例如：{"entityType":"DeviceEntity","field1":"channelType"}
     * <p>
     * 场景2：查询整个entityType的 field2，并聚合成唯一性，过滤条件为field1=value1
     * entityType, field1, value1, field2
     * 例如：{"entityType":"DeviceEntity","field1":"deviceType","value1":"ModBus Device","field2":"deviceName"}
     *
     * @param params 用户参数
     * @return 查询结果
     */
    @PostMapping("data")
    public AjaxResult selectDataList(@RequestBody Map<String, Object> params) {
        try {
            String mode = (String) params.get("mode");

            if ("Option1".equals(mode)) {
                return AjaxResult.success(this.selectOption1(params));
            }
            if ("Option2".equals(mode)) {
                return AjaxResult.success(this.selectOption2(params));
            }
            if ("Option3".equals(mode)) {
                return AjaxResult.success(this.selectOption3(params));
            }

            // 不正确的参数
            return AjaxResult.error("不支持的操作模式!");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取选项列表：查询entityType表的field字段，并对该字段做一个distinct操作，把结果作为选项返回。
     * 例如：listOptionList("ChannelEntity", "channelType")
     * 意思是查询ChannelEntity表，将channelType字段作为选项
     *
     * @param params 参数
     * @return 结果
     */
    private List<Object> selectOption1(Map<String, Object> params) {
        String entityType = (String) params.get("entityType");
        String camelField = (String) params.get("field");

        if (MethodUtils.hasEmpty(entityType, camelField)) {
            throw new ServiceException("缺少参数：entityType, camelField");
        }


        // 其次查询单参数
        String underField = StringUtils.camelToUnderline(camelField);

        // 安全性检查：是否对该表和字段进行操作
        if (!this.entityOptionManager.isPermit(entityType, underField)) {
            throw new ServiceException("不允许该操作！");
        }

        String tableName = this.entityOptionManager.getTableName(entityType);
        if (MethodUtils.hasEmpty(tableName)) {
            throw new ServiceException("表名称为空！");
        }

        // 查询数据
        List<Map<String, Object>> mapList = this.foxSqlService.selectOptionList(tableName, underField, true);

        return this.getOptionList(mapList, camelField, camelField);
    }

    /**
     * 获取选项列表：查询entityType表的field2字段，按field1字段=value1的条件进行过滤
     * 例如：listOption2List("OperateEntity", "manufacturer", '武汉中科图灵', 'deviceType')
     * 意思是查询OperateEntity表，按将manufacturer=武汉中科图灵的条件进行过滤，并将查询结果中的deviceType字段作为选项返回
     *
     * @param params 参数
     * @return 结果
     */
    private List<Object> selectOption2(Map<String, Object> params) {
        String entityType = (String) params.get("entityType");
        String camelField = (String) params.get("field");
        String value = (String) params.get("value");
        String camelField2 = (String) params.get("field2");

        if (MethodUtils.hasEmpty(entityType, camelField, value, camelField2)) {
            throw new ServiceException("缺少参数：entityType, camelField1, value1, camelField2 ");
        }
        // 优先查询双参数

        String underField1 = StringUtils.camelToUnderline(camelField);
        String underField2 = StringUtils.camelToUnderline(camelField2);

        // 安全性检查：是否对该表和字段进行操作
        if (!this.entityOptionManager.isPermit(entityType, underField1, underField2)) {
            throw new ServiceException("不允许该操作！");
        }

        String tableName = this.entityOptionManager.getTableName(entityType);
        if (MethodUtils.hasEmpty(tableName)) {
            throw new ServiceException("表名称为空！");
        }

        Object fieldValue1 = value;
        if (this.entityOptionManager.isNumberField(entityType, underField1)) {
            fieldValue1 = Long.parseLong(value);
        }


        List<Map<String, Object>> mapList = this.foxSqlService.selectOptionList(tableName, underField1, underField2, fieldValue1, true);

        // 用field1/field2作为distinct的条件，value1作为筛选条件
        return this.getOptionList(mapList, camelField2, camelField2);
    }

    /**
     * 获取选项列表：对entityType的field1，field2进行distinct操作，过滤条件为field1=value1，并返回两者中的一个字段（value来指明）字段的内容为选项
     *
     * @param params 参数
     * @return 结果
     */
    private List<Object> selectOption3(Map<String, Object> params) {
        String entityType = (String) params.get("entityType");
        String camelField1 = (String) params.get("field1");
        String value1 = (String) params.get("value1");
        String camelField2 = (String) params.get("field2");
        String value = (String) params.get("value");
        String label = (String) params.get("label");

        // 优先查询双参数
        if (MethodUtils.hasEmpty(entityType, camelField1, value1, camelField2)) {
            throw new ServiceException("缺少参数：entityType, camelField1, value1, camelField2 ");
        }

        String underField1 = StringUtils.camelToUnderline(camelField1);
        String underField2 = StringUtils.camelToUnderline(camelField2);
        String underValue = StringUtils.camelToUnderline(value);

        // 安全性检查：是否对该表和字段进行操作
        if (!this.entityOptionManager.isPermit(entityType, underField1, underField2)) {
            throw new ServiceException("不允许该操作！");
        }

        String tableName = this.entityOptionManager.getTableName(entityType);
        if (MethodUtils.hasEmpty(tableName)) {
            throw new ServiceException("表名称为空！");
        }

        Object fieldValue1 = value1;
        if (this.entityOptionManager.isNumberField(entityType, underField1)) {
            fieldValue1 = Long.parseLong(value1);
        }


        // 检查：第三个字段是否允许
        if (!this.entityOptionManager.isPermit(entityType, underValue)) {
            throw new ServiceException("不允许该操作！");
        }

        // 用field1/field2/value作为distinct的条件，value1作为筛选条件
        List<Map<String, Object>> mapList = this.foxSqlService.selectOptionList(tableName, underField1, underField2, underValue, fieldValue1, true);
        return this.getOptionList(mapList, value, label);
    }

    @PostMapping("tree")
    public AjaxResult selectTreeList(@RequestBody Map<String, Object> params) {
        try {
            String entityType = (String) params.get("entityType");
            String camelField1 = (String) params.get("field1");
            String camelField2 = (String) params.get("field2");

            // 优先查询双参数
            if (!MethodUtils.hasEmpty(entityType, camelField1, camelField2)) {
                String underField1 = StringUtils.camelToUnderline(camelField1);
                String underField2 = StringUtils.camelToUnderline(camelField2);

                // 安全性检查：是否对该表和字段进行操作
                if (!this.entityOptionManager.isPermit(entityType, underField1, underField2)) {
                    return AjaxResult.error("不允许该操作！");
                }

                String tableName = this.entityOptionManager.getTableName(entityType);
                if (MethodUtils.hasEmpty(tableName)) {
                    return AjaxResult.error("表名称为空！");
                }


                // 用field1/field1作为distinct的条件
                List<Map<String, Object>> mapList = this.foxSqlService.selectOptionList(tableName, underField1, underField2, true);
                return AjaxResult.success(this.getOptionTree(mapList, camelField1, camelField2));
            }

            // 不正确的参数
            return AjaxResult.error("操作失败!");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获得选项
     *
     * @param mapList    mapList
     * @param valueField 数值字段
     * @param labelField 标签字段
     * @return 转换结果
     */
    private List<Object> getOptionList(List<Map<String, Object>> mapList, String valueField, String labelField) {
        List<Object> result = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            Object dataValue = map.get(valueField);
            Object dataLabel = map.get(labelField);

            Map<String, Object> option = new HashMap<>();
            option.put("value", dataValue);
            option.put("label", dataLabel);

            result.add(option);
        }

        return result;
    }


    /**
     * 获得二级选项
     *
     * @param mapList 数据列表
     * @param field1  字段1
     * @param field2  字段2
     * @return 返回结果
     */
    private List<Object> getOptionTree(List<Map<String, Object>> mapList, String field1, String field2) {
        // 先进行分组
        Map<Object, List<Object>> groupMap = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            Object data1 = map.get(field1);
            Object data2 = map.get(field2);

            List<Object> list = groupMap.get(data1);
            if (list == null) {
                list = new ArrayList<>();
                groupMap.put(data1, list);
            }
            list.add(data2);
        }

        List<Object> result = new ArrayList<>();
        for (Object key : groupMap.keySet()) {
            if (key == null) {
                continue;
            }

            // 一级数据
            Map<String, Object> option1 = new HashMap<>();
            option1.put("value", key.toString());
            option1.put("label", key.toString());

            // 二级数据
            List<Object> list = groupMap.get(key);
            if (list == null) {
                continue;
            }

            option1.put("children", new ArrayList<>());
            List option2List = (List) option1.get("children");

            for (Object object2 : list) {
                Map<String, Object> option2 = new HashMap<>();
                option2.put("value", object2.toString());
                option2.put("label", object2.toString());
                option2List.add(option2);
            }

            result.add(option1);
        }

        return result;
    }

}
