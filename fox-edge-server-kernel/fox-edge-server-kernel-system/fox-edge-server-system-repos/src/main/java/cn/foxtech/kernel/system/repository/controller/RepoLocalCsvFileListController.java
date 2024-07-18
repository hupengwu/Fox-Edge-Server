package cn.foxtech.kernel.system.repository.controller;

import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.http.ExportUtil;
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

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * CSV文件列表管理：设备模板的文件列表
 */
@RestController
@RequestMapping("/repository/local/file-list")
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
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            ExportUtil.exportTextFile(response, dir.getAbsolutePath() + "/" + path, file);
        } catch (Exception e) {
            return;
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
