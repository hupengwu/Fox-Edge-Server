package cn.foxtech.channel.opcua.handler;

import cn.foxtech.channel.opcua.entity.OpcUaChannelEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscriptionManager;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OpcUaHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(OpcUaHandler.class);

    private OpcUaChannelEntity channelEntity;

    private UaSubscriptionManager.SubscriptionListener listener;

    public OpcUaHandler(OpcUaChannelEntity channelEntity) {
        this.channelEntity = channelEntity;
    }

    @Override
    public void run() {
        this.createSubscription();
    }


    private void redoSubscription() {
        UaSubscriptionManager subscriptionManager = this.channelEntity.getOpcLink().getSubscriptionManager();

        // 获得旧订阅的ID
        Set<UInteger> ids = new HashSet<>();
        for (UaSubscription uaSubscription : subscriptionManager.getSubscriptions()) {
            ids.add(uaSubscription.getSubscriptionId());
        }

        // 逐个删除旧的订阅
        for (UInteger id : ids) {
            subscriptionManager.deleteSubscription(id);
        }

        // 重新订阅
        subscriptionManager.createSubscription(1000);
    }

    public void removeListener() {
        UaSubscriptionManager subscriptionManager = this.channelEntity.getOpcLink().getSubscriptionManager();

        // 获得旧订阅的ID
        Set<UInteger> ids = new HashSet<>();
        for (UaSubscription uaSubscription : subscriptionManager.getSubscriptions()) {
            ids.add(uaSubscription.getSubscriptionId());
        }

        // 逐个删除旧的订阅
        for (UInteger id : ids) {
            subscriptionManager.deleteSubscription(id);
        }

        if (this.listener == null) {
            return;
        }

        // 删除监听器
        subscriptionManager.removeSubscriptionListener(this.listener);
    }


    private void createSubscription() {
        UaSubscriptionManager subscriptionManager = this.channelEntity.getOpcLink().getSubscriptionManager();

        // 添加监听线程
        this.listener = new CustomSubscriptionListener();
        subscriptionManager.addSubscriptionListener(this.listener);

        // 订阅事件
        subscriptionManager.createSubscription(1000);
    }

    /**
     * 自定义订阅监听
     */
    private class CustomSubscriptionListener implements UaSubscriptionManager.SubscriptionListener {
        @Override
        public void onKeepAlive(UaSubscription subscription, DateTime publishTime) {
            //logger.info("opcUa监听：onKeepAlive（心跳激活） " + channelEntity.getChannelName());

            // 记录心跳时间
            channelEntity.setActiveTime(System.currentTimeMillis());
        }

        @Override
        public void onStatusChanged(UaSubscription subscription, StatusCode status) {
            logger.info("opcUa监听：onStatusChanged（连接状态改变） " + channelEntity.getChannelName());
        }

        @Override
        public void onPublishFailure(UaException exception) {
            logger.info("opcUa监听：onPublishFailure（连接断开） " + channelEntity.getChannelName());
        }

        @Override
        public void onNotificationDataLost(UaSubscription subscription) {
            logger.info("opcUa监听：onNotificationDataLost（数据丢失） " + channelEntity.getChannelName());
        }

        /**
         * 重连时 尝试恢复之前的订阅失败时 会调用此方法
         *
         * @param uaSubscription 订阅
         * @param statusCode     状态
         */
        @Override
        public void onSubscriptionTransferFailed(UaSubscription uaSubscription, StatusCode statusCode) {
            logger.info("opcUa监听：正在重新启动订阅 " + channelEntity.getChannelName());

            // 重新发起订阅
            redoSubscription();
        }
    }
}