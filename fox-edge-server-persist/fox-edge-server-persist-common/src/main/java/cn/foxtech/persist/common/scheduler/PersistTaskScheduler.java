package cn.foxtech.persist.common.scheduler;

import cn.foxtech.common.domain.constant.RestFulManagerVOConstant;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.persist.common.redislist.RedisListManageRequest;
import cn.foxtech.persist.common.redislist.RedisListManageRespond;
import cn.foxtech.persist.common.redislist.RedisListRecordRequest;
import cn.foxtech.persist.common.redislist.RedisListValueRequest;
import cn.foxtech.persist.common.service.EntityManageService;
import cn.foxtech.persist.common.service.EntityUpdateService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 记录队列：从topic改为采用list方式，是为了让记录数据更可靠
 */
@Component
public class PersistTaskScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(PersistTaskScheduler.class);
    @Autowired
    EntityManageService entityManageService;
    @Autowired
    EntityUpdateService entityUpdateService;
    @Autowired
    private RedisListRecordRequest recordRequest;

    @Autowired
    private RedisListValueRequest valueRequest;

    @Autowired
    private RedisListManageRequest manageRequest;

    @Autowired
    private RedisListManageRespond manageRespond;


    @Autowired
    private RedisConsoleService console;

    @Override
    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }


        int size = 0;

        // 更新记录数据
        size += this.updateRecordRequest();
        // 更新数值数据
        size += this.updateValueRequest();
        // 更新管理数据
        size += this.updateManageRequest();

        // 如果没有数据到达，那么休眠1000毫秒
        if (size <= 0) {
            Thread.sleep(1000);
        }
    }

    private int updateManageRequest() {
        // 读取到的数据量
        int size = 0;

        do {
            // 弹出数据
            Object respondMap = this.manageRequest.pop();
            if (respondMap == null) {
                break;
            }

            size++;

            // 处理数据
            this.updateManageRespond(respondMap);

            // 为其他任务腾出点执行的机会
            if (size > 256) {
                break;
            }

        } while (true);

        return size;
    }

    /**
     * 数据可丢弃：先弹出，再处理
     *
     * @return
     */
    private int updateValueRequest() {
        // 读取到的数据量
        int size = 0;

        do {
            // 弹出数据
            Object respondMap = this.valueRequest.pop();
            if (respondMap == null) {
                break;
            }

            size++;

            // 处理数据
            this.updateDeviceRespond(respondMap);

            // 为其他任务腾出点执行的机会
            if (size > 256) {
                break;
            }

        } while (true);

        return size;
    }

    /**
     * 数据不可丢弃：先处理成功，再弹出数据
     *
     * @return
     */
    private int updateRecordRequest() {
        // 预览消息队列
        List<Object> respondVOList = this.recordRequest.range();

        // 读取到的数据量
        int size = respondVOList.size();

        // 处理并删除这个数据
        for (Object respondMap : respondVOList) {
            // 处理数据
            this.updateDeviceRespond(respondMap);

            // 删除这个对象
            this.recordRequest.pop();
        }

        return size;
    }

    private void updateDeviceRespond(Object respondMap) {
        try {
            TaskRespondVO taskRespondVO = TaskRespondVO.buildRespondVO((Map<String, Object>) respondMap);
            for (OperateRespondVO operateRespondVO : taskRespondVO.getRespondVOS()) {
                this.entityUpdateService.updateDeviceRespond(operateRespondVO, taskRespondVO.getClientName());
            }
        } catch (Exception e) {
            String message = "更新设备数据，发生异常：" + e.getMessage();
            logger.error(message);
            console.error(message);
        }
    }

    private void updateManageRespond(Object respondMap) {
        try {
            RestFulRequestVO requestVO = RestFulRequestVO.buildRequestVO((Map<String, Object>) respondMap);

            // 场景1： 删除设备数值
            if (RestFulManagerVOConstant.uri_device_value.equals(requestVO.getUri()) && "delete".equals(requestVO.getMethod())) {
                RestFulRespondVO respondVO = this.entityUpdateService.deleteValueEntity(requestVO);
                this.manageRespond.push(respondVO);
                return;
            }

        } catch (Exception e) {
            String message = "更新设备数据，发生异常：" + e.getMessage();
            logger.error(message);
            console.error(message);
        }
    }

}