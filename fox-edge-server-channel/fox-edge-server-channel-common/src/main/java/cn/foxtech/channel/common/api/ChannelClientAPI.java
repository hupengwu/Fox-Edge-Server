package cn.foxtech.channel.common.api;

import cn.foxtech.channel.common.linker.LinkerEntity;
import cn.foxtech.channel.common.linker.LinkerManager;
import cn.foxtech.channel.common.linker.LinkerMethodEntity;
import cn.foxtech.channel.common.linker.LinkerMethodTemplate;
import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Channel服务的API接口
 * 每一种具体的Channel模块会去实现这些方法，那么Channel框架在控制这些Channel模块的时候，会展现出相应的特性
 */
@Component
public class ChannelClientAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelClientAPI.class);

    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private ChannelServerAPI channelServerAPI;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private LinkerMethodTemplate methodTemplate;

    /**
     * 执行主从半双工操作：上位机向设备问询，并等待设备的回答，直到设备响应或者超时
     *
     * @param requestVO 请求报文
     * @return 返回的json报文
     * @throws ServiceException 异常信息
     */
    public ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        // 检查：是否开启链路模式的拦截操作
        this.interceptIsNotLinked(requestVO);

        ChannelRespondVO respondVO = this.channelServerAPI.execute(requestVO);

        // 对设备返回的数据，进行状态拦截检查
        this.interceptDetectLink(respondVO);
        return respondVO;
    }

    /**
     * 执行发布操作：上位机向设备单向发送报文，不需要等待设备的返回
     *
     * @param requestVO 发布报文
     * @throws ServiceException 异常信息
     */
    public void publish(ChannelRequestVO requestVO) throws ServiceException {
        // 检查：路模式的拦截操作
        this.interceptIsNotLinked(requestVO);

        this.channelServerAPI.publish(requestVO);
    }

    /**
     * 非连接状态的拦截操作：如果用户配置了面向连接，那么如果通道没有建立设备连接，则会拦截下其他操作，
     * 等待连接线程自动跟设备建立连接，然后才会放行后续操作
     *
     * @param requestVO
     */
    private void interceptIsNotLinked(ChannelRequestVO requestVO) {
        if (!this.channelProperties.getLinkerMode()) {
            return;
        }

        // 获得ChannelEntity信息
        ChannelEntity channelEntity = this.entityManageService.getChannelEntity(requestVO.getName(), this.channelProperties.getChannelType());
        if (channelEntity == null) {
            return;
        }

        // 获得解码器信息
        Object linkEncoder = channelEntity.getChannelParam().get(LinkerMethodEntity.PARAM_KEY);
        if (linkEncoder == null) {
            return;
        }

        // 获得编码器
        LinkerMethodEntity methodEntity = this.methodTemplate.getMap().get(linkEncoder);
        if (methodEntity == null) {
            return;
        }

        // 检查：链路状态
        LinkerEntity linkerEntity = LinkerManager.queryEntity(requestVO.getName());
        if (linkerEntity == null) {
            throw new ServiceException("指定的linkerEntity不存在：" + requestVO.getName());
        }

        if (!linkerEntity.isLinked()) {
            throw new ServiceException("指定的从站尚未建立链路：" + requestVO.getName());
        }
    }

    private void interceptDetectLink(ChannelRespondVO respondVO) {
        if (!this.channelProperties.getLinkerMode()) {
            return;
        }

        // 检查：设备是否返回数据
        if (respondVO.getCode() != 200) {
            return;
        }

        // 获得ChannelEntity信息
        ChannelEntity channelEntity = this.entityManageService.getChannelEntity(respondVO.getName(), this.channelProperties.getChannelType());
        if (channelEntity == null) {
            return;
        }

        // 获得解码器信息
        Object linkEncoder = channelEntity.getChannelParam().get(LinkerMethodEntity.PARAM_KEY);
        if (linkEncoder == null) {
            return;
        }

        // 获得编码器
        LinkerMethodEntity methodEntity = this.methodTemplate.getMap().get(linkEncoder);
        if (methodEntity == null) {
            return;
        }

        // 获得解码器函数
        Method decodeDetectLinkerRespond = methodEntity.getDecodeInterceptLinkerRespond();
        if (decodeDetectLinkerRespond == null) {
            return;
        }

        try {
            if ((boolean) decodeDetectLinkerRespond.invoke(this, channelEntity.getChannelParam(), respondVO.getRecv())) {
                LinkerManager.updateEntity4ActiveLinker(respondVO.getName(), true);
            } else {
                LOGGER.info("拦截到设备断开连接状态，连接断开!");
                LinkerManager.updateEntity4ActiveLinker(respondVO.getName(), false);
            }
        } catch (InvocationTargetException ie) {
            LOGGER.info("拦截设备返回时，报文解码失败:" + channelEntity.getChannelName() + ",原因:" + ie.getTargetException());
        } catch (Exception e) {
            throw new ServiceException("拦截设备返回时，报文解码失败:" + channelEntity.getChannelName() + ",原因:" + e.getMessage());
        }
    }

    /**
     * 设备的主动上报消息：设备向上位机
     *
     * @return 上报消息
     * @throws ServiceException 异常信息
     */
    public List<ChannelRespondVO> receive() throws ServiceException {
        return this.channelServerAPI.report();
    }

    /**
     * 打开通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     * @throws Exception 异常
     */
    public void openChannel(String channelName, Map<String, Object> channelParam) throws Exception {
        this.channelServerAPI.openChannel(channelName, channelParam);
    }

    /**
     * 关闭通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    public void closeChannel(String channelName, Map<String, Object> channelParam) {
        this.channelServerAPI.closeChannel(channelName, channelParam);
    }

    /**
     * 对通道进行管理操作
     *
     * @param requestVO 操作请求
     * @return 响应
     * @throws ServiceException 异常状况
     */
    public ChannelRespondVO manageChannel(ChannelRequestVO requestVO) throws ServiceException {
        return this.channelServerAPI.manageChannel(requestVO);
    }
}
