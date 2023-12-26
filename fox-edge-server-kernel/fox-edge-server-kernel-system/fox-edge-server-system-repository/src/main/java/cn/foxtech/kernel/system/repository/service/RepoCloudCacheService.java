package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RepoCloudCacheService {
    private final String absolutePath = (new File("")).getAbsolutePath();

    /**
     * 查询本地模块列表列表
     *
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> queryLocalListFile(String modelType) throws IOException {
        String listFileName = "";
        if (RepoCompConstant.repository_type_decoder.equals(modelType)) {
            listFileName = "decoderList.jsn";
        } else if (RepoCompConstant.repository_type_webpack.equals(modelType)) {
            listFileName = "webpackList.jsn";
        } else if (RepoCompConstant.repository_type_service.equals(modelType)) {
            listFileName = "serviceList.jsn";
        } else if (RepoCompConstant.repository_type_template.equals(modelType)) {
            listFileName = "templateList.jsn";
        } else {
            listFileName = "serviceList.jsn";
        }

        // 下载list文件
        String localPath = this.absolutePath + "/repository/" + modelType;

        return this.queryLocalListFile(localPath, listFileName);
    }


    /**
     * 从本地列表文件中，取得模块列表信息
     *
     * @param localPath
     * @param listFileName
     * @return
     * @throws IOException
     */
    private List<Map<String, Object>> queryLocalListFile(String localPath, String listFileName) throws IOException {
        String pathName = localPath + "/" + listFileName;

        // 检查文件是否已经下载
        File exist = new File(pathName);
        if (!exist.exists()) {
            return new ArrayList<>();
        }

        // 解析json格式的list文件
        String jsn = FileTextUtils.readTextFile(localPath + "/" + listFileName);
        Map<String, Object> param = JsonUtils.buildObject(jsn, Map.class);

        return (List<Map<String, Object>>) param.get("list");
    }
}
