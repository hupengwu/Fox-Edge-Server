package cn.foxtech.common.entity.manager;

import cn.foxtech.common.utils.json.JsonUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地配置缓存
 * 有的应用并不需要在运行期，读取用户随时修改的配置数据，只需要启动阶段读取配置初始化参数，那么就可以使用该组件方便的使用用户输入的全局配置
 *
 * 注意，它必须依赖于EntityServiceManager的从redis加载的管理数据数据，所以必须在EntityServiceManager初始化之后执行
 * this.localConfigService.initialize()
 *
 */
@Component
public class LocalConfigService {
    @Autowired
    private ConfigManageService configManageService;

    @Getter
    private Map<String, Object> configs = new HashMap<>();

    public void initialize() {
        // 从redis缓存之中，从Redis中读取配置信息，该信息由ConfigManageService组件，自动冲serverConfig.json、redis缓存、管理服务通告，三者中自动判定出数值
        this.configManageService.initialize("serverConfig", "serverConfig.json");

        // 读取配置数据
        Map<String, Object> configs = this.configManageService.getConfigParam("serverConfig");

        // 本应用反正不想动态根据全局变化，调整，将上面艰难获得的信息，缓存起来，反复使用
        this.configs = JsonUtils.clone(configs);
    }

    public void initialize(String configName, String classpathFile) {
        // 从redis缓存之中，从Redis中读取配置信息，该信息由ConfigManageService组件，自动冲serverConfig.json、redis缓存、管理服务通告，三者中自动判定出数值
        this.configManageService.initialize(configName, classpathFile);

        // 读取配置数据
        Map<String, Object> configs = this.configManageService.getConfigParam("serverConfig");

        // 本应用反正不想动态根据全局变化，调整，将上面艰难获得的信息，缓存起来，反复使用
        this.configs = JsonUtils.clone(configs);
    }
}
