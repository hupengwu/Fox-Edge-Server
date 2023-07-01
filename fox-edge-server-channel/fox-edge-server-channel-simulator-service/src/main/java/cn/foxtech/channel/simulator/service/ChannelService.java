package cn.foxtech.channel.simulator.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.common.constant.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.vo.PublicRequestVO;
import cn.foxtech.common.domain.vo.PublicRespondVO;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.core.exception.ServiceException;
import lombok.AccessLevel;
import lombok.Getter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelService extends ChannelServerAPI {
    /**
     * 串口名-串口映射表
     */
    private final Map<String, String> recv2rspd = new ConcurrentHashMap<>();
    private final Map<String, String> recv2rsrd = new ConcurrentHashMap<>();
    /**
     * 串口名-串口映射表
     */
    @Getter(value = AccessLevel.PUBLIC)
    private final Map<String, String> channel2event = new ConcurrentHashMap();

    @Autowired
    private ChannelProperties constants;

    @Autowired
    private ExecuteService executeService;

    /**
     * 重新打开串口
     */
    public void initService() {
        try {
            // 读取JSON格式的配置文件
            File file = new File("");
            String confFileName = file.getAbsolutePath() + "/conf/fox-edge-server-channel-simulator.conf";

            String jsonData = new String(Files.readAllBytes(Paths.get(confFileName)));

            JSONObject parse = JSONObject.fromObject(jsonData);
            JSONArray array = parse.getJSONArray("channel_simulator");
            for (Object object : array) {
                // 读取文件参数
                JSONObject at = (JSONObject) object;
                String name = (String) at.get("name");
                String recv = (String) at.get("recv");
                String rspd = (String) at.get("rspd");
                String rsrd = (String) at.get("rsrd");

                String channelName = (String) at.get("channel_name");
                String event = (String) at.get("event");

                if (recv != null && rspd != null) {
                    // 格式化数据
                    recv = HexUtils.byteArrayToHexString(HexUtils.hexStringToByteArray(recv)).toUpperCase();
                    rspd = HexUtils.byteArrayToHexString(HexUtils.hexStringToByteArray(rspd)).toUpperCase();
                    this.recv2rspd.put(recv, rspd);
                }
                if (recv != null && rsrd != null) {
                    recv = HexUtils.byteArrayToHexString(HexUtils.hexStringToByteArray(recv)).toUpperCase();
                    rsrd = HexUtils.byteArrayToHexString(HexUtils.hexStringToByteArray(rsrd)).toUpperCase();
                    this.recv2rsrd.put(recv, rsrd);
                }
                if (channelName != null && event != null) {
                    // 格式化数据
                    event = HexUtils.byteArrayToHexString(HexUtils.hexStringToByteArray(event)).toUpperCase();

                    this.channel2event.put(channelName, event);
                }
            }
        } catch (IOException e) {
            System.out.print(e);
        }
    }

    /**
     * 执行主从半双工操作
     *
     * @param requestVO 请求报文
     * @return 返回的json报文
     * @throws ServiceException 异常信息
     */
    @Override
    public ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        return this.executeService.execute(recv2rspd, recv2rsrd, requestVO);
    }

    /**
     * 设备的主动上报消息
     *
     * @return 上报消息
     * @throws ServiceException 异常信息
     */
    @Override
    public List<ChannelRespondVO> receive() throws ServiceException {
        return this.executeService.receive(constants.getChannelType(), this.channel2event);
    }

    /**
     * 获得资源的信息
     *
     * @return 资源信息
     * @throws ServiceException 异常信息
     */
    @Override
    public PublicRespondVO getChannelNameList(PublicRequestVO requestVO) throws ServiceException {
        PublicRespondVO respondVO = new PublicRespondVO();
        respondVO.bindResVO(requestVO);

        List<String> channelNameList = new ArrayList<>();
        channelNameList.add("channel-simulator");
        respondVO.setData(channelNameList);

        return respondVO;
    }
}
