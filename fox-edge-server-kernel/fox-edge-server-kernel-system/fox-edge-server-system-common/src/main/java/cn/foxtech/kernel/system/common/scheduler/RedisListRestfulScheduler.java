package cn.foxtech.kernel.system.common.scheduler;


import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.kernel.system.common.redislist.RedisListRestfulHandler;
import cn.foxtech.kernel.system.common.redislist.RedisListRestfulMessage;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisListRestfulScheduler extends PeriodTaskService {
    @Autowired
    private RedisListRestfulMessage restfulMessage;


    @Setter
    private RedisListRestfulHandler handler;

    @Override
    public void execute(long threadId) throws Exception {
        // 取出一个对象
        Object message = this.restfulMessage.rangeOne();
        if (message != null) {
            // 处理数据
            this.handler.onMessage(message);

            // 删除这个对象
            this.restfulMessage.pop();
        } else {
            // 如果没有数据到达，那么休眠1000毫秒
            Thread.sleep(1000);
        }
    }
}