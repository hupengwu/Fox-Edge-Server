package cn.foxtech.device.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"cn.foxtech.device.script.engine"})
public class ScriptEngineConfig {
}
