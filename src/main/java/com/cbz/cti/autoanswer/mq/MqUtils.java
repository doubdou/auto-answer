package com.cbz.cti.autoanswer.mq;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 * mq 工具
 * @author jinzw
 * @date 2020-10-17 16:41
 */
//@Component
public class MqUtils {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.topic}")
    String mqTopic;
    public void sendMsg(Object obj){
        //发送mq到数据库
        rocketMQTemplate.convertAndSend(mqTopic,obj);
    }

}
