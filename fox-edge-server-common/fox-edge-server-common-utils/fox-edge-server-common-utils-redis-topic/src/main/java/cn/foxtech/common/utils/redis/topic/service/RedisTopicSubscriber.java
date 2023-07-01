package cn.foxtech.common.utils.redis.topic.service;

/**
 * redis topic的订阅：由于bean对象是框架创建的，不能动态创建，只能采用预置了5个订阅的bean对象
 * 注意：
 * 1、每个bean对象，是否真正用于订阅，根据topic不为empt来确定
 * 2、优先用第一个订阅，其次是第二个订阅，以此类推
 * 3、使用时，从RedisTopicSubscriber派生一个类，并@Component注解
 */
public class RedisTopicSubscriber {
    public String topic1st() {
        return "";
    }

    public String topic2nd() {
        return "";
    }

    public String topic3rd() {
        return "";
    }

    public String topic4th() {
        return "";
    }

    public String topic5th() {
        return "";
    }

    public void receiveTopic1st(String message) {
        // default implementation ignored
    }

    public void receiveTopic2nd(String message) {
        // default implementation ignored
    }

    public void receiveTopic3rd(String message) {
        // default implementation ignored
    }

    public void receiveTopic4th(String message) {
        // default implementation ignored
    }

    public void receiveTopic5th(String message) {
        // default implementation ignored
    }
}
