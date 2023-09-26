package cn.foxtech.channel.common.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Component
public class ChannelProperties {
    @Autowired
    private AbstractEnvironment environment;

    /**
     * 必填参数
     */
    @Value("${spring.fox-service.model.name}")
    private String channelType;

    /**
     * 是否打印收/发日志
     * 该参数只是临时变量，它的填写由各个channel服务在Initialize阶段，自己填写，自己在后续阶段使用。
     */
    @Setter
    private boolean logger = false;

    /**
     * 可选参数
     */
    private String initMode;

    /**
     * 可选参数
     */
    private Boolean linkerMode;

    private Map<String, Object> linkEncoderJars;


    public void initialize() {
        this.initMode = this.environment.getProperty("spring.channel.init-mode", String.class, "redis");
        this.linkerMode = this.environment.getProperty("spring.channel.link-mode", Boolean.class, false);

        this.linkEncoderJars = this.getPropertyMap("spring.channel.link-encoder-jar");
    }

    private Map<String, Object> getPropertyMap(String head) {
        // 将全体属性转换为map
        Map<String, Object> rtn = new HashMap<>();
        for (PropertySource<?> propertySource : this.environment.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
                    rtn.put(key, propertySource.getProperty(key));
                }
            }
        }

        // 挑选出指定前缀的key
        Map<String, Object> result = new HashMap<>();
        for (String key : rtn.keySet()) {
            if (key.startsWith(head + ".")) {
                result.put(key, rtn.get(key));
            }
        }


        return result;

    }
}
