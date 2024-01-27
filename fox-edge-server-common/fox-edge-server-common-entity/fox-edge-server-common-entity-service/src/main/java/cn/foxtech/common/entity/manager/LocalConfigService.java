package cn.foxtech.common.entity.manager;

import cn.foxtech.common.utils.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地配置缓存：该配置只是一个启动阶段产生的一次配置快照，不会去动态感知 Manager 那边来的用户配置
 * 用户在 Manager 那边修改配置后，必须重启进程，才能获得这个配置信息
 * 如果你想要感知运行期的用户变化，请直接使用 InitialConfigService 组件，并用 getConfigParam 方法获得配置信息
 *
 * 有的应用并不需要在运行期，读取用户随时修改的配置数据，只需要启动阶段读取配置初始化参数，那么就可以使用该组件方便的使用用户输入的全局配置
 * 注意，它必须依赖于EntityServiceManager的从redis加载的管理数据数据，所以必须在EntityServiceManager初始化之后执行
 * this.localConfigService.initialize()
 */
@Component
public class LocalConfigService {
    private final Map<String, Object> configs = new HashMap<>();
    @Autowired
    private InitialConfigService configService;

    /**
     * 按默认方式进行初始化：从“serverConfig.json”装载初始化数值，并保存到“serverConfig”的Key中
     */
    public void initialize() {
        this.initialize("serverConfig", "serverConfig.json");
    }

    /**
     * 按默认方式进行获得配置数据：获得serverConfig为key的配置数值
     *
     * @return 配置数值
     */
    public Map<String, Object> getConfig() {
        return this.getConfig("serverConfig");
    }

    public void initialize(String configName, String classpathFile) {
        // 从redis缓存之中，从Redis中读取配置信息，该信息由ConfigManageService组件，自动冲serverConfig.json、redis缓存、管理服务通告，三者中自动判定出数值
        this.configService.initialize(configName, classpathFile);

        // 读取配置数据
        Map<String, Object> configs = this.configService.getConfigParam(configName);

        // 本应用反正不想动态根据全局变化，调整，将上面艰难获得的信息，缓存起来，反复使用
        this.configs.computeIfAbsent(configName, k -> JsonUtils.clone(configs));
    }

    public Map<String, Object> getConfig(String configName) {
        return (Map<String, Object>) this.configs.getOrDefault(configName, new HashMap<>());
    }
}
