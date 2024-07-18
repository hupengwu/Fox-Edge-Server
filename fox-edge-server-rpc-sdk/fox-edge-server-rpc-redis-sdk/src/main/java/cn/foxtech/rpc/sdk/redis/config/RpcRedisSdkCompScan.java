package cn.foxtech.rpc.sdk.redis.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"cn.foxtech.common.rpc.redis", "cn.foxtech.utils.common.utils.redis"})
public class RpcRedisSdkCompScan {
}


