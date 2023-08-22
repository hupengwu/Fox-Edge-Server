package cn.foxtech.manager.system.utils;

import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.utils.file.FileAttributesUtils;
import cn.foxtech.manager.system.constants.RepositoryConstant;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateUtils {
    private static final Logger logger = Logger.getLogger(TemplateUtils.class);

    /**
     * 查询模板列表
     *
     * @return
     */
    public static List<Map<String, Object>> queryTemplateList(boolean isModelList) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();

        File file = new File("");

        File templateDir = new File(file.getAbsolutePath() + "/template/");
        if (!templateDir.exists() || !templateDir.isDirectory()) {
            return result;
        }

        for (String modelName : templateDir.list()) {
            File modelDir = new File(templateDir, modelName);
            if (!modelDir.isDirectory()) {
                continue;
            }

            for (String version : modelDir.list()) {
                File versionDir = new File(modelDir, version);
                if (!versionDir.isDirectory()) {
                    continue;
                }

                String[] fileNames = versionDir.list();
                if (isModelList) {
                    Map<String, Object> data = new HashMap<>();
                    data.put(RepositoryConstant.filed_model_name, modelName);
                    data.put(RepositoryConstant.filed_version, version);
                    data.put(RepositoryConstant.filed_component, RepositoryConstant.repository_type_template);
                    data.put("fileNames", fileNames);

                    result.add(data);
                } else {

                    for (File childFile : versionDir.listFiles()) {
                        Map<String, Object> data = new HashMap<>();
                        result.add(data);

                        data.put(RepositoryConstant.filed_model_name, modelName);
                        data.put(RepositoryConstant.filed_version, version);
                        data.put(RepositoryConstant.filed_component, RepositoryConstant.repository_type_template);
                        data.put("fileName", childFile.getName());

                        // 乱码文件的异常处理
                        try {
                            BasicFileAttributes attributes = FileAttributesUtils.getAttributes(childFile);
                            data.put(BaseVOFieldConstant.field_create_time, attributes.creationTime().toMillis());
                            data.put(BaseVOFieldConstant.field_update_time, attributes.lastModifiedTime().toMillis());
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }

                }
            }
        }

        return result;
    }
}
