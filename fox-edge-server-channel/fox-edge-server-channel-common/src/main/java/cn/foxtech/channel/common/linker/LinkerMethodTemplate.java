package cn.foxtech.channel.common.linker;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@Component
public class LinkerMethodTemplate {
    private Map<String, LinkerMethodEntity> map = new ConcurrentHashMap<>();
}
