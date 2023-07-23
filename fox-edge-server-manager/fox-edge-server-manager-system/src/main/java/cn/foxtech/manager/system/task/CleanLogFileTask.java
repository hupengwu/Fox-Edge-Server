package cn.foxtech.manager.system.task;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.file.FileAttributesUtils;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 初始化装载任务
 */
@Component
public class CleanLogFileTask extends PeriodTask {
    @Autowired
    private RedisConsoleService logger;

    @Override
    public int getTaskType() {
        return PeriodTaskType.task_type_share;
    }

    /**
     * 获得调度周期
     *
     * @return 调度周期，单位秒
     */
    public int getSchedulePeriod() {
        return 3600;
    }

    /**
     * 待周期性执行的操作
     */
    @Override
    public void execute() {
        try {
            File file = new File("");
            File dir = new File(file.getAbsolutePath() + "/logs");

            // 最大保留7天的日志
            this.removeLogFiles(dir, 7 * 24);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void removeLogFiles(File dir, int hour) throws IOException {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            // 循环，添加文件名或回调自身
            File file = new File(dir, files[i]);
            if (!file.isFile()) {
                continue;
            }

            // 检查：是否为.gz文件，这文件是日志压缩文件
            if (!file.getName().endsWith(".gz")) {
                continue;
            }

            // 检查：是否为过期的文件
            BasicFileAttributes attributes = FileAttributesUtils.getAttributes(file);
            Long fileTime = attributes.creationTime().toMillis();
            Long timeOut = System.currentTimeMillis() - fileTime;
            if (timeOut < hour * 3600 * 1000) {
                continue;
            }


            file.delete();
        }
    }
}
