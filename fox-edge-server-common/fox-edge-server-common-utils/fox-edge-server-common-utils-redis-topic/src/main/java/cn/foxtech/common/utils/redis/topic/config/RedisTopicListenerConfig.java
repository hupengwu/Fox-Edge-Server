package cn.foxtech.common.utils.redis.topic.config;

import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 两个道订阅和单通道发送的Topic：这种topic适合device这种只有既要接收请求，又要发送请求的场景
 */
@Component
public class RedisTopicListenerConfig {
    @Autowired
    private RedisTopicSubscriber redisTopicSubscriber;

    /**
     * 用于spring session，防止每一个消息，就创建一个线程。当
     * 消息风暴来临的时候，会导致创建出无数的线程，导致内存不足，而永远脱连的严重问题
     *
     * 参考文章：https://www.cnblogs.com/aoeiuv/p/9565617.html
     *
     * 背景：
     * 如果不指定线程池，那么redis的这个组件会指定一个缺省的SimpleAsyncTaskExecutor线程池
     * 该缺省的线程池，会采用每一个消息创建一个线程进行处理的方式，来接收并行处理数据
     * 如果大量消息到达的时候，处理不及时，就会出现线程堆积的严重问题。
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor springSessionRedisTaskExecutor(){
        ThreadPoolTaskExecutor springSessionRedisTaskExecutor = new ThreadPoolTaskExecutor();
        springSessionRedisTaskExecutor.setCorePoolSize(4);
        springSessionRedisTaskExecutor.setMaxPoolSize(8);
        springSessionRedisTaskExecutor.setKeepAliveSeconds(10);
        springSessionRedisTaskExecutor.setQueueCapacity(1000);
        springSessionRedisTaskExecutor.setThreadNamePrefix("Spring session redis executor thread: ");
        return springSessionRedisTaskExecutor;
    }

    /**
     * redis消息监听器容器
     * 可以添加多个监听不同话题的redis监听器，只需要把消息监听器和相应的消息订阅处理器绑定，该消息监听器
     * 通过反射技术调用消息订阅处理器的相关方法进行一些业务处理
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    RedisMessageListenerContainer containerV2(RedisConnectionFactory connectionFactory,
                                              ThreadPoolTaskExecutor redisTaskExecutor,
                                              MessageListenerAdapter listenerAdapter1st,
                                              MessageListenerAdapter listenerAdapter2nd,
                                              MessageListenerAdapter listenerAdapter3rd,
                                              MessageListenerAdapter listenerAdapter4th,
                                              MessageListenerAdapter listenerAdapter5th
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setTaskExecutor(redisTaskExecutor);

        if (!this.redisTopicSubscriber.topic1st().isEmpty()) {
            container.addMessageListener(listenerAdapter1st, new PatternTopic(this.redisTopicSubscriber.topic1st()));
        }
        if (!this.redisTopicSubscriber.topic2nd().isEmpty()) {
            container.addMessageListener(listenerAdapter2nd, new PatternTopic(this.redisTopicSubscriber.topic2nd()));
        }
        if (!this.redisTopicSubscriber.topic3rd().isEmpty()) {
            container.addMessageListener(listenerAdapter3rd, new PatternTopic(this.redisTopicSubscriber.topic3rd()));
        }
        if (!this.redisTopicSubscriber.topic4th().isEmpty()) {
            container.addMessageListener(listenerAdapter4th, new PatternTopic(this.redisTopicSubscriber.topic4th()));
        }
        if (!this.redisTopicSubscriber.topic5th().isEmpty()) {
            container.addMessageListener(listenerAdapter5th, new PatternTopic(this.redisTopicSubscriber.topic5th()));
        }

        return container;
    }


    /**
     * 消息监听器适配器，绑定消息处理器，利用反射技术调用消息处理器的业务方法
     *
     * @return
     */
    @Bean
    MessageListenerAdapter listenerAdapter1st() {
        return new MessageListenerAdapter(this.redisTopicSubscriber, "receiveTopic1st");
    }

    @Bean
    MessageListenerAdapter listenerAdapter2nd() {
        return new MessageListenerAdapter(this.redisTopicSubscriber, "receiveTopic2nd");
    }

    @Bean
    MessageListenerAdapter listenerAdapter3rd() {
        return new MessageListenerAdapter(this.redisTopicSubscriber, "receiveTopic3rd");
    }

    @Bean
    MessageListenerAdapter listenerAdapter4th() {
        return new MessageListenerAdapter(this.redisTopicSubscriber, "receiveTopic4th");
    }

    @Bean
    MessageListenerAdapter listenerAdapter5th() {
        return new MessageListenerAdapter(this.redisTopicSubscriber, "receiveTopic5th");
    }
}
