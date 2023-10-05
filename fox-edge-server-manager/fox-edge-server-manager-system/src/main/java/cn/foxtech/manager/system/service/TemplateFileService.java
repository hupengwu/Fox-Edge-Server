package cn.foxtech.manager.system.service;

import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.utils.file.FileAttributesUtils;
import cn.foxtech.manager.system.constants.RepoComponentConstant;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TemplateFileService {
    private static final Logger logger = Logger.getLogger(TemplateFileService.class);

    /**
     * 查询模板列表
     *
     * @return
     */
    public List<Map<String, Object>> queryTemplateList() throws IOException {
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

            for (String modelVer : modelDir.list()) {
                File modelDirDir = new File(modelDir, modelVer);
                if (!modelDirDir.isDirectory()) {
                    continue;
                }

                for (String version : modelDirDir.list()) {
                    File versionDir = new File(modelDirDir, version);
                    if (!versionDir.isDirectory()) {
                        continue;
                    }

                    for (File childFile : versionDir.listFiles()) {
                        Map<String, Object> data = this.getFileNameInfo(modelName, modelVer, version, childFile);
                        result.add(data);
                    }
                }
            }
        }

        return result;
    }

    private Map<String, Object> getFileNameInfo(String modelName, String modelVer, String version, File childFile) {
        Map<String, Object> data = new HashMap<>();

        data.put(RepoComponentConstant.filed_model_name, modelName);
        data.put(RepoComponentConstant.filed_model_version, modelVer);
        data.put(RepoComponentConstant.filed_version, version);
        data.put(RepoComponentConstant.filed_component, RepoComponentConstant.repository_type_template);
        data.put("fileName", childFile.getName());

        // 乱码文件的异常处理
        try {
            BasicFileAttributes attributes = FileAttributesUtils.getAttributes(childFile);
            data.put(BaseVOFieldConstant.field_create_time, attributes.creationTime().toMillis());
            data.put(BaseVOFieldConstant.field_update_time, attributes.lastModifiedTime().toMillis());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return data;
    }
}
