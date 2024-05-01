package cn.foxtech.kernel.system.repository.controller;

import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.repository.service.RepoLocalCompService;
import cn.foxtech.kernel.system.repository.service.RepoLocalCsvFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * CSV文件列表管理：设备模板的文件列表
 */
@RestController
@RequestMapping("/kernel/manager/repository/local/file-list")
public class RepoLocalCsvFileListController {
    /**
     * 仓库服务
     */
    @Autowired
    private RepoLocalCompService compService;

    @Autowired
    private RepoLocalCsvFileService fileService;


    @PostMapping("page")
    public Map<String, Object> selectFilePage(@RequestBody Map<String, Object> body) {
        try {
            Integer id = (Integer) body.get(RepoCompVOFieldConstant.field_id);
            Integer pageNum = (Integer) body.get(RepoCompVOFieldConstant.field_page_num);
            Integer pageSize = (Integer) body.get(RepoCompVOFieldConstant.field_page_size);


            // 简单验证
            if (MethodUtils.hasEmpty(id, pageNum, pageSize)) {
                throw new ServiceException("参数不能为空: id, pageNum, pageSize");
            }

            RepoCompEntity compEntity = this.compService.getRepoCompEntity(Long.parseLong(id.toString()));
            if (compEntity == null) {
                throw new ServiceException("找不到对应的组件：" + id);
            }

            // 查询数据
            List<Map<String, Object>> entityList = this.fileService.queryFileList(compEntity);


            // 分页查询
            return AjaxResult.success(PageUtils.getPageList(entityList, pageNum, pageSize));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("download")
    public void download(@RequestBody Map<String, Object> body) {
        try {
            Integer id = (Integer) body.get(RepoCompVOFieldConstant.field_id);
            String file = (String) body.get(RepoCompVOFieldConstant.field_file);

            // 简单验证
            if (MethodUtils.hasEmpty(id, file)) {
                throw new ServiceException("参数不能为空: id, file");
            }

            // 查询路径部分的信息
            String path = this.fileService.queryTemplateFilePath(Long.parseLong(id.toString()));

            // 下载文件
            File dir = new File("");
            this.downloadTextFile(dir.getAbsolutePath() + "/" + path + "/", file);

        } catch (Exception e) {
            return;
        }
    }

    public void downloadTextFile(String path, String fileName) throws IOException {
        HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        File download = new File(path + fileName);
        if (download.exists()) {
            resp.setContentType("application/x-msdownload");
            resp.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), StandardCharsets.ISO_8859_1));
            InputStream inputStream = new FileInputStream(download);
            ServletOutputStream ouputStream = resp.getOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = inputStream.read(b)) != -1) {
                ouputStream.write(b, 0, n);
            }
            ouputStream.close();
            inputStream.close();
        }
    }

    @PostMapping("delete")
    public AjaxResult deleteFileTemplate(@RequestBody Map<String, Object> body) {
        try {
            Integer id = (Integer) body.get(RepoCompVOFieldConstant.field_id);
            String file = (String) body.get(RepoCompVOFieldConstant.field_file);

            // 简单验证
            if (MethodUtils.hasEmpty(id, file)) {
                throw new ServiceException("参数不能为空: id, file");
            }

            // 删除文件模板
            this.fileService.deleteFileTemplate(Long.parseLong(id.toString()), file);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
